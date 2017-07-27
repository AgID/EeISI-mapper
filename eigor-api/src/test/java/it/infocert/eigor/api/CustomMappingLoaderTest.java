package it.infocert.eigor.api;

import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.configuration.PropertiesBackedConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomMappingLoaderTest {

    private Reflections reflections;
    private ConversionRegistry conversionRegistry;

    @Before
    public void setUp() throws Exception {
        reflections = new Reflections("it.infocert.eigor");
        conversionRegistry = mock(ConversionRegistry.class);
    }

    @Test
    public void converterShouldLoadAListOfClass() throws Exception {
        TestFromCenConverter sut = new TestFromCenConverter(reflections, conversionRegistry, createConfiguration("classpath:custom-test.conf"));
        sut.configure();
        List<CustomMapping<?>> customMapping = sut.getCustomMapping();
        assertFalse(customMapping.isEmpty());
        assertThat(customMapping.size(), is(1));
        assertTrue(customMapping.get(0) instanceof TestCustomMapper);
    }

    @Test
    public void converterShouldThrowConfigurationExceptionIfConfigIsNotAValidClass() throws Exception {
        TestFromCenConverter sut = new TestFromCenConverter(reflections, conversionRegistry, createConfiguration("custom-test-wrong.conf"));
        try {
            sut.configure();
            fail();
        } catch (ConfigurationException e) {
            assertTrue(e.getMessage().contains("Invalid Class Name Test"));
        }
    }

    @Test
    public void converterShouldNotBreakIfAnExistentButNotConverterClassIsSpecified() throws Exception {
        TestFromCenConverter sut = new TestFromCenConverter(reflections, conversionRegistry, createConfiguration("custom-test-wrong-class.conf"));
        sut.configure();
        List<CustomMapping<?>> customMapping = sut.getCustomMapping();
        assertFalse(customMapping.isEmpty());
        assertThat(customMapping.size(), is(2));
    }

    private EigorConfiguration createConfiguration(String configFile) {
        return new PropertiesBackedConfiguration()
                .addProperty("eigor.converter.test.mapping.one-to-one", "classpath:test-mapping.properties")
                .addProperty("eigor.converter.test.mapping.many-to-one", "classpath:test-mapping.properties")
                .addProperty("eigor.converter.test.mapping.one-to-many", "classpath:test-mapping.properties")
                .addProperty("eigor.converter.test.mapping.custom", configFile)
                ;
    }
}

final class TestFromCenConverter extends AbstractFromCenConverter {

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.test.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.test.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.test.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.test.mapping.custom";

    TestFromCenConverter(Reflections reflections, ConversionRegistry conversionRegistry, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
    }


    @Override
    protected String getOne2OneMappingPath() {
        return ONE2ONE_MAPPING_PATH;
    }

    @Override
    protected String getMany2OneMappingPath() {
        return MANY2ONE_MAPPING_PATH;
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return ONE2MANY_MAPPING_PATH;
    }

    @Override
    protected String getCustomMappingPath() {
        return CUSTOM_CONVERTER_MAPPING_PATH;
    }

    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {
        return null;
    }

    @Override
    public boolean support(String format) {
        return false;
    }

    @Override
    public Set<String> getSupportedFormats() {
        return null;
    }

    @Override
    public String extension() {
        return null;
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getName() {
        return null;
    }
}

class TestCustomMapper implements CustomMapping<String> {

    @Override
    public void map(BG0000Invoice cenInvoice, String s, List<IConversionIssue> errors) {
        System.out.println("Success!");
    }
}
