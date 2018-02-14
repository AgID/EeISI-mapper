package it.infocert.eigor.api;

import it.infocert.eigor.api.configuration.Configurable;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.impl.FromCenListBakedRepository;
import it.infocert.eigor.api.impl.ToCenListBakedRepository;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConversionRepository implements ToCenConversionRepository, FromCenConversionRepository, Configurable {

    private final ToCenListBakedRepository toCenConversionRepository;
    private final FromCenListBakedRepository fromCenConversionRepository;

    @Override
    public void configure() throws ConfigurationException {
        toCenConversionRepository.configure();
        fromCenConversionRepository.configure();
    }

    private ConversionRepository(ToCenListBakedRepository toConversions, FromCenListBakedRepository fromConversions) {
        this.fromCenConversionRepository = fromConversions;
        this.toCenConversionRepository = toConversions;
    }

    /**
     * Return the conversion that correspond to the given format.
     * @param format
     */
    @Nullable
    @Override public FromCenConversion findConversionFromCen(String format) {
        return fromCenConversionRepository.findConversionFromCen(format);
    }

    /**
     * Return the supported formats.
     */
    @Override public Set<String> supportedFromCenFormats() {
        return fromCenConversionRepository.supportedFromCenFormats();
    }

    @Override
    public List<FromCenConversion> getFromCenConverters() {
        return fromCenConversionRepository.getFromCenConverters();
    }

    /**
     * Return the {@link ToCenConversion} that knows how to convert an invoice expressed in the given format.
     * @param sourceFormat The format of the original invoice.
     * @return <code>null</code> if no conversions are found.
     */
    @Nullable
    @Override public ToCenConversion findConversionToCen(String sourceFormat) {
        return toCenConversionRepository.findConversionToCen(sourceFormat);
    }

    @Override public Set<String> supportedToCenFormats() {
        return toCenConversionRepository.supportedToCenFormats();
    }

    @Override
    public List<ToCenConversion> getToCenConverters() {
        return toCenConversionRepository.getToCenConverters();
    }

    public static class Builder {

        private Set<ToCenConversion> toCenConversions = new LinkedHashSet<>();
        private Set<FromCenConversion> fromCenConversions = new LinkedHashSet<>();

        public Builder register(ToCenConversion toCenConversion) {
            if(toCenConversion!=null) this.toCenConversions.add(toCenConversion);
            return this;
        }

        public Builder register(FromCenConversion fromCenConversion) {
            if(fromCenConversion!=null) this.fromCenConversions.add(fromCenConversion);
            return this;
        }

        public ConversionRepository build() {

            FromCenConversion[] fromConversionsAsArray = fromCenConversions!=null ? fromCenConversions.toArray(new FromCenConversion[]{}) : new FromCenConversion[]{};
            FromCenListBakedRepository fromConversions = new FromCenListBakedRepository(fromConversionsAsArray);

            ToCenConversion[] toCenConversionsAsArray = toCenConversions!=null ? toCenConversions.toArray(new ToCenConversion[]{}) : new ToCenConversion[]{};
            ToCenListBakedRepository toConversions = new ToCenListBakedRepository(toCenConversionsAsArray);

            return new ConversionRepository(
                    toConversions,
                    fromConversions
            );
        }
    }
}
