package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.AbstractToCenConverter;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class FattPa2Cen extends AbstractToCenConverter {

    private final static Logger log = LoggerFactory.getLogger(FattPa2Cen.class);
    private final static String FORMAT = "ubl";

    public FattPa2Cen(Reflections reflections, ConversionRegistry conversionRegistry, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        InputStream clonedInputStream = null;

        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);

            clonedInputStream = new ByteArrayInputStream(bytes);

            Document doc = getDocument(clonedInputStream);

            BG0000Invoice invoice = new BG0000Invoice();
            doc.getRootElement()
            return new ConversionResult<BG0000Invoice>(Lists.newArrayList(), )
        } catch (IOException | JDOMException e) {
            log.error(e.getMessage(), e);
        }
        return null;
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

    @Override
    public boolean support(String format) {
        if(format == null){
            log.error("NULL FORMAT");
            return false;
        }
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return null;
    }

    @Override
    public String getMappingRegex() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
