package it.infocert.eigor.api;

/**
 * An exception to be thrown when a syntax error is found in a mapping file for conversion.
 */
public class SyntaxErrorInMappingFileException extends Exception{
    public SyntaxErrorInMappingFileException() {
    }

    public SyntaxErrorInMappingFileException(String message) {
        super(message);
    }

    public SyntaxErrorInMappingFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyntaxErrorInMappingFileException(Throwable cause) {
        super(cause);
    }
}
