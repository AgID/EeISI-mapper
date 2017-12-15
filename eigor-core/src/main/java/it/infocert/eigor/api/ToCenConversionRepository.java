package it.infocert.eigor.api;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * A {@link ToCenConversionRepository} is able to find a {@link ToCenConversion conversion} that will convert
 * an invoice from the given formatPadded to the CEN formatPadded.
 */
public interface ToCenConversionRepository {

    /**
     * Return the {@link ToCenConversion} that knows how to convert an invoice expressed in the given format.
     * @param sourceFormat The format of the original invoice.
     * @return <code>null</code> if no conversions are found.
     */
    @Nullable
    ToCenConversion findConversionToCen(String sourceFormat);

    Set<String> supportedToCenFormats();

    List<ToCenConversion> getToCenConverters();
}
