package it.infocert.eigor.api;

import java.util.Set;

/**
 * Find a {@link FromCenConversion conversion} that will convert
 * a @{@link it.infocert.eigor.model.core.model.BG0000Invoice CEN} invoice in another format.
 */
public interface FromCenConversionRepository {

    /**
     * Return the conversion that correspond to the given format.
     */
    public FromCenConversion findConversionFromCen(String ubl);

    /**
     * Return the supported formats.
     */
    public Set<String> supportedFromCenFormats();


}
