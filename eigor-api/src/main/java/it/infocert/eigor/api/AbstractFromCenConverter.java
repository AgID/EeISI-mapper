package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.configuration.ConfigurableSupport;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import com.helger.commons.collection.pair.Pair;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.mapping.GenericManyToOneTransformer;
import it.infocert.eigor.api.mapping.GenericOneToManyTransformer;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.fromCen.InvoiceXpathCenMappingValidator;
import it.infocert.eigor.api.mapping.toCen.OneCen2ManyXpathMappingValidator;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class with utility methods for CEN-XML conversion
 */
public abstract class AbstractFromCenConverter implements FromCenConversion {

    private static final Logger log = LoggerFactory.getLogger(AbstractFromCenConverter.class);
    private final EigorConfiguration configuration;
    private Reflections reflections;
    private ConversionRegistry conversionRegistry;
    private String regex;
    private final DefaultResourceLoader drl;
    private Multimap<String, String> mappings;
    private Multimap<String, String> many2oneMappings;
    protected final ConfigurableSupport configurableSupport;

    protected AbstractFromCenConverter(Reflections reflections, ConversionRegistry conversionRegistry, EigorConfiguration configuration) {
        this.reflections = reflections;
        this.conversionRegistry = conversionRegistry;
        this.drl = new DefaultResourceLoader();
        this.configuration = checkNotNull( configuration );
        this.configurableSupport = new ConfigurableSupport(this);
    }

    protected final ResourceLoader getResourceLoader() {
        return drl;
    }

    protected final EigorConfiguration getConfiguration() {
        return configuration;
    }

    @Override public void configure() throws ConfigurationException {

        // load one to one mappings
        {
            String one2OneMappingPath = getOne2OneMappingPath();
            InputStream inputStream = null;
            Resource resource = drl.getResource(this.configuration.getMandatoryString(one2OneMappingPath));
            try {
                inputStream = resource.getInputStream();
                InputInvoiceXpathMap inputInvoiceXpathMap = new InputInvoiceXpathMap(new InvoiceXpathCenMappingValidator(getMappingRegex(), reflections));
                mappings = inputInvoiceXpathMap.getMapping(inputStream);
            } catch (IOException e) {
                throw new ConfigurationException(e);
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    log.warn("Unable to close resource {}.", resource);
                }
            }
        }

        // load many to one mappings
        {
            InputStream inputStream = null;
            String resource = getMany2OneMappingPath();
            try {
                inputStream = drl.getResource(configuration.getMandatoryString(resource)).getInputStream();
                InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(null);
                many2oneMappings = mapper.getMapping(inputStream);
            } catch (IOException e) {
                throw new ConfigurationException(e);
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    log.warn("Unable to close resource {}.", resource);
                }
            }
        }

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
    protected Pair<Document, List<ConversionIssue>> applyOne2OneTransformationsBasedOnMapping(BG0000Invoice invoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        // apply the mappings
        for (Map.Entry<String, String> entry : mappings.entries()) {
            String key = entry.getKey();
            GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(key, entry.getValue(), reflections, conversionRegistry);
            transformer.transformCenToXml(invoice, document, errors);
        }
        return new Pair<>(document, errors);
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


        for (String key: many2oneMappings.keySet()) {

            // Stop at each something.target key
            if (key.contains("target")){
                if (!existsValueForKeyInMultiMap(many2oneMappings, key, errors, "many2one")) {
                    continue;
                }
                String xPath = many2oneMappings.get(key).iterator().next();
                String expressionKey = key.replace(".target", ".expression");
                if (!existsValueForKeyInMultiMap(many2oneMappings, expressionKey, errors, "many2one")) {
                    continue;
                }
                String combinationExpression = many2oneMappings.get(expressionKey).iterator().next();

                int index = 1;
                List<String> btPaths = new ArrayList<>();
                String sourceKey = key.replace(".target", ".source."+index);
                while (many2oneMappings.containsKey(sourceKey)){
                    if (existsValueForKeyInMultiMap(many2oneMappings, sourceKey, errors, "many2one")) {
                        btPaths.add(many2oneMappings.get(sourceKey).iterator().next());

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

        InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new OneCen2ManyXpathMappingValidator("\\/FatturaElettronica\\/FatturaElettronica(Header|Body)(\\/\\w+(\\[\\])*)*", "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?", reflections));
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

    protected abstract String getMany2OneMappingPath();

    protected abstract String getOne2ManyMappingPath();

    public void setMappingRegex(String regex) {
        this.regex = regex;
    }
    @Override
    public String getMappingRegex() {
        return regex;
    }
}
