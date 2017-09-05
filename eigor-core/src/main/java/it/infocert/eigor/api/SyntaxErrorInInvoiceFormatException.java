package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorMessage;

/**
 * An exception to be thrown when a syntax error is found in an invoice.
 */
public class SyntaxErrorInInvoiceFormatException extends EigorException {

    public SyntaxErrorInInvoiceFormatException(String message) {
        super(new ErrorMessage(message));
    }

    public SyntaxErrorInInvoiceFormatException(String message, Throwable cause) {
        super(new ErrorMessage(message), cause);
    }

    public SyntaxErrorInInvoiceFormatException(Throwable cause) {
        super(new ErrorMessage(cause.getMessage()), cause);
    }
}
