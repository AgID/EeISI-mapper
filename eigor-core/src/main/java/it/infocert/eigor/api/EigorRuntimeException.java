package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;

public class EigorRuntimeException extends RuntimeException {

    private ErrorMessage errorMessage;

    public EigorRuntimeException(String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error error) {
        this(new ErrorMessage(message, location, action, error));
    }

    public EigorRuntimeException(String message) {
        this(new ErrorMessage(message));
    }

    public EigorRuntimeException(String message, Throwable cause) {
        this(new ErrorMessage(message), cause);
    }

    public EigorRuntimeException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public EigorRuntimeException(String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error error, Throwable cause) {
        this(new ErrorMessage(message, new ErrorCode(location, action, error)), cause);
    }

    public EigorRuntimeException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }

    public EigorRuntimeException(Throwable cause, ErrorMessage errorMessage) {
        super(cause);
        this.errorMessage = errorMessage;
    }

    public EigorRuntimeException(ErrorMessage errorMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorMessage.getMessage(), cause, enableSuppression, writableStackTrace);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
