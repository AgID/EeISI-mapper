package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.AbstractFromCenConverter;
import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static it.infocert.eigor.api.conversion.ConversionRegistry.DEFAULT_REGISTRY;

/**
 * A fake conversion used to lay out the API general structure.
 *
 * @see FakeToCenConversion
 */
public class FakeFromCenConversion extends AbstractFromCenConverter {



    public FakeFromCenConversion(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, DEFAULT_REGISTRY, configuration, null);
    }

    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) {
        BinaryConversionResult binaryConversionResult = new BinaryConversionResult("this is a fake invoice".getBytes(), new ArrayList<IConversionIssue>());
        return binaryConversionResult;
    }

    @Override
    public boolean support(String format) {
        return "fake".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Collections.singletonList("fake"));
    }

    @Override
    public String extension() {
        return "fake";
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getOne2OneMappingPath() {
        return "/tmp/fake.properties";
    }

    @Override protected String getMany2OneMappingPath() {
        return null;
    }

    @Override protected String getOne2ManyMappingPath() {
        return null;
    }

    @Override
    protected String getCustomMappingPath() {
        return null;
    }

    @Override
    public String getName() {
        return "fake";
    }

    @Override public void configure() throws ConfigurationException {
        // really nothing to do here
    }
}
