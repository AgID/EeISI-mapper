package it.infocert.eigor.converter.ubl2cen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.converter.ubl2cen.mapping.UblXpathMap;
import it.infocert.eigor.converter.ubl2cen.mapping.GenericOneToOneTransformation;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The UBL to CEN format converter
 */
public class Ubl2Cen implements ToCenConversion {

    private static final Logger log = LoggerFactory.getLogger(Ubl2Cen.class);

    private static final String FORMAT = "ubl";
    private Reflections reflections;

    public Ubl2Cen(Reflections reflections) {
        this.reflections = reflections;
    }

    /**
     * 1. read document (from xml to Document obj)
     * 2. maps each path into BG/BT obj
     *
     * @param sourceInvoiceStream The stream containing the representation of the invoice to be converted.
     * @return ConversionResult<BG0000Invoice>
     * @throws SyntaxErrorInInvoiceFormatException
     */
    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = null;

        Document document = getDocument(sourceInvoiceStream);
        ConversionResult<BG0000Invoice> result = applyTransformations(document);

        return result;
    }

    /**
     * Gets the document.
     *
     * @param sourceInvoiceStream the source invoice stream
     * @return the document
     * @throws SyntaxErrorInInvoiceFormatException syntax error in invoice format exception
     */
    protected Document getDocument(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(sourceInvoiceStream);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return doc;
    }

    /**
     * Apply transformations into BG0000Invoice.
     *
     * @param document the input document
     * @return the BG0000Invoice
     */
    protected ConversionResult<BG0000Invoice> applyTransformations(Document document) throws SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = new BG0000Invoice();

        UblXpathMap mapper = new UblXpathMap();
        Multimap<String, String> mapping = mapper.getMapping();
        List<Exception> errors = new ArrayList<>();
        for (Map.Entry<String, String> entry : mapping.entries()) {
            GenericOneToOneTransformation transformer = new GenericOneToOneTransformation(entry.getValue(), entry.getKey(), reflections);
            transformer.transform(document, invoice, errors);
        }

        log.info("transformed invoice: " + invoice);
        return new ConversionResult<BG0000Invoice>(errors, invoice);
    }

    @Override
    public boolean support(String format) {
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList(FORMAT));
    }

}
