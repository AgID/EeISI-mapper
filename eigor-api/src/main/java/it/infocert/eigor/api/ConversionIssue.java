package it.infocert.eigor.api;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

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
