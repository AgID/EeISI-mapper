package it.infocert.eigor.model.core.datatypes;

import javax.activation.MimeType;

public class FileReference {
    private final String filePath;
    private final MimeType mimeType;
    private final String fileName;

    public FileReference(String filePath, MimeType mimeType, String fileName) {
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public String getFileName() {
        return fileName;
    }
}
