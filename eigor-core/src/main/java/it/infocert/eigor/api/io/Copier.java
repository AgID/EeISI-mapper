package it.infocert.eigor.api.io;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Copier {

    private final Logger log = LoggerFactory.getLogger(Copier.class);

    private final File dest;

    private final Set<Callback> callbacks;

    public Copier(File dest) {
        this.dest = dest;
        callbacks = new LinkedHashSet<>();
    }

    public Copier withCallback(Callback callback){
        this.callbacks.add(callback);
        return this;
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

    public void copyFromClasspath2(String resourceDir) throws IOException {
        URL resource = getClass().getResource(resourceDir);
        File sourceDir = new File(resource.getFile());
        log.trace("Copying from filesystem dir '{}' to '{}'.", sourceDir.getAbsolutePath(), dest.getAbsolutePath());
        FileUtils.copyDirectoryToDirectory(sourceDir, dest);

        log.trace("Copied from filesystem dir '{}' to '{}'.", sourceDir.getAbsolutePath(), dest.getAbsolutePath());
    }

    public void copyFromClasspath(String resourceDir) throws IOException {
        URL resource = getClass().getResource(resourceDir);
        final File sourceDir = new File(resource.getFile());
        log.trace("Copying from filesystem dir '{}' to '{}'.", sourceDir.getAbsolutePath(), dest.getAbsolutePath());

        RecursiveNavigator.mirror(sourceDir, new RecursiveNavigator.CopyCallback(dest) {
            @Override protected void mirrorFolder(File sourceDir, File destDir) throws IOException {
                super.mirrorFolder(sourceDir, destDir);
                Copier.this.fireAfterFileCopied(destDir);
            }

            @Override protected void mirrorFile(File sourceFile, File destFile) throws IOException {
                super.mirrorFile(sourceFile, destFile);
                Copier.this.fireAfterFileCopied(destFile);
            }
        });

        log.trace("Copied from filesystem dir '{}' to '{}'.", sourceDir.getAbsolutePath(), dest.getAbsolutePath());
    }

    public void copyFromJar(String pathInJarAsStr) throws IOException {

        Preconditions.checkArgument(pathInJarAsStr!=null && !pathInJarAsStr.trim().isEmpty(), "Invalid path in jar.");

        URL resource = getClass().getResource(pathInJarAsStr);

        Preconditions.checkState(resource!=null, "Unable to find resource %s in JAR.", pathInJarAsStr);

        String fullPath = resource.toString();

        log.trace("Copying from jar dir '{}' to '{}'.", fullPath, dest.getAbsolutePath());

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
                    fireAfterFileCopied(file);

                }
            }


        }

        log.trace("Copied from jar dir '{}' to '{}'.", fullPath, dest.getAbsolutePath());
        jarFile.close();
    }

    private void fireAfterFileCopied(File file) throws IOException {
        for (Callback callback : callbacks) {
            callback.afterFileCopied(file);
        }
    }

    public static interface Callback {
        void afterFileCopied(File file) throws IOException;
    }

}
