package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;

/**
 * An exception to be thrown when a syntax error is found in a mapping file for conversion.
 */
public class SyntaxErrorInMappingFileException extends EigorException {
    private static final ErrorCode.Error NAME = ErrorCode.Error.INVALID;
    public SyntaxErrorInMappingFileException(String message) {
        super(ErrorMessage.builder().message(message).error(NAME).build());
    }

    public SyntaxErrorInMappingFileException(String message, Throwable cause) {
        super(ErrorMessage.builder().message(message).error(NAME).build(), cause);
    }

    public SyntaxErrorInMappingFileException(ErrorMessage message) {
        super(message);
    }

    public SyntaxErrorInMappingFileException(ErrorMessage message, Throwable cause) {
        super(message, cause);
    }

    public SyntaxErrorInMappingFileException(Throwable cause) {
        super(ErrorMessage.builder().message(cause.getMessage()).error(NAME).build(), cause);
    }
}