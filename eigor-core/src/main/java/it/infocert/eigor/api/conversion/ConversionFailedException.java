package it.infocert.eigor.api.conversion;

public class ConversionFailedException extends Exception {

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
