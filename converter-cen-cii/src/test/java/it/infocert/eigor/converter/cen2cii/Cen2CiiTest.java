package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.*;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class Cen2CiiTest {
    private static final Logger log = LoggerFactory.getLogger(Cen2CiiTest.class);

    private Cen2Cii converter;

    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new DefaultEigorConfigurationLoader().loadConfiguration();
        converter = new Cen2Cii(new Reflections("it.infocert"), conf);
        converter.configure();
    }

    @Test
    public void shouldSupportCii(){
        assertThat(converter.support("cii"), is(true));
    }

    @Test
    public void shouldSupportedFormatsCii(){
        assertThat(converter.getSupportedFormats(), contains("cii"));
    }
}
