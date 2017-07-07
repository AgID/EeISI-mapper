package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import com.helger.commons.collection.pair.Pair;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.mapping.GenericManyToOneTransformer;
import it.infocert.eigor.api.mapping.GenericOneToManyTransformer;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.fromCen.InvoiceXpathCenMappingValidator;
import it.infocert.eigor.api.mapping.toCen.OneCen2ManyXpathMappingValidator;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class with utility methods for CEN-XML conversion
 */
public abstract class AbstractFromCenConverter implements FromCenConversion {

    private static final Logger log = LoggerFactory.getLogger(AbstractFromCenConverter.class);
    private Reflections reflections;
    private ConversionRegistry conversionRegistry;
    private String regex;


    protected AbstractFromCenConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        this.reflections = reflections;
        this.conversionRegistry = conversionRegistry;
    }

    /**
     * Move values from a {@link BG0000Invoice} to an XML {@link Document} and serialize it in a byte array
     *
     * @param invoice  the {@link BG0000Invoice} from which to take the values
     * @param document the {@link Document} to populate with BTvalues
     * @param errors   a list of {@link ConversionIssue}, to be filled if an error occurs during the conversion
     * @return a {@link ConversionResult} of {@link BinaryConversionResult} containing both the XML byte array and the error list
     * @throws SyntaxErrorInInvoiceFormatException
     */
    protected BinaryConversionResult applyOne2OneTransformationsBasedOnMapping(BG0000Invoice invoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        String pathOfMappingConfFile = getOne2OneMappingPath();
        Multimap<String, String> mappings = new InputInvoiceXpathMap(new InvoiceXpathCenMappingValidator(getMappingRegex(), reflections)).getMapping(pathOfMappingConfFile);

        for (Map.Entry<String, String> entry : mappings.entries()) {
            String key = entry.getKey();
            GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(key, entry.getValue(), reflections, conversionRegistry);
            transformer.transformCenToXml(invoice, document, errors);
        }

        return new BinaryConversionResult(createXmlFromDocument(document, errors), errors);
    }

    private byte[] createXmlFromDocument(Document document, List<ConversionIssue> errors) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new XMLOutputter().output(document, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            errors.add(ConversionIssue.newError(e, e.getMessage()));
            return null;
        }
    }

    protected BinaryConversionResult applyMany2OneTransformationsBasedOnMapping(BG0000Invoice invoice, Document partialDocument, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(null);
        Multimap<String, String> mapping = mapper.getMapping(getMany2OneMappingPath());
        for (String key: mapping.keySet()) {

            // Stop at each something.target key
            if (key.contains("target")){
                if (!existsValueForKeyInMultiMap(mapping, key, errors, "many2one")) {
                    continue;
                }
                String xPath = mapping.get(key).iterator().next();
                String expressionKey = key.replace(".target", ".expression");
                if (!existsValueForKeyInMultiMap(mapping, expressionKey, errors, "many2one")) {
                    continue;
                }
                String combinationExpression = mapping.get(expressionKey).iterator().next();

                int index = 1;
                List<String> btPaths = new ArrayList<>();
                String sourceKey = key.replace(".target", ".source."+index);
                while (mapping.containsKey(sourceKey)){
                    if (existsValueForKeyInMultiMap(mapping, sourceKey, errors, "many2one")) {
                        btPaths.add(mapping.get(sourceKey).iterator().next());

                    }
                    index++;
                    sourceKey = key.replace(".target", ".source."+index);
                }

                GenericManyToOneTransformer transformer = new GenericManyToOneTransformer(xPath, combinationExpression, btPaths, reflections, conversionRegistry);
                transformer.transformCenToXml(invoice, partialDocument, errors);
            }
        }
        return new BinaryConversionResult(createXmlFromDocument(partialDocument, errors), errors);
    }

    protected BinaryConversionResult applyOne2ManyTransformationsBasedOnMapping(BG0000Invoice invoice, Document partialDocument, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new OneCen2ManyXpathMappingValidator());
        Multimap<String, String> mapping = mapper.getMapping(getOne2ManyMappingPath());
        for (String key: mapping.keySet()) {

            // Stop at each something.target key
            if (key.endsWith("cen.source")){
                if (!existsValueForKeyInMultiMap(mapping, key, errors, "one2many")) {
                    continue;
                }
                String cenPath = mapping.get(key).iterator().next();

                int index = 1;
                List<String> xPaths = new ArrayList<>();
                Map<String,Pair<Integer,Integer>> splitIndexPairs = new HashMap<>();
                String sourceKey = key.replace("cen.source", "xml.target."+index);
                while (mapping.containsKey(sourceKey)){

                    if (existsValueForKeyInMultiMap(mapping, sourceKey, errors, "one2many")) {
                        String indexBeginString = null, indexEndString = null;
                        try {
                            Integer indexBegin = null;
                            if (existsValueForKeyInMultiMap(mapping, sourceKey.concat(".start"), errors, "one2many")) {
                                indexBeginString = mapping.get(sourceKey.concat(".start")).iterator().next();
                                indexBegin = Integer.parseInt(indexBeginString);
                            }
                            Integer indexEnd = null;
                            if (existsValueForKeyInMultiMap(mapping, sourceKey.concat(".end"), errors, "one2many")) {
                                indexEndString = mapping.get(sourceKey.concat(".end")).iterator().next();
                                indexEnd = Integer.parseInt(indexEndString);
                            }

                            Pair<Integer,Integer> pair = new Pair<>(indexBegin, indexEnd);
                            String xPath = mapping.get(sourceKey).iterator().next();
                            xPaths.add(xPath);
                            splitIndexPairs.put(xPath,pair);
                        } catch (NumberFormatException e) {
                            errors.add(ConversionIssue.newError(new RuntimeException(String.format("For start index key %s value is %s, for end index key %s value is %s!", sourceKey.concat(".start"), indexBeginString, sourceKey.concat(".end"), indexEndString))));
                        }
                    }
                    index++;
                    sourceKey = key.replace("cen.source", "xml.target."+index);
                }

                GenericOneToManyTransformer transformer = new GenericOneToManyTransformer(reflections, conversionRegistry, xPaths, cenPath, splitIndexPairs);
                transformer.transformCenToXml(invoice, partialDocument, errors);
            }
        }
        return new BinaryConversionResult(createXmlFromDocument(partialDocument, errors), errors);
    }

    private boolean existsValueForKeyInMultiMap(Multimap<String, String> mapping, String key, List<ConversionIssue> errors, String mappingType) {
        if (mapping.get(key) == null || !mapping.get(key).iterator().hasNext()) {
            errors.add(ConversionIssue.newError(new RuntimeException(String.format("No value in %s mapping properties for key %s!",  mappingType, key))));
            return false;
        }
        return true;
    }

    /**
     * Get the one2one mapping configuration file path
     *
     * @return the path to the file
     */
    protected abstract String getOne2OneMappingPath();
    // TODO Will all the from cen converters have this kind of mapping? If so, make methods abstrac
    protected String getMany2OneMappingPath() {
        return null;
    }
    protected String getOne2ManyMappingPath() {
        return null;
    }

    public void setMappingRegex(String regex) {
        this.regex = regex;
    }
    @Override
    public String getMappingRegex() {
        return regex;
    }
}
