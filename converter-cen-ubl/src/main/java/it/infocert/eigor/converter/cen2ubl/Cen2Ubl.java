package it.infocert.eigor.converter.cen2ubl;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ConversionIssueErrorCodeMapper;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.cen2ubl.converters.Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Cen2Ubl extends AbstractFromCenConverter {

    private final Logger log = LoggerFactory.getLogger(Cen2Ubl.class);

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.cen-ubl.mapping.custom";

    private static final String FORMAT = "ubl";

    private final String CBC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private final String CAC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private final EigorConfiguration configuration;
    private final DefaultResourceLoader drl = new DefaultResourceLoader();

    private XSDValidator xsdValidator;
    private IXMLValidator ublValidator;
    private IXMLValidator ciusValidator;

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            new StringToStringConverter(),
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
            new JavaLocalDateToStringConverter(),
            new Untdid2005DateTimePeriodQualifiersToStringConverter(),
            new Untdid1001InvoiceTypeCodesToStringConverter(),
            new DoubleToStringConverter("0.00"),
            new Iso31661CountryCodesToStringConverter(),
            new IdentifierToStringConverter(),
            new Untdid4461PaymentMeansCodeToString()
    );

    @Override
    public void configure() throws ConfigurationException {
        super.configure();
        // load the XSD.
        {
            String mandatoryString = this.configuration.getMandatoryString("eigor.converter.cen-ubl.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);

                xsdValidator = new XSDValidator(xsdFile.getFile());
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for UBL2CEN from '" + mandatoryString + "'.", e);
            }
        }

        // load the UBL schematron validator.
        try {
            Resource ublSchemaFile = drl.getResource(this.configuration.getMandatoryString("eigor.converter.cen-ubl.schematron"));
            ublValidator = new SchematronValidator(ublSchemaFile.getFile(), true);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

//        // load the CIUS schematron validator.
//        try {
//            Resource ciusSchemaFile = drl.getResource(this.configuration.getMandatoryString("eigor.converter.cen-ubl.cius"));
//            ciusValidator = new SchematronValidator(ciusSchemaFile.getFile(), true);
//        } catch (Exception e) {
//            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
//        }

        configurableSupport.configure();

    }

    public Cen2Ubl(Reflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
        this.configuration = checkNotNull(configuration);
    }


    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) throws SyntaxErrorInInvoiceFormatException {
        List<IConversionIssue> errors = new ArrayList<>(0);
        Document document = new Document();
        createRootNode(document);

        applyOne2OneTransformationsBasedOnMapping(invoice, document, errors);
        applyMany2OneTransformationsBasedOnMapping(invoice, document, errors);
        applyOne2ManyTransformationsBasedOnMapping(invoice, document, errors);
        applyCustomMapping(invoice, document, errors);

        new XmlNamespaceApplier(CBC_URI, CAC_URI).applyUblNamespaces(document);
        new ConversionIssueErrorCodeMapper(getName()).mapAll(errors);

        byte[] documentByteArray = createXmlFromDocument(document, errors);


        try {

            List<IConversionIssue> validationErrors = xsdValidator.validate(documentByteArray);
            if (validationErrors.isEmpty()) {
                log.info("Xsd validation succesful!");
            }
            errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "XSD").mapAll(validationErrors));
            errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "Schematron").mapAll(ublValidator.validate(documentByteArray)));
//            errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "SchematronCIUS").mapAll(ciusValidator.validate(documentByteArray)));

        } catch (IllegalArgumentException e) {
            errors.add(new ConversionIssueErrorCodeMapper(getName(), "Validation").map(ConversionIssue.newWarning(e, e.getMessage())));
        }

        return new BinaryConversionResult(documentByteArray, errors);
    }

    private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors);
        }
    }

    @Override
    public boolean support(String format) {
        return "ubl".equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Collections.singletonList(FORMAT));
    }

    @Override
    public String extension() {
        return "xml";
    }

    @Override
    public String getMappingRegex() {
        return "\\/Invoice(\\/\\w+(\\[\\])*)*";
    }

    @Override
    protected String getOne2OneMappingPath() {
        return ONE2ONE_MAPPING_PATH;
    }

    @Override
    protected String getMany2OneMappingPath() {
        return MANY2ONE_MAPPING_PATH;
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return ONE2MANY_MAPPING_PATH;
    }

    @Override
    protected String getCustomMappingPath() {
        return CUSTOM_CONVERTER_MAPPING_PATH;
    }

    @Override
    public String getName() {
        return "converter-cen-ubl";
    }



    private void createRootNode(Document doc) {
        Element root = new Element("Invoice");
        root.addNamespaceDeclaration(Namespace.getNamespace("cac", CAC_URI));
        root.addNamespaceDeclaration(Namespace.getNamespace("cbc", CBC_URI));
        root.addNamespaceDeclaration(Namespace.getNamespace("qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("udt", "urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2"));
        root.addNamespaceDeclaration(Namespace.getNamespace("ccts", "urn:un:unece:uncefact:documentation:2"));
        root.setNamespace(Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"));
        doc.setRootElement(root);
    }
}

