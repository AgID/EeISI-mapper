package it.infocert.eigor.api;

import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * Convert a {@link BG0000Invoice CEN invoice} into another format.
 * <h2>General Contract</h2>
 * <p>
 *     Implementations are required to perform syntax validation of the produced format.
 *     In case a syntax error occurs, implementations are required to throw a SyntaxErrorInInvoiceFormatException.
 * </p>
 * <p>
 *     Implementations are not required to perform any validation on the provided
 *     {@link BG0000Invoice CEN invoice}.
 * </p>
 * @see ToCenConversion
 */
public interface FromCenConversion {

    /**
     * @throws SyntaxErrorInInvoiceFormatException If a syntax error occurs in the generated invoice format.
     */
    byte[] convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException;

    /**
     * Whether the given format is supported or not.
     */
    boolean support(String format);

    /**
     * The list of supported formats.
     * If one of this format is passed to {@link ToCenConversion#support(String)} it should return {@code true}.
     */
    String getSupportedFormats();
}
