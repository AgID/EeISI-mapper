package it.inocert.eigor.cli;

import it.infocert.eigor.test.Files;
import it.infocert.eigor.test.OS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static it.infocert.eigor.test.Failures.failForException;
import static it.infocert.eigor.test.Files.newFile;
import static it.infocert.eigor.test.Files.unzip;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class ITCli {

    @Rule public TemporaryFolder tmp = new TemporaryFolder();

    @Test public void shouldUnzipAndRunOnWindows() throws Exception {

        assumeTrue(OS.isWindows());

        File workdir = tmp.newFolder("workdir");
        File outputFolder = newFile(workdir, "output");
        assertTrue( outputFolder.mkdirs() );

        File eigorCliZipped = moveEigorZipFile(workdir);
        unzip(eigorCliZipped, workdir);

        // prepare invocation
        String args =
                "--input "
                        + newFile(workdir , "eigor-cli" , "examples" , "cen-a7-minimum-content-with-std-values.csv")
                        + " --output "
                        + outputFolder
                        + " --source " + "csvcen" + " --target " + "cenfattpa";
        File batToRun = newFile(workdir , "eigor-cli" , "eigor.bat");

        // run eigor cli
        final AtomicReference<Process> proc = new AtomicReference<>();
        Thread runningThread = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Runtime runtime = Runtime.getRuntime();
                    String command = "cmd /c " + batToRun + " " + args;
                    proc.set( runtime.exec(command) );
                } catch (IOException e) {
                    failForException(e);
                }
            }
        });
        runningThread.start();

        // wait for it to terminate
        int timeoutToGetTheProcess = 5000;
        int timeoutToProcessCompletion = 25000;
        long started = System.currentTimeMillis();
        do {
            Thread.sleep(100);
        } while (proc.get() == null && System.currentTimeMillis() - started < timeoutToGetTheProcess);
        if(proc.get() == null){
            runningThread.interrupt();
            Assert.fail("process didn't finished after " + timeoutToGetTheProcess + " millis");
        }
        proc.get().waitFor(timeoutToProcessCompletion, TimeUnit.MILLISECONDS);
        proc.get().destroyForcibly();

        // uncomment to check for program output
        // BufferedInputStream out = new BufferedInputStream(proc.get().getInputStream());
        // BufferedInputStream err = new BufferedInputStream(proc.get().getErrorStream());
        // System.out.println( IOUtils.toString(out) );
        // System.out.println( IOUtils.toString(err) );

        assertThat( proc.get().exitValue(), is(1) );
        String[] producedFiles = outputFolder.list();
        assertThat("Output folder '" + outputFolder.getAbsolutePath() + "' was supposed to contain some files, but it don't.", producedFiles.length, greaterThan(0) );
        assertThat(asList(producedFiles), hasItems( "fromcen-errors.csv", "invoice-cen.csv", "invoice-source.csv", "invoice-target.xml", "invoice-transformation.log", "rule-report.csv" ) );

    }

    @Test public void shouldUnzipAndRunOnUnix() throws Exception {

        assumeTrue(OS.isUnix());

        File workdir = tmp.newFolder("workdir");
        File outputFolder = newFile(workdir, "output");
        assertTrue( outputFolder.mkdirs() );

        File eigorCliZipped = moveEigorTarGzFile(workdir);
        Files.untar(eigorCliZipped, workdir);

        // prepare invocation
        String args =
                "--input "
                        + newFile(workdir , "eigor-cli" , "examples" , "cen-a7-minimum-content-with-std-values.csv")
                        + " --output "
                        + outputFolder
                        + " --source " + "csvcen" + " --target " + "cenfattpa";
        File batToRun = newFile(workdir , "eigor-cli" , "eigor.sh");
        Files.setPermission(batToRun, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ);

        // run eigor cli
        final AtomicReference<Process> proc = new AtomicReference<>();
        Thread runningThread = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Runtime runtime = Runtime.getRuntime();
                    String[] command = new String[]{
                            "bash", "-c", "cd " + batToRun.getParentFile().getAbsolutePath() + ";" + "./" + batToRun.getName() + " " + args };
                    proc.set( runtime.exec(command) );
                    } catch (IOException e) {
                    failForException(e);
                }
            }
        });
        runningThread.start();

        // wait for it to terminate
        int timeoutToGetTheProcess = 5000;
        int timeoutToProcessCompletion = 25000;
        long started = System.currentTimeMillis();
        do {
            Thread.sleep(100);
        } while (proc.get() == null && System.currentTimeMillis() - started < timeoutToGetTheProcess);
        if(proc.get() == null){
            runningThread.interrupt();
            Assert.fail("process didn't finished after " + timeoutToGetTheProcess + " millis");
        }
        proc.get().waitFor(timeoutToProcessCompletion, TimeUnit.MILLISECONDS);

        // uncomment to check for program output
//        BufferedInputStream out = new BufferedInputStream(proc.get().getInputStream());
//        BufferedInputStream err = new BufferedInputStream(proc.get().getErrorStream());
//        System.out.println( IOUtils.toString(out) );
//        System.out.println( IOUtils.toString(err) );
//        System.exit(1);



        assertThat( proc.get().exitValue(), is(0) );

        proc.get().destroyForcibly();


        String[] producedFiles = outputFolder.list();
        assertThat("Output folder '" + outputFolder.getAbsolutePath() + "' was supposed to contain some files, but it don't.", producedFiles.length, greaterThan(0) );
        assertThat(asList(producedFiles), hasItems( "fromcen-errors.csv", "invoice-cen.csv", "invoice-source.csv", "invoice-target.xml", "invoice-transformation.log", "rule-report.csv" ) );



    }

    private File moveEigorZipFile(File destinationFolder) throws IOException {
        File eigorCliZipped = newFile("target", "eigor.zip");
        return moveFile(destinationFolder, eigorCliZipped);
    }

    private File moveEigorTarGzFile(File destinationFolder) throws IOException {
        File eigorCliZipped = newFile("target", "eigor.tar.gz");
        return moveFile(destinationFolder, eigorCliZipped);
    }

    private File moveFile(File destinationFolder, File fileToMove) throws IOException {
        assertTrue( "File " + fileToMove.getAbsolutePath() + " does not exists, but it should.", fileToMove.exists() );
        FileUtils.copyFileToDirectory(fileToMove, destinationFolder);
        fileToMove = new File(destinationFolder, fileToMove.getName());
        assertTrue( "File " + fileToMove.getAbsolutePath() + " does not exists, but it should.", fileToMove.exists() );
        return fileToMove;
    }

}
