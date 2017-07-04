package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.mapping.GenericManyToOneTransformer;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.toCen.InvoiceCenXpathMappingValidator;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Abstract2CenConverter implements ToCenConversion {

    private static final Logger log = LoggerFactory.getLogger(Abstract2CenConverter.class);
    private Reflections reflections;
    private String regex;
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

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new InvoiceCenXpathMappingValidator(getMappingRegex(), reflections));
        Multimap<String, String> mapping = mapper.getMapping(getOne2OneMappingPath());
        for (Map.Entry<String, String> entry : mapping.entries()) {
            GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(entry.getValue(), entry.getKey(), reflections, conversionRegistry);
            transformer.transformXmlToCen(document, invoice, errors);
        }
        return new ConversionResult<>(errors, invoice);
    }

    protected ConversionResult<BG0000Invoice> applyMany2OneTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(null);
        Multimap<String, String> mapping = mapper.getMapping(getMany2OneMappingPath());
        for (String key: mapping.keySet()) {

            // Stop at each something.target key
            if (key.contains("target")){
                if (!existsValueForKeyInMany2OneMultiMap(mapping, key, errors)) {
                    continue;
                }
                String bgBtPath = mapping.get(key).iterator().next();
                String expressionKey = key.replace(".target", ".expression");
                if (!existsValueForKeyInMany2OneMultiMap(mapping, expressionKey, errors)) {
                    continue;
                }
                String combinationExpression = mapping.get(expressionKey).iterator().next();

                int index = 1;
                List<String> xPaths = new ArrayList<>();
                String sourceKey = key.replace("cen.target", "xml.expression."+index);
                while (mapping.containsKey(sourceKey)){
                    if (existsValueForKeyInMany2OneMultiMap(mapping, sourceKey, errors)) {
                        xPaths.add(mapping.get(sourceKey).iterator().next());

                    }
                    index++;
                    sourceKey = key.replace(".target", ".source."+index);
                }

                GenericManyToOneTransformer transformer = new GenericManyToOneTransformer(bgBtPath, combinationExpression, xPaths, reflections, conversionRegistry);
                transformer.transformXmlToCen(document, partialInvoice, errors);
            }
        }
        return new ConversionResult<BG0000Invoice>(errors, partialInvoice);
    }

    private boolean existsValueForKeyInMany2OneMultiMap(Multimap<String, String> mapping, String key, List<ConversionIssue> errors) {
        if (mapping.get(key) == null || !mapping.get(key).iterator().hasNext()) {
            errors.add(ConversionIssue.newError(new RuntimeException("No value in many2one mapping properties for key: " + key)));
            return false;
        }
        return true;
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


    protected String getOne2OneMappingPath() {
        return null;
    }

    protected String getMany2OneMappingPath() {
        return null;
    }

    protected String getOne2ManyMappingPath() {
        return null;
    }

    public String getMappingRegex() {
        return regex;
    }

    public void setMappingRegex(String regex) {
        this.regex = regex;
    }
}
