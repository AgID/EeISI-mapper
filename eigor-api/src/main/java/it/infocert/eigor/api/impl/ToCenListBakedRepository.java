package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.ToCenConversionRepository;
import it.infocert.eigor.api.configuration.Configurable;
import it.infocert.eigor.api.configuration.ConfigurationException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToCenListBakedRepository implements ToCenConversionRepository, Configurable {

    private List<ToCenConversion> converters;
    private Set<String> formats;

    public ToCenListBakedRepository(ToCenConversion... converters) {
        this.converters = Arrays.asList(converters);
    }

    @Override
    public ToCenConversion findConversionToCen(String format) {
        for (ToCenConversion converter: converters) {
            if (converter.support(format)) {
                return converter;
            }
        }
        return null;
    }

    @Override
    public Set<String> supportedToCenFormats() {
        if (formats == null){
            formats  = new HashSet<>();
            for (ToCenConversion converter : converters) {
                formats.addAll(converter.getSupportedFormats());
            }
        }
        return formats;
    }

    @Override public void configure() throws ConfigurationException {
        for (ToCenConversion converter : converters) {
            converter.configure();
        }
    }
}
