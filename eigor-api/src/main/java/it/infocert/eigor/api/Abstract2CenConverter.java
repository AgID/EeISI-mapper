package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.mapping.toCen.GenericOneToOneTransformation;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceXpathMap;
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
import java.util.List;
import java.util.Map;

public abstract class Abstract2CenConverter implements  ToCenConversion {

    private static final Logger log = LoggerFactory.getLogger(Abstract2CenConverter.class);
    private Reflections reflections;

    public Abstract2CenConverter(Reflections reflections) {
        this.reflections = reflections;
    }

    protected Abstract2CenConverter() {
    }

    /**
     * Apply 1to1 transformations into BG0000Invoice.
     *
     * @param document the input document
     * @param errors   the errors list
     * @return the BG0000Invoice
     */
    protected ConversionResult<BG0000Invoice> applyOne2OneTransformationsBasedOnMapping(Document document, List<Exception> errors) throws SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = new BG0000Invoice();

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap();
        Multimap<String, String> mapping = mapper.getMapping(getMappingPath());
        for (Map.Entry<String, String> entry : mapping.entries()) {
            GenericOneToOneTransformation transformer = new GenericOneToOneTransformation(entry.getValue(), entry.getKey(), reflections);
            transformer.transform(document, invoice, errors);
        }
        return new ConversionResult<BG0000Invoice>(errors, invoice);
    }

    /**
     * Gets the document.
     *
     * @param sourceInvoiceStream the source invoice stream
     * @return the document
     * @throws SyntaxErrorInInvoiceFormatException syntax error in invoice format exception
     */
    public Document getDocument(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
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

}
