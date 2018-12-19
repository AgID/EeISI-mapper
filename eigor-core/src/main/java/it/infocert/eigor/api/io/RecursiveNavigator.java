package it.infocert.eigor.api.io;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class RecursiveNavigator {

    public static void mirror(File sourceDir, MirrorCallback mirrorCallback) throws IOException {
        new RecursiveNavigator().visit(sourceDir, mirrorCallback);
    }

    private RecursiveNavigator(){
    }

    void visit(final File root, Callback callback) throws IOException {
        try {
            callback.onEntry(root);
            if (!root.isDirectory()) {
            } else {
                File[] files = root.listFiles();
                for (File containedFile : files) {
                    visit(containedFile, callback);
                }
            }
        }catch(Exception e){
            throw new IOException(e);
        }
    }

    public interface Callback {
        void onEntry(File file) throws Exception;
    }

    public static abstract class MirrorCallback implements Callback {
        private final File destDir;
        private File sourceDir;
        private Logger log = LoggerFactory.getLogger(this.getClass());

        public MirrorCallback(File destDir) {
            this.destDir = destDir;
        }

        @Override public void onEntry(File file) throws Exception {
            if(file.isDirectory()) onDirectory(file); else onFile(file);
        }

        private void onFile(File sourceFile) throws IOException {

            String sourceFilePath = sourceFile.getAbsolutePath();
            String sourceDirPath = sourceDir.getAbsolutePath();
            if(!sourceFilePath.startsWith(sourceDirPath)){
                throw new IllegalArgumentException(sourceFilePath + " and " + sourceDirPath);
            }
            File destFile = new File(destDir.getAbsolutePath() + sourceFile.getAbsolutePath().substring(sourceDirPath.length()));

            mirrorFile(sourceFile, destFile);

        }

        private void onDirectory(File sourceSubDir) throws IOException {

            if(sourceDir == null){
                sourceDir = sourceSubDir;
            }

            String sourceSubDirPath = sourceSubDir.getAbsolutePath();
            String sourceDirPath = sourceDir.getAbsolutePath();
            if(!sourceSubDirPath.startsWith(sourceDirPath)){
                throw new IllegalArgumentException(sourceDirPath + " and " + sourceSubDirPath);
            }
            File dirToCreate = new File(destDir.getAbsolutePath() + sourceSubDirPath.substring(sourceDirPath.length()));

            mirrorFolder(sourceSubDir, dirToCreate);

        }

        protected abstract void mirrorFolder(File sourceDir, File destDir) throws IOException;

        protected abstract void mirrorFile(File sourceFile, File destFile) throws IOException;
    }

    public static class CopyCallback extends MirrorCallback {

        private Logger log = LoggerFactory.getLogger(this.getClass());

        public CopyCallback(File destDir) {
            super(destDir);
        }

        @Override protected void mirrorFolder(File sourceDir, File destDir) throws IOException {
            log.trace("Mirroring folder '{}' to '{}'.", sourceDir.getAbsolutePath(), destDir.getAbsolutePath());
            destDir.mkdirs();
        }

        @Override protected void mirrorFile(File sourceFile, File destFile) throws IOException {
            log.trace("Copying file '{}' to '{}'.", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            FileUtils.copyFile(sourceFile, destFile);
        }
    }
}
