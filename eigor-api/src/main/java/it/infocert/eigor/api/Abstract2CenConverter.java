package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.mapping.toCen.GenericManyToOneTransformation;
import it.infocert.eigor.api.mapping.toCen.GenericOneToOneTransformation;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceCenXpathMapValidator;
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
import java.util.ArrayList;
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
    protected ConversionResult<BG0000Invoice> applyOne2OneTransformationsBasedOnMapping(Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = new BG0000Invoice();

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new InputInvoiceCenXpathMapValidator("/(BG|BT)[0-9]{4}(-[0-9]{1})?"));
        Multimap<String, String> mapping = mapper.getMapping(getOne2OneMappingPath());
        for (Map.Entry<String, String> entry : mapping.entries()) {
            GenericOneToOneTransformation transformer = new GenericOneToOneTransformation(entry.getValue(), entry.getKey(), reflections);
            transformer.transform(document, invoice, errors);
        }
        return new ConversionResult<BG0000Invoice>(errors, invoice);
    }

    protected ConversionResult<BG0000Invoice> applyMany2OneTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap();
        Multimap<String, String> mapping = mapper.getMapping(getMany2OneMappingPath());
        for (String key: mapping.keySet()) {

            // Stop at each something.target key
            if (key.contains("target")){
                if (existsValueForKeyInMany2OneMultiMap(mapping, key, errors)) {
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
                }

                GenericManyToOneTransformation transformer = new GenericManyToOneTransformation(bgBtPath, combinationExpression, xPaths, reflections);
                transformer.transform(document, partialInvoice, errors);
            }
//            mapBt33.type=concatenation
//            mapBt33.xml.source.1=**/CapitaleSociale
//            mapBt33.xml.source.2=**/SocioUnico
//            mapBt33.xml.source.3=**/StatoLiquidazione
//            mapBt33.cen.target=BT-33
//            mapBt33.cen.expression=%1-%2 %3
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


    protected String getOne2OneMappingPath() {
        return null;
    }

    protected String getMany2OneMappingPath() {
        return null;
    }

    protected String getOne2ManyMappingPath() {
        return null;
    }
}
