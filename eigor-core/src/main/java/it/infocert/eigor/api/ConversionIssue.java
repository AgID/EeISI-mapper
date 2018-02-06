package it.infocert.eigor.api;


import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;

public class ConversionIssue implements IConversionIssue {

    /**
     * The message describing the issue.
     */
    private final ErrorMessage errorMessage;

    /**
     * The exception that caused the problem.
     */
    private final Exception cause;

    /**
     * If {@literal true} this has to be considered fatal, meaning it prevents the conversion to be completed.
     */
    private final boolean fatal;

    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about an error caused by the given exception.
     */
    public static ConversionIssue newError(EigorException e) {
        return new ConversionIssue(e.getErrorMessage(), e, true);
    }

    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about an error caused by the given exception.
     */
    public static ConversionIssue newError(EigorRuntimeException e) {
        return new ConversionIssue(e.getErrorMessage(), e, true);
    }

    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about an error caused by the given exception and error code.
     */
    @SafeVarargs
    public static ConversionIssue newError(Exception e, String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error error, Pair<String, String>... parameters) {
        return new ConversionIssue(new ErrorMessage(message, location, action, error, parameters), e, true);
    }

    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about a warning caused by the given exception.
     */
    public static ConversionIssue newWarning(EigorException e) {
        return new ConversionIssue(e.getErrorMessage(), e, false);
    }

    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about a warning caused by the given exception.
     */
    public static ConversionIssue newWarning(EigorRuntimeException e) {
        return new ConversionIssue(e.getErrorMessage(), e, false);
    }

    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about a warning caused by the given exception.
     */
    public static ConversionIssue newWarning(Exception e, ErrorMessage m) {
        return new ConversionIssue(m, e, false);
    }


    /**
     * Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about a warning caused by the given exception and error code.
     */
    @SafeVarargs
    public static ConversionIssue newWarning(Exception e, String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error error, Pair<String, String>... parameters) {
        return new ConversionIssue(new ErrorMessage(message, location, action, error, parameters), e, false);
    }

    private ConversionIssue(ErrorMessage message, Exception cause, boolean fatal) {
        this.errorMessage = message;
        this.cause = cause;
        this.fatal = fatal;
    }

    private ConversionIssue(String message, Exception cause, boolean fatal) {
        this(new ErrorMessage(message), cause, fatal);
    }

    @Override
    public String getMessage() {
        String message = errorMessage.toString();
        if (message == null) {

            StringBuilder sb = new StringBuilder();
            sb.append(cause.getMessage());
            // Please keep it, used for debugging.
//            if(cause!=null){
//                StringWriter sw = new StringWriter();
//                cause.printStackTrace( new PrintWriter(sw));
//                sb.append("\n").append(sw.toString());
//            }

            return sb.toString();
        }

        return message;
    }

    @Override
    public Exception getCause() {
        return cause;
    }

    @Override
    public boolean isError() {
        return fatal;
    }

    @Override
    public boolean isWarning() {
        return !fatal;
    }

    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return String.format("%s %s Fatal: %s", errorMessage, cause, fatal);
    }
}
