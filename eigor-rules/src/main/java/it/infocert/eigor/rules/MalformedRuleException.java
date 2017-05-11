package it.infocert.eigor.rules;

public class MalformedRuleException extends RuntimeException{

    public MalformedRuleException() {
    }

    public MalformedRuleException(String message) {
        super(message);
    }

    public MalformedRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedRuleException(Throwable cause) {
        super(cause);
    }

    public MalformedRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
