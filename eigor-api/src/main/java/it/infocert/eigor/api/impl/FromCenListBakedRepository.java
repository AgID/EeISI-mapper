package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.FromCenConversionRepository;
import it.infocert.eigor.api.configuration.Configurable;
import it.infocert.eigor.api.configuration.ConfigurationException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FromCenListBakedRepository implements FromCenConversionRepository, Configurable {

    private List<FromCenConversion> converters;
    private Set<String> formats;

    public FromCenListBakedRepository(FromCenConversion... converters) {
        this.converters = Arrays.asList(converters);
    }

    @Override
    public FromCenConversion findConversionFromCen(String format) {
        for (FromCenConversion converter: converters) {
            if (converter.support(format)) {
                return converter;
            }
        }
        return null;
    }

    @Override
    public Set<String> supportedFromCenFormats() {
        if (formats == null){
            formats  = new HashSet<>();
            for (FromCenConversion converter : converters) {
                formats.addAll(converter.getSupportedFormats());
            }
        }
        return formats;
    }

    @Override public void configure() throws ConfigurationException {
        for (FromCenConversion converter : converters) {
            converter.configure();
        }
    }
}
