package it.infocert.eigor.api;


public class ConversionIssue {
    private final String message;
    private final Exception cause;
    private final Boolean fatal;

    private ConversionIssue(String message, Exception cause, boolean fatal) {
        this.message = message;
        this.cause = cause;
        this.fatal = fatal;
    }

    public String getMessage() {
        if (message == null) {
            return cause.getMessage();
        }
        return message;
    }

    public Exception getCause() {
        return cause;
    }

    public boolean isError() {
        return fatal;
    }

    public boolean isWarning() {
        return !fatal;
    }

    public static ConversionIssue newWarning(Exception e) {
        return new ConversionIssue(null, e, false);
    }

    public static ConversionIssue newError(Exception e) {
        return new ConversionIssue(null, e, true);
    }

    public static ConversionIssue newWarning(Exception e, String message) {
        return new ConversionIssue(message, e, false);

    }

    public static ConversionIssue newError(Exception e, String message) {
        return new ConversionIssue(message, e, true);

    }

}
