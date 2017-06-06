package it.inocert.eigor.cli;

import it.infocert.eigor.test.Files;
import it.infocert.eigor.test.OS;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static it.infocert.eigor.test.Failures.failForException;
import static it.infocert.eigor.test.Files.newFile;
import static it.infocert.eigor.test.Files.unzip;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class ITCli {

    @Rule public TemporaryFolder tmp = new TemporaryFolder();
    @Rule public TestName testName = new TestName();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Test(timeout = 50000) public void shouldUnzipAndRunOnWindowsWithForce() throws Exception {

        assumeTrue("Run only on Win.", OS.isWindows());

        log.error("Starting test {} on win.", testName.getMethodName());

        File workdir = tmp.newFolder("workdir");
        File outputFolder = newFile(workdir, "output");
        assertTrue( outputFolder.mkdirs() );

        // prepare invocation
        final String args =
                "--input "
                        + newFile(workdir , "eigor-cli" , "examples" , "cen-a7-minimum-content-with-std-values.csv")
                        + " --output "
                        + outputFolder
                        + " --source " + "csvcen" + " --target " + "fatturapa" + " --force"; // FIXME maybe remove --force once conversion is free of errors

        Process process = executeTheCli(workdir, args);

        assertThat( process.exitValue(), is(0) );
        String[] producedFiles = outputFolder.list();
        assertThat("Output folder '" + outputFolder.getAbsolutePath() + "' was supposed to contain some files, but it don't.", producedFiles.length, greaterThan(0) );
        assertThat(asList(producedFiles), hasItems( "fromcen-errors.csv", "invoice-cen.csv", "invoice-source.csv", "invoice-target.xml", "invoice-transformation.log", "rule-report.csv" ) );

    }

    @Test(timeout = 50000) public void shouldUnzipAndRunOnWindowsWithoutForcing() throws Exception {

        assumeTrue("Run only on Win.", OS.isWindows());

        log.error("Starting test {} on win.", testName.getMethodName());

        File workdir = tmp.newFolder("workdir");
        File outputFolder = newFile(workdir, "output");
        assertTrue( outputFolder.mkdirs() );

        // prepare invocation
        final String args =
                "--input "
                        + newFile(workdir , "eigor-cli" , "examples" , "cen-a7-minimum-content-with-std-values.csv")
                        + " --output "
                        + outputFolder
                        + " --source " + "csvcen" + " --target " + "fatturapa";

        Process process = executeTheCli(workdir, args);

        assertThat( process.exitValue(), is(0) );
        String[] producedFiles = outputFolder.list();
        assertThat("Output folder '" + outputFolder.getAbsolutePath() + "' was supposed to contain some files, but it don't.", producedFiles.length, greaterThan(0) );
        assertThat(asList(producedFiles), hasItems( "fromcen-errors.csv", "invoice-cen.csv", "invoice-source.csv", "invoice-transformation.log", "rule-report.csv" ) );
        assertThat("Target invoice cannot be produced, because it is the last step and --force was not provded.", asList(producedFiles), not(contains("invoice-target.xml")) );

    }

    private Process executeTheCli(File workdir, final String args) throws Exception {
        File eigorCliZipped = moveEigorZipFile(workdir);
        log.error("Eigor zip file available at '{}'.", eigorCliZipped.getAbsolutePath());

        unzip(eigorCliZipped, workdir);

        final File batToRun = newFile(workdir , "eigor-cli" , "eigor.bat");

        // run eigor cli
        final AtomicReference<Process> proc = new AtomicReference<>();
        Thread runningThread = new Thread(new Runnable() {
            @Override public void run() {
                String command = "cmd /c " + batToRun + " " + args;
                try {
                    Runtime runtime = Runtime.getRuntime();
                    log.error("Executing '{}'", command);

                    final Process exec = runtime.exec(command);

                    // immediately consumes process streams to avoid it blocks!
                    // see for instance: https://stackoverflow.com/questions/5483830/process-waitfor-never-returns
                    new Thread(new Runnable() {
                        @Override public void run() {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                            String line;
                            StringBuffer sb = new StringBuffer();
                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line + "\n");
                                }
                            } catch (IOException e) {
                                log.error("Error", e);
                            }
                            log.error( "\nout stream =================\n{}\n===========================", sb.toString() );
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override public void run() {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
                            String line;
                            StringBuffer sb = new StringBuffer();
                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line + "\n");
                                }
                            } catch (IOException e) {
                                log.error("Error", e);
                            } finally {

                            }
                            log.error( "\nerr stream =================\n{}\n===========================", sb.toString() );
                        }
                    }).start();


                    proc.set(exec);
                    log.error("Terminated '{}'", command);
                } catch (IOException e) {
                    log.error("Exception caught while executing '{}'", command, e);
                    failForException(e);
                } finally {
                    log.error("Finished '{}'", command);
                }
            }
        });
        log.error("Starting execution of eigor in thread '{}'.", runningThread);
        runningThread.start();
        log.error("Started execution of eigor in thread '{}'.", runningThread);

        // wait for it to terminate
        int timeoutToGetTheProcess = 50000;

        long timeSinceLastStatusDump = 0;
        long started = System.currentTimeMillis();

        Process process = proc.get();
        do {
            int timeBetweenChecks = 100;
            Thread.sleep(timeBetweenChecks);
            timeSinceLastStatusDump += timeBetweenChecks;
            if(timeSinceLastStatusDump>=2000){
                log.error("After {}ms still waiting for process to complete. Process is {}.", System.currentTimeMillis() - started, process);
                timeSinceLastStatusDump = 0;
            }
            process = proc.get();
        } while (process == null && System.currentTimeMillis() - started < timeoutToGetTheProcess);

        if(process == null){
            runningThread.interrupt();
            Assert.fail("process didn't finished after " + timeoutToGetTheProcess + " millis");
        }

        log.error("Waiting for process {}", valueOf( process ));
        process.waitFor();

        log.error("Destroying process {}", valueOf( process ));
        process.destroy();
        return process;
    }

    @Test(timeout = 50000) public void shouldUnzipAndRunOnUnix() throws Exception {

        assumeTrue("Run only on linux.", OS.isUnix());

        File workdir = tmp.newFolder("workdir");
        File outputFolder = newFile(workdir, "output");
        assertTrue( outputFolder.mkdirs() );

        File eigorCliZipped = moveEigorTarGzFile(workdir);
        Files.untar(eigorCliZipped, workdir);

        // prepare invocation
        final String args =
                "--input "
                        + newFile(workdir , "eigor-cli" , "examples" , "cen-a7-minimum-content-with-std-values.csv")
                        + " --output "
                        + outputFolder
                        + " --source " + "csvcen" + " --target " + "fatturapa" + " --force"; // FIXME maybe remove --force once conversion is free of errors


        final File batToRun = newFile(workdir , "eigor-cli", "eigor.sh"); //createNewFileUnix(workdir , "eigor-cli" , "eigor.sh");
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
        proc.get().waitFor();

        // uncomment to check for program output
//        BufferedInputStream out = new BufferedInputStream(proc.get().getInputStream());
//        BufferedInputStream err = new BufferedInputStream(proc.get().getErrorStream());
//        System.out.println( IOUtils.toString(out) );
//        System.out.println( IOUtils.toString(err) );
//        System.exit(1);



        assertThat( proc.get().exitValue(), is(0) );
        proc.get().destroy();


        String[] producedFiles = outputFolder.list();
        assertThat("Output folder '" + outputFolder.getAbsolutePath() + "' was supposed to contain some files, but it don't.", producedFiles.length, greaterThan(0) );
        assertThat(asList(producedFiles), hasItems( "fromcen-errors.csv", "invoice-cen.csv", "invoice-source.csv", "invoice-target.xml", "invoice-transformation.log", "rule-report.csv" ) );



    }

    private File moveEigorZipFile(File destinationFolder) throws IOException {
        File eigorCliZipped = newFile("target", "eigor.zip");
        return moveFile(destinationFolder, eigorCliZipped);
    }

    private File createNewFileUnix(Object target, String... pathComponents) throws IOException {
        File file = null;
        if (target instanceof String) {
            file = newFile((String)target, pathComponents);
        } else if (target instanceof File) {
            file = newFile((File)target, pathComponents);
        } else {
            throw new IOException("Failed to create " + Arrays.toString(pathComponents) + " on Linux machine! Invalid File descriptor!");
        }
        if (!file.exists() &&
                !file.createNewFile()) {
            throw new IOException("Failed to create " + Arrays.toString(pathComponents) + " on Linux machine!");
        }
        return file;
    }
    private File moveEigorTarGzFile(File destinationFolder) throws IOException {
        File eigorCliZipped = newFile("target", "eigor.tar.gz");//createNewFileUnix("target", "eigor.zip");
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
