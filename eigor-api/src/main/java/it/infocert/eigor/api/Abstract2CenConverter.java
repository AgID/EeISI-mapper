package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceCenXpathMapValidator;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class Abstract2CenConverter implements ToCenConversion {

    private static final Logger log = LoggerFactory.getLogger(Abstract2CenConverter.class);
    private Reflections reflections;
    private ConversionRegistry conversionRegistry;

    public Abstract2CenConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        this.reflections = reflections;
        this.conversionRegistry = conversionRegistry;
    }


    /**
     * Apply 1to1 transformations into BG0000Invoice.
     *
     * @param document the input document
     * @param errors   the errors list
     * @return the BG0000Invoice
     */
    protected ConversionResult<BG0000Invoice> applyOne2OneTransformationsBasedOnMapping(Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = new BG0000Invoice();

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new InputInvoiceCenXpathMapValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?"));
        Multimap<String, String> mapping = mapper.getMapping(getMappingPath());
        for (Map.Entry<String, String> entry : mapping.entries()) {
            GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(entry.getValue(), entry.getKey(), reflections, conversionRegistry);
            transformer.transformXmlToCen(document, invoice, errors);
        }
        return new ConversionResult<>(errors, invoice);
    }

    /**
     * Gets the document.
     *
     * @param sourceInvoiceStream the source invoice stream
     * @return the document
     * @throws SyntaxErrorInInvoiceFormatException syntax error in invoice format exception
     */
    public Document getDocument(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        Document doc;
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            saxBuilder.setIgnoringBoundaryWhitespace(true);
            doc = saxBuilder.build(sourceInvoiceStream);
        } catch (JDOMException | IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return doc;
    }

    @Override
    public String getMappingPath() {
        return null;
    }
}
