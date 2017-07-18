package it.infocert.eigor.api;



public class ConversionIssue implements IConversionIssue {

    /** The message describing the issue. */
    private final String message;

    /** The exception that caused the problem. */
    private final Exception cause;

    /** If {@literal true} this has to be considered fatal, meaning it prevents the conversion to be completed. */
    private final boolean fatal;

    /** Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about a warning caused by the given exception. */
    public static IConversionIssue newWarning(Exception e) {
        return new ConversionIssue(null, e, false);
    }

    /** Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about a warning caused by the given exception. */
    public static IConversionIssue newWarning(Exception e, String message) {
        return new ConversionIssue(message, e, false);
    }

    /** Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about an error caused by the given exception. */
    public static IConversionIssue newError(Exception e) {
        return new ConversionIssue(null, e, true);
    }

    /** Create a new {@link it.infocert.eigor.api.ConversionIssue issue} about an error caused by the given exception. */
    public static IConversionIssue newError(Exception e, String message) {
        return new ConversionIssue(message, e, true);
    }

    private ConversionIssue(String message, Exception cause, boolean fatal) {
        this.message = message;
        this.cause = cause;
        this.fatal = fatal;
    }

    @Override
    public String getMessage() {
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

}
