package it.infocert.eigor.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import it.infocert.eigor.api.configuration.ConfigurableSupport;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.mapping.GenericManyToOneTransformer;
import it.infocert.eigor.api.mapping.GenericOneToManyTransformer;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.toCen.InvoiceCenXpathMappingValidator;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class AbstractToCenConverter implements ToCenConversion {

    private static final Logger log = LoggerFactory.getLogger(AbstractToCenConverter.class);
    private IReflections reflections;
    private final ConversionRegistry conversionRegistry;
    protected final EigorConfiguration configuration;
    protected final DefaultResourceLoader drl;

    @Nullable
    private Multimap<String, String> oneToOneMappings;

    @Nullable
    private Multimap<String, String> manyToOne;

    @Nullable
    private Multimap<String, String> oneToMany;
    protected final ConfigurableSupport configurableSupport;
    private List<CustomMapping<?>> customMappings = Lists.newArrayList();

    public AbstractToCenConverter(IReflections reflections, ConversionRegistry conversionRegistry, EigorConfiguration configuration) {
        this.reflections = reflections;
        this.conversionRegistry = conversionRegistry;
        this.configuration = configuration;
        this.drl = new DefaultResourceLoader();
        this.configurableSupport = new ConfigurableSupport(this);
    }

    @Override public void configure() throws ConfigurationException {

        // configure one-one mappings
        {
            InputInvoiceXpathMap mapper;
            String mappingRegex = getMappingRegex();
            if(mappingRegex!=null){
                mapper = new InputInvoiceXpathMap(new InvoiceCenXpathMappingValidator(mappingRegex, reflections));
            }else{
                mapper = new InputInvoiceXpathMap();
            }

            String one2OneMappingPath = getOne2OneMappingPath();
            if(one2OneMappingPath!=null){
                Resource thePathOfOneOneMappingFile = drl.getResource(one2OneMappingPath);
                oneToOneMappings = mapper.getMapping(thePathOfOneOneMappingFile);
            }

        }

        // configure many-one mappings
        {
            InputInvoiceXpathMap mapper = new InputInvoiceXpathMap();
            String many2OneMappingPath = getMany2OneMappingPath();
            if(many2OneMappingPath!=null){
                Resource thePathOfManyOneMappingFile = drl.getResource(many2OneMappingPath);
                manyToOne = mapper.getMapping(thePathOfManyOneMappingFile);
            }
        }

        // configure one-many mappings
        {
            InputInvoiceXpathMap mapper = new InputInvoiceXpathMap();
            String one2ManyMappingPath = getOne2ManyMappingPath();
            if(one2ManyMappingPath!=null){
                Resource thePathOfOneManyMappingFile = drl.getResource(one2ManyMappingPath);
                oneToMany = mapper.getMapping(thePathOfOneManyMappingFile);
            }
        }

        // load custom mappings
        {
            String resource = getCustomMappingPath();
            if (!Objects.equals(resource, "")) {
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
     * Apply 1to1 transformations into BG0000Invoice.
     *
     * @param document the input document
     * @param errors   the errors list
     * @return the BG0000Invoice
     */
    protected ConversionResult<BG0000Invoice> applyOne2OneTransformationsBasedOnMapping(Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        BG0000Invoice invoice = new BG0000Invoice();

        if(oneToOneMappings == null) {
            return new ConversionResult<>(errors, invoice);
        }


        for (Map.Entry<String, String> entry : oneToOneMappings.entries()) {
            GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(entry.getValue(), entry.getKey(), reflections, conversionRegistry);
            transformer.transformXmlToCen(document, invoice, errors);
        }
        return new ConversionResult<>(errors, invoice);
    }

    protected ConversionResult<BG0000Invoice> applyMany2OneTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        if(manyToOne == null) return new ConversionResult<BG0000Invoice>(errors, partialInvoice);

        for (String key: manyToOne.keySet()) {

            // Stop at each something.target key
            if (key.contains("target")){
                if (!existsValueForKeyInMany2OneMultiMap(manyToOne, key, errors)) {
                    continue;
                }
                String bgBtPath = manyToOne.get(key).iterator().next();
                String expressionKey = key.replace(".target", ".expression");
                if (!existsValueForKeyInMany2OneMultiMap(manyToOne, expressionKey, errors)) {
                    continue;
                }
                String combinationExpression = manyToOne.get(expressionKey).iterator().next();

                int index = 1;
                List<String> xPaths = new ArrayList<>();
                String sourceKey = key.replace("cen.target", "xml.source."+index);
                while (manyToOne.containsKey(sourceKey)){
                    if (existsValueForKeyInMany2OneMultiMap(manyToOne, sourceKey, errors)) {
                        xPaths.add(manyToOne.get(sourceKey).iterator().next());

                    }
                    index++;
                    sourceKey = key.replace("cen.target", "xml.source."+index);
                }

                GenericManyToOneTransformer transformer = new GenericManyToOneTransformer(bgBtPath, combinationExpression, xPaths, expressionKey.substring(0, expressionKey.indexOf(".expression")), reflections, conversionRegistry);
                transformer.transformXmlToCen(document, partialInvoice, errors);
            }
        }
        return new ConversionResult<BG0000Invoice>(errors, partialInvoice);
    }

    //TODO
//    protected ConversionResult<BG0000Invoice> applyOne2ManyTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
//        return new ConversionResult<BG0000Invoice>(errors, partialInvoice);
//    }
    protected ConversionResult<BG0000Invoice> applyOne2ManyTransformationsBasedOnMapping(BG0000Invoice partialInvoice, Document document, List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        if (oneToMany != null) {
            for (String key : oneToMany.keySet()) {

                // Stop at each something.target key
                if (key.endsWith("cen.source")) {
                    if (!existsValueForKeyInOne2ManyMultiMap(oneToMany, key, errors, "one2many")) {
                        continue;
                    }
                    String cenPath = oneToMany.get(key).iterator().next();

                    int index = 1;
                    List<String> xPaths = new ArrayList<>();
                    Map<String, Pair<Integer, Integer>> splitIndexPairs = new HashMap<>();
                    String sourceKey = key.replace("cen.source", "xml.target." + index);
                    while (oneToMany.containsKey(sourceKey)) {

                        if (existsValueForKeyInOne2ManyMultiMap(oneToMany, sourceKey, errors, "one2many")) {
                            String indexBeginString = null, indexEndString = null;
                            try {
                                Integer indexBegin = null;
                                if (existsValueForKeyInOne2ManyMultiMap(oneToMany, sourceKey.concat(".start"), errors, "one2many")) {
                                    indexBeginString = oneToMany.get(sourceKey.concat(".start")).iterator().next();
                                    indexBegin = Integer.parseInt(indexBeginString);
                                }
                                Integer indexEnd = null;
                                if (existsValueForKeyInOne2ManyMultiMap(oneToMany, sourceKey.concat(".end"), errors, "one2many")) {
                                    indexEndString = oneToMany.get(sourceKey.concat(".end")).iterator().next();
                                    indexEnd = Integer.parseInt(indexEndString);
                                }

                                if (indexBegin != null && indexEnd != null) {
                                    Pair<Integer, Integer> pair = new Pair<>(indexBegin, indexEnd);
                                    String xPath = oneToMany.get(sourceKey).iterator().next();
                                    xPaths.add(xPath);
                                    splitIndexPairs.put(xPath, pair);
                                }
                            } catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newError(new RuntimeException(String.format("For start index key %s value is %s, for end index key %s value is %s!", sourceKey.concat(".start"), indexBeginString, sourceKey.concat(".end"), indexEndString))));
                            }
                        }
                        index++;
                        sourceKey = key.replace("cen.source", "xml.target." + index);
                    }

                    GenericOneToManyTransformer transformer = new GenericOneToManyTransformer(reflections, conversionRegistry, xPaths, cenPath, splitIndexPairs);
                    transformer.transformCenToXml(partialInvoice, document, errors);
                }
            }
        }
        return new ConversionResult<>(errors, partialInvoice);
    }

    private boolean existsValueForKeyInMany2OneMultiMap(Multimap<String, String> mapping, String key, List<IConversionIssue> errors) {
        if (mapping.get(key) == null || !mapping.get(key).iterator().hasNext()) {
            errors.add(ConversionIssue.newError(new RuntimeException("No value in many2one mapping properties for key: " + key)));
            return false;
        }
        return true;
    }
    
    private boolean existsValueForKeyInOne2ManyMultiMap(Multimap<String, String> mapping, String key, List<IConversionIssue> errors, String mappingType) {
    	if (mapping.get(key) == null || !mapping.get(key).iterator().hasNext()) {
            RuntimeException e = new RuntimeException(String.format("No value in %s mapping properties for key %s!", mappingType, key));
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
     * Gets the document.
     *
     * @param sourceInvoiceStream the source invoice stream
     * @return the document
     * @throws SyntaxErrorInInvoiceFormatException syntax error in invoice format exception
     */
    protected Document getDocument(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException, JDOMException, IOException {
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
