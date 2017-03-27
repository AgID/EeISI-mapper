package it.infocert.eigor.model.core.datatypes;

import javax.activation.MimeType;

/**
 * A set of finite-length sequences of binary digits.
 */
public final class Binary {

    private final byte[] bytes;
    private final MimeType mimeType;
    private final String fileName;

    public Binary(byte[] bytes, MimeType mimeType, String fileName) {
        this.bytes = bytes;
        this.mimeType = mimeType;
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public String getFileName() {
        return fileName;
    }
}
