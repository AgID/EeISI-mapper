package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.AbstractToCenConverter;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FattPA2CenConverter extends AbstractToCenConverter {

    private static final Logger log = LoggerFactory.getLogger(FattPA2CenConverter.class);
    private static final ConversionRegistry conversionRegistry = new ConversionRegistry();

    public FattPA2CenConverter(Reflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
    }

    public ConversionResult<BG0000Invoice> convert(InputStream input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(input);
        } catch ( IOException | ParserConfigurationException | SAXException e) {
            log.error(e.getMessage(), e);
        }
        assert doc != null;
        doc.getDocumentElement().normalize();

        //TODO Implement conversion

        return new ConversionResult<BG0000Invoice>( new BG0000Invoice() );
    }

    public BG0000Invoice convert(String fileName) {
        return convert(new File(fileName));
    }

    public BG0000Invoice convert(File file) {
        BG0000Invoice converted = null;

        try(FileInputStream input = new FileInputStream(file)) {
            converted = convert(input).getResult();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return converted;
    }


    @Override
    public boolean support(String format) {
        return "fattpa".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>( Arrays.asList("fattpa") );
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getName() {
        return "cen-fatturapa";
    }

    @Override public void configure() throws ConfigurationException {
        // this converter is probably not completely done and it does not support the protocol imposed by the abstract.
    }

    @Override
    protected String getOne2OneMappingPath() {
        return null;
    }

    @Override
    protected String getMany2OneMappingPath() {
        return null;
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return null;
    }

    @Override
    protected String getCustomMappingPath() {
        return null;
    }
}
