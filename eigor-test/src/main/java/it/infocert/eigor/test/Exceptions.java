package it.infocert.eigor.test;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utils to work with exceptions.
 */
public final class Exceptions {

    /**
     * The stack trace of an exception in a {@link String string}.
     */
    public static String stackTrace(Exception e) {
        if(e == null) return "";
        StringWriter ss = new StringWriter(0);
        e.printStackTrace(new PrintWriter(ss));
        return ss.toString();
    }

    private Exceptions(){
        throw new UnsupportedOperationException("Cannot be instatiated.");
    }

}
