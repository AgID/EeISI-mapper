package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorMessage;

/**
 * An exception to be thrown when a syntax error is found in a mapping file for conversion.
 */
public class SyntaxErrorInMappingFileException extends EigorException {
    private static final String NAME = "SyntaxErrorInMappingFile";
    public SyntaxErrorInMappingFileException(String message) {
        super(ErrorMessage.builder().message(message).error(NAME).build());
    }

    public SyntaxErrorInMappingFileException(String message, Throwable cause) {
        super(ErrorMessage.builder().message(message).error(NAME).build(), cause);
    }

    public SyntaxErrorInMappingFileException(Throwable cause) {
        super(ErrorMessage.builder().message(cause.getMessage()).error(NAME).build(), cause);
    }
}