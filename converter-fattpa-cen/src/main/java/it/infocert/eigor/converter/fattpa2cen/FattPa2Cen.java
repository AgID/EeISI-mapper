package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathFactory;
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
        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);

            InputStream clonedInputStream = new ByteArrayInputStream(bytes);

            Document doc = getDocument(clonedInputStream);

            BG0000Invoice invoice = new BG0000Invoice();
            String number = getNumber(doc);
            invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber(number));
            return new ConversionResult<>(Lists.<IConversionIssue>newArrayList(), invoice);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private String getNumber(Document doc) {
        XPathFactory factory = XPathFactory.instance();
        Text number = factory.compile("//FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero/text()", Filters.text()).evaluateFirst(doc);
        return number.getText();
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
