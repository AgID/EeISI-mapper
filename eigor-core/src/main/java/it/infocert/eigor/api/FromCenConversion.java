package it.infocert.eigor.api;

import it.infocert.eigor.api.configuration.Configurable;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.util.Set;

/**
 * Convert a {@link BG0000Invoice CEN invoice} into another format.
 * <h2>General Contract</h2>
 * <p>
 *     Implementations are not required to perform any validation on the provided
 *     {@link BG0000Invoice CEN invoice}.
 * </p>
 * @see ToCenConversion
 */
public interface FromCenConversion extends Named, Configurable {

    /**
     * The return object should contain a byte[] result and a not null but possible empty array of issues
     * BinaryConversionResult is immutable, once created with the result and issues parameters, cannot be changed
     * @param invoice
     * @return {@link BinaryConversionResult BinaryConversionResult}
     */
    BinaryConversionResult convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException;

    /**
     * Whether the given format is supported or not.
     * The "format" is not a file extension, instead is the "nickname" of the format as provided through the command line
     * to define a specific conversion.
     */
    boolean support(String format);

    /**
     * The list of supported formats.
     * If one of this format is passed to {@link ToCenConversion#support(String)} it should return {@code true}.
     */
    Set<String> getSupportedFormats();

    /**
     * The preferred file extension for the target format, without ".".
     * For instance, not '{@code .xml}' but '{@code xml}'.
     */
    String extension();

    /**
     *
     * Regex string for mapping key validation
     */
    String getMappingRegex();
}
