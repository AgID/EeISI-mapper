package it.infocert.eigor.api.errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErrorMessage {

    private final String message;
    private final ErrorCodes errorCode;
    private final List<Exception> relatedExceptions = new ArrayList<>(0);

    public ErrorMessage(String message, String location, String type, String code) {
        this(message, ErrorCodes.retrieveErrorCode(location, type, code));
    }

    public ErrorMessage(String message, ErrorCodes errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public ErrorMessage(ErrorMessage errorMessage, String message) {
        this(message, errorMessage.getErrorCode());
    }

    public ErrorMessage(Exception relatedException, String message, String location, String type, String code) {
        this(relatedException, message, ErrorCodes.retrieveErrorCode(location, type, code));
    }

    public ErrorMessage(Exception relatedException, String message, ErrorCodes errorCode) {
        this(message, errorCode);
        this.relatedExceptions.add(relatedException);
    }

    public ErrorMessage(ErrorMessage errorMessage, Exception relatedException, String message) {
        this(relatedException, message, errorMessage.getErrorCode());
        this.relatedExceptions.addAll(errorMessage.relatedExceptions);
    }

    public String getMessage() {
        return message;
    }

    public ErrorCodes getErrorCode() {
        return errorCode;
    }


    public List<Exception> getRelatedExceptions() {
        return relatedExceptions;
    }

    public Exception getRelatedException(int index) {
        return relatedExceptions.get(index);
    }

    public boolean hasRelatedExceptions() {
        return !relatedExceptions.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s - %s", errorCode, message);
    }

}
