package it.infocert.eigor.api;

import it.infocert.eigor.api.configuration.Configurable;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.io.InputStream;
import java.util.Set;

/**
 * Convert an invoice in a {@link BG0000Invoice CEN invoice}.
 * <h2>General Contract</h2>
 * <p>
 *     Implementations are required to perform syntax validation of the source invoice.
 *     In case a syntax error occurs, implementations are required to throw a SyntaxErrorInInvoiceFormatException.
 * </p>
 * <p>
 *     Implementations are not required to perform any validation on the obtained
 *     {@link BG0000Invoice CEN invoice}.
 * </p>
 */
public interface ToCenConversion extends Named, Configurable {

    /**
     * Convert the given invoice in a {@link BG0000Invoice CEN invoice}.
     * @param sourceInvoiceStream The stream containing the representation of the invoice to be converted.
     * @return The {@link BG0000Invoice CEN invoice}.
     * @throws SyntaxErrorInInvoiceFormatException   When a syntax error that makes impossible
     *                                  to convert the source invoice in the CEN format is found.
     */
    ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException;
    
    /**
     * Returns true if the format is supported, false otherwise
     */
    boolean support(String format);

    /**
     * The list of supported formats.
     * If one of this format is passed to {@link ToCenConversion#support(String)} it should return {@code true}.
     */
    Set<String> getSupportedFormats();
}
