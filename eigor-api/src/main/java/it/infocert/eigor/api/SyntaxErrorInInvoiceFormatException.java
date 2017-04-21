package it.infocert.eigor.api;

/**
 * An exception to be thrown when a syntax error is found in an invoice.
 */
public class SyntaxErrorInInvoiceFormatException extends Exception {
    public SyntaxErrorInInvoiceFormatException() {
    }

    public SyntaxErrorInInvoiceFormatException(String message) {
        super(message);
    }

    public SyntaxErrorInInvoiceFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyntaxErrorInInvoiceFormatException(Throwable cause) {
        super(cause);
    }
}
