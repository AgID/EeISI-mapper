package it.infocert.eigor.api.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Copier {

    private final File dest;

    public Copier(File dest) {
        this.dest = dest;
    }

    public void copyFrom(String resource) {
        boolean done = false;
        try{
            copyFromJar(resource);
            done = true;
        }catch(Exception e1){
            try{
                copyFromClasspath(resource);
                done = true;
            }catch(Exception e2){

            }
        }
        if(!done) throw new IllegalArgumentException("Unable to copy from '" + resource + "'.");
    }

    public void copyFromClasspath(String resourceDir) throws IOException {
        URL resource = getClass().getResource(resourceDir);
        File sourceDir = new File(resource.getFile());
        FileUtils.copyDirectoryToDirectory(sourceDir, dest);
    }

    public void copyFromJar(String s) throws IOException {
        URL resource = getClass().getResource(s);

        String fullPath = resource.toString();

        // jar:file:/workspace/repos/infocert/eigor/eigor-api/src/test/test-jar/cii-schematron.jar!/converterdata/converter-cii-cen/cii/xsd/uncoupled/data/standard/


        if(!fullPath.contains(".jar!")) throw new IllegalArgumentException("Not a jar: " + fullPath);

        String[] split = fullPath.split("\\.jar!/");
        String pathOfJar = (split[0] + ".jar").substring("jar:file:".length());
        String pathInJar = split[1];

        JarFile jarFile = new JarFile(pathOfJar);

        JarEntry item = null;

        Enumeration<JarEntry> entries = jarFile.entries();
        while(entries.hasMoreElements()){
            JarEntry jarEntry = entries.nextElement();

            if(jarEntry.getName().startsWith(pathInJar)) {
                if(jarEntry.isDirectory()){



                    String fullJarPath = jarEntry.getName();
                    String relativePath = fullJarPath.substring( pathInJar.length() );

                    File directory = new File(dest, relativePath);
                    directory.mkdirs();

                }else{



                    String fullJarPath = jarEntry.getName();
                    String relativePath = fullJarPath.substring( pathInJar.length() );
                    File file = new File(dest, relativePath);
                    IOUtils.copy( jarFile.getInputStream(jarEntry), new FileOutputStream(file));

                }
            }
        }






    }
}
