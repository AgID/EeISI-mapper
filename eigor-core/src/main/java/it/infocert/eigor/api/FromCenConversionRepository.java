package it.infocert.eigor.api;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Find a {@link FromCenConversion conversion} that will convert
 * a @{@link it.infocert.eigor.model.core.model.BG0000Invoice CEN} invoice in another format.
 */
public interface FromCenConversionRepository {

    /**
     * Return the conversion that correspond to the given format.
     */
    @Nullable
    FromCenConversion findConversionFromCen(String ubl);

    /**
     * Return the supported formats.
     */
    Set<String> supportedFromCenFormats();


    List<FromCenConversion> getFromCenConverters();
}
