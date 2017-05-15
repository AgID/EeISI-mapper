package it.inocert.eigor.cli;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static it.infocert.eigor.test.Failures.fail;
import static org.junit.Assert.assertTrue;

public class TestCli {

    @Rule public TemporaryFolder tmp = new TemporaryFolder();

    @Test public void shouldUnzip() throws Exception {

        File workdir = tmp.newFolder("workdir");
        File zippedFile = new File( "target" + File.separator + "eigor.zip" );

        assertTrue( "File " + zippedFile.getAbsolutePath() + " does not exists, but it should.", zippedFile.exists() );

        FileUtils.copyFileToDirectory(zippedFile, workdir);

        zippedFile = new File(workdir, zippedFile.getName());

        unZipIt(zippedFile, workdir);

        System.out.println(workdir);

        File batToRun = new File(workdir + File.separator + "eigor-cli" + File.separator + "eigor.bat");

        //Process exec = Runtime.getRuntime().exec("cmd /c start " + batToRun);
        final Process[] exec = { null };

        Thread runningThread = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Runtime runtime = Runtime.getRuntime();
                    exec[0] = runtime.exec("cmd /c " + batToRun);
                } catch (IOException e) {
                    fail(e);
                }
            }
        });
        runningThread.start();
        long started = System.currentTimeMillis();
        do {
            Thread.sleep(100);
        } while (exec[0] == null && System.currentTimeMillis() - started < 5000);

        if(exec[0] != null){
            System.out.println("completed!");
            exec[0].destroyForcibly();
            BufferedInputStream out = new BufferedInputStream(exec[0].getInputStream());
            BufferedInputStream err = new BufferedInputStream(exec[0].getErrorStream());

            System.out.println( IOUtils.toString(out) );
            System.out.println( IOUtils.toString(err) );
        }else{
            runningThread.interrupt();
        }


        System.exit(0);



    }

    /**
     * Unzip it
     * @param zipFile input zip file
     * @param folder zip file output folder
     */
    public void unZipIt(File zipFile, File folder) throws Exception {
        // inspired by http://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/

        ZipInputStream zis = null;
        FileOutputStream fos = null;
        ZipEntry ze = null;
        try{
            byte[] buffer = new byte[1024];


            //create output directory is not exists
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();

                if(ze.isDirectory()){
                    File newFolder = new File(folder, fileName);
                    newFolder.mkdirs();
                }else{
                    File newFile = new File(folder, fileName);

                    //create all non exists folders
                    //else you will hit FileNotFoundException for compressed folder
                    File file = new File(newFile.getParent());
                    file.mkdirs();

                    fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }


                ze = zis.getNextEntry();
            }


        }finally {
            if(zis!=null) zis.closeEntry();
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fos);
        }






    }
}
