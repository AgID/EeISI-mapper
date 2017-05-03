package it.infocert.eigor.api;

import it.infocert.eigor.model.core.model.BG0000Invoice;

/**
 * Convert a {@link BG0000Invoice CEN invoice} into another format.
 * <h2>General Contract</h2>
 * <p>
 *     Implementations are not required to perform any validation on the provided
 *     {@link BG0000Invoice CEN invoice}.
 * </p>
 * @see ToCenConversion
 */
public interface FromCenConversion {


    ConversionResult convert(BG0000Invoice invoice);

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
