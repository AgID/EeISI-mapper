package it.infocert.eigor.test;

import org.junit.Assert;

import static it.infocert.eigor.test.Exceptions.stackTrace;

public final class Failures {

    /**
     * Fails because of an unexpected exception.
     */
    public static void failForException(Exception unexpectedException){
        Assert.fail( "Unexpected exception!\n" + stackTrace(unexpectedException) );
    }

    private Failures() {
        throw new UnsupportedOperationException("Cannot be instatiated.");
    }

}
