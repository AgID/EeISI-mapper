package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.configuration.ConfigurableSupport;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.mapping.GenericManyToOneTransformer;
import it.infocert.eigor.api.mapping.GenericOneToManyTransformer;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.fromCen.InvoiceXpathCenMappingValidator;
import it.infocert.eigor.api.mapping.fromCen.OneCen2ManyXpathMappingValidator;
import it.infocert.eigor.api.mapping.toCen.ManyCen2OneXpathMappingValidator;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import it.infocert.eigor.org.springframework.core.io.ResourceLoader;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private IReflections reflections;
    private ConversionRegistry conversionRegistry;
    private final ErrorCode.Location callingLocation;
    private final DefaultResourceLoader drl;
    private Multimap<String, String> mappings;
    private Multimap<String, String> many2oneMappings;
    private Multimap<String, String> one2ManyMappings;
    protected final ConfigurableSupport configurableSupport;
    private List<CustomMapping<?>> customMappings;

    protected AbstractFromCenConverter(IReflections reflections, ConversionRegistry conversionRegistry, EigorConfiguration configuration, ErrorCode.Location callingLocation) {
        this.reflections = reflections;
        this.conversionRegistry = conversionRegistry;
        this.callingLocation = callingLocation;
        this.drl = new DefaultResourceLoader();
        this.configuration = checkNotNull(configuration);
        this.configurableSupport = new ConfigurableSupport(this);
    }

    protected final ResourceLoader getResourceLoader() {
        return drl;
    }

    protected final EigorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void configure() throws ConfigurationException {

        // load one to one mappings
        {
            String one2OneMappingPath = getOne2OneMappingPath();
            InputStream inputStream = null;
            Resource resource = drl.getResource(this.configuration.getMandatoryString(one2OneMappingPath));
            try {
                inputStream = resource.getInputStream();
                InputInvoiceXpathMap inputInvoiceXpathMap = new InputInvoiceXpathMap(new InvoiceXpathCenMappingValidator(getMappingRegex(), reflections, callingLocation));
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
                InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new ManyCen2OneXpathMappingValidator(getMappingRegex(), "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?", reflections, callingLocation));
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

        // load one to many mappings
        {
            InputStream inputStream = null;
            String resource = getOne2ManyMappingPath();
            try {
                inputStream = drl.getResource(configuration.getMandatoryString(resource)).getInputStream();
                InputInvoiceXpathMap mapper = new InputInvoiceXpathMap(new OneCen2ManyXpathMappingValidator(getMappingRegex(), "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?", reflections, callingLocation));
                one2ManyMappings = mapper.getMapping(inputStream);
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

        // load custom mappings
        {
            String resource = getCustomMappingPath();
            if (resource != null) {
                try (InputStream inputStream = drl.getResource(configuration.getMandatoryString(resource)).getInputStream()) {
                    CustomMappingLoader cml = new CustomMappingLoader(inputStream);
                    customMappings = cml.loadCustomMapping();
                } catch (IllegalAccessException | InstantiationException | IOException | ClassNotFoundException e) {
                    throw new ConfigurationException(e);
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
     */
    protected Pair<Document, List<IConversionIssue>> applyOne2OneTransformationsBasedOnMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        // apply the mappings
        for (Map.Entry<String, String> entry : mappings.entries()) {
            String key = entry.getKey();
            GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(key, entry.getValue(), reflections, conversionRegistry, callingLocation);
            transformer.transformCenToXml(invoice, document, errors);
        }
        return new Pair<>(document, errors);
    }

    protected byte[] createXmlFromDocument(Document document, List<IConversionIssue> errors) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(document, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                    .location(callingLocation)
                    .action(ErrorCode.Action.CONFIGURED_MAP)
                    .error(ErrorCode.Error.INVALID)

                    .build());
            errors.add(ConversionIssue.newError(ere));
            return null;
        }
    }

    protected Pair<Document, List<IConversionIssue>> applyMany2OneTransformationsBasedOnMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {


        for (String key : many2oneMappings.keySet()) {

            // Stop at each something.target key
            if (key.contains("target")) {
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
                String sourceKey = key.replace(".target", ".source." + index);
                while (many2oneMappings.containsKey(sourceKey)) {
                    if (existsValueForKeyInMultiMap(many2oneMappings, sourceKey, errors, "many2one")) {
                        btPaths.add(many2oneMappings.get(sourceKey).iterator().next());

                    }
                    index++;
                    sourceKey = key.replace(".target", ".source." + index);
                }

                GenericManyToOneTransformer transformer = new GenericManyToOneTransformer(xPath, combinationExpression, btPaths, expressionKey.substring(0, expressionKey.indexOf(".expression")), reflections, conversionRegistry, callingLocation);
                transformer.transformCenToXml(invoice, document, errors);
            }
        }
        return new Pair<>(document, errors);
    }

    protected Pair<Document, List<IConversionIssue>> applyOne2ManyTransformationsBasedOnMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {

        for (String key : one2ManyMappings.keySet()) {

            // Stop at each something.target key
            if (key.endsWith("cen.source")) {
                if (!existsValueForKeyInMultiMap(one2ManyMappings, key, errors, "one2many")) {
                    continue;
                }
                String cenPath = one2ManyMappings.get(key).iterator().next();

                int index = 1;
                List<String> xPaths = new ArrayList<>();
                Map<String, Pair<Integer, Integer>> splitIndexPairs = new HashMap<>();
                String sourceKey = key.replace("cen.source", "xml.target." + index);
                while (one2ManyMappings.containsKey(sourceKey)) {

                    if (existsValueForKeyInMultiMap(one2ManyMappings, sourceKey, errors, "one2many")) {
                        String indexBeginString = null, indexEndString = null;
                        try {
                            Integer indexBegin = null;
                            if (existsValueForKeyInMultiMap(one2ManyMappings, sourceKey.concat(".start"), errors, "one2many")) {
                                indexBeginString = one2ManyMappings.get(sourceKey.concat(".start")).iterator().next();
                                indexBegin = Integer.parseInt(indexBeginString);
                            }
                            Integer indexEnd = null;
                            if (existsValueForKeyInMultiMap(one2ManyMappings, sourceKey.concat(".end"), errors, "one2many")) {
                                indexEndString = one2ManyMappings.get(sourceKey.concat(".end")).iterator().next();
                                indexEnd = Integer.parseInt(indexEndString);
                            }

                            Pair<Integer, Integer> pair = new Pair<>(indexBegin, indexEnd);
                            String xPath = one2ManyMappings.get(sourceKey).iterator().next();
                            xPaths.add(xPath);
                            splitIndexPairs.put(xPath, pair);
                        } catch (NumberFormatException e) {
                            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                    String.format("For start index key %s value is %s, for end index key %s value is %s!", sourceKey.concat(".start"), indexBeginString, sourceKey.concat(".end"), indexEndString),
                                    callingLocation,
                                    ErrorCode.Action.CONFIGURED_MAP,
                                    ErrorCode.Error.INVALID,
                                    e
                            )));
                        }
                    }
                    index++;
                    sourceKey = key.replace("cen.source", "xml.target." + index);
                }

                GenericOneToManyTransformer transformer = new GenericOneToManyTransformer(reflections, conversionRegistry, xPaths, cenPath, splitIndexPairs, callingLocation);
                transformer.transformCenToXml(invoice, document, errors);
            }
        }
        return new Pair<>(document, errors);
    }

    private boolean existsValueForKeyInMultiMap(Multimap<String, String> mapping, String key, List<IConversionIssue> errors, String mappingType) {
        if (mapping.get(key) == null || !mapping.get(key).iterator().hasNext()) {
            EigorRuntimeException e = new EigorRuntimeException(
                    String.format("No value in %s mapping properties for key %s!", mappingType, key),
                    callingLocation,
                    ErrorCode.Action.CONFIGURED_MAP,
                    ErrorCode.Error.INVALID
            );
            if (key.contains(".end")) {
                log.warn("Key {} is missing value. Assuming last index and splitting to string end", key);
            } else {
                errors.add(ConversionIssue.newError(e));
            }
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

    /**
     * Get the many2one mapping configuration file path
     *
     * @return the path to the file
     */
    protected abstract String getMany2OneMappingPath();

    /**
     * Get the one2many mapping configuration file path
     *
     * @return the path to the file
     */
    protected abstract String getOne2ManyMappingPath();

    /**
     * Get the path for the {@link CustomMapping} configuration file
     *
     * @return the path to the file
     */
    protected abstract String getCustomMappingPath();


    protected List<CustomMapping<?>> getCustomMapping() {
        return customMappings;
    }
}
