package it.infocert.eigor.api.conversion;

/**
 * Specify a problem occurred during conversion through a type conversion.
 */
public abstract class ConversionFailedException extends Exception {

    public ConversionFailedException() {
    }

    public ConversionFailedException(String s) {
        super(s);
    }

    public ConversionFailedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConversionFailedException(Throwable throwable) {
        super(throwable);
    }
}
