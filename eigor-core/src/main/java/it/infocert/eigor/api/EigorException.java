package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;

public class EigorException extends Exception {
    
    private final ErrorMessage errorMessage;

    public EigorException(String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error error, Pair<String, String>... parameters) {
        this(new ErrorMessage(message, location, action, error, parameters));
    }

    public EigorException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public EigorException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }

    public EigorException(ErrorMessage errorMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMessage.getMessage(), cause, enableSuppression, writableStackTrace);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
