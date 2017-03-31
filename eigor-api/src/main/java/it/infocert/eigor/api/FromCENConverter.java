package it.infocert.eigor.api;

import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * Convert a {@link BG0000Invoice CEN invoice} into another format.
 * <h2>General Contract</h2>
 * <p>
 *     Implementations are required to perform syntax validation of the produced format.
 *     In case a syntax error occurs, implementations are required to throw a SyntaxInvoiceException.
 * </p>
 * <p>
 *     Implementations are not required to perform any validation on the provided
 *     {@link BG0000Invoice CEN invoice}.
 * </p>
 * @see ToCENConversion
 */
public interface FromCENConverter {
    byte[] convert(BG0000Invoice cenInvoice) throws SyntaxInvoiceException;

    boolean support(String format);
}
