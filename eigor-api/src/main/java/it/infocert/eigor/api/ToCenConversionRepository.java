package it.infocert.eigor.api;

/**
 * A {@link ToCenConversionRepository} is able to find a {@link ToCENConversion conversion} that will convert
 * an invoice from the given format to the CEN format.
 */
public interface ToCenConversionRepository {

    /**
     * Return the {@link ToCENConversion} that knows how to convert an invoice expressed in the given format.
     * @param sourceFormat The format of the original invoice.
     * @return <code>null</code> if no conversions are found.
     */
    ToCENConversion findConversionToCen(String sourceFormat);
}
