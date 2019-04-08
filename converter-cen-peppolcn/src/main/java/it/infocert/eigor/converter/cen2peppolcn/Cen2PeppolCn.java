package it.infocert.eigor.converter.cen2peppolcn;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.api.xml.ClasspathXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.commons.cen2ubl.XmlNamespaceApplier;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.FileSystemResource;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Cen2PeppolCn extends AbstractFromCenConverter {

    private final Logger log = LoggerFactory.getLogger(Cen2PeppolCn.class);

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cen-peppolcn.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cen-peppolcn.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.cen-peppolcn.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.cen-peppolcn.mapping.custom";

    private static final String FORMAT = "peppolcn";

    private final String CBC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private final String CAC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private final EigorConfiguration configuration;
    private final DefaultResourceLoader drl = new DefaultResourceLoader();

    private XSDValidator xsdValidator;
    private IXMLValidator cenValidator;
    private IXMLValidator ublValidator;

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            StringToStringConverter.newConverter(),
            Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            JavaLocalDateToStringConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToStringConverter.newConverter(),
            Untdid1001InvoiceTypeCodesToStringConverter.newConverter(),
            BigDecimalToStringConverter.newConverter("0.00"),
            Iso31661CountryCodesToStringConverter.newConverter(),
            IdentifierToStringConverter.newConverter(),
            Untdid4461PaymentMeansCodeToString.newConverter()
    );

    @Override
    public void configure() throws ConfigurationException {
        super.configure();
        // load the XSD.
        {
            try {
                xsdValidator = new ClasspathXSDValidator("/converterdata/converter-commons/peppolcn/xsdstatic/UBL-CreditNote-2.1.xsd", ErrorCode.Location.PEPPOLCN_OUT);
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD.", e);
            }

        }

        // load the UBL schematron validator.
        try {
            Resource ublSchemaFile = drl.getResource(this.configuration.getMandatoryString("eigor.converter.cen-peppolcn.schematron1"));
            Resource cenSchemaFile = drl.getResource(this.configuration.getMandatoryString("eigor.converter.cen-peppolcn.schematron2"));
            boolean schematronAutoUpdate = "true".equals(this.configuration.getMandatoryString("eigor.converter.cen-peppolcn.schematron.auto-update-xslt"));
            cenValidator = new CiusSchematronValidator(new FileSystemResource(cenSchemaFile.getFile()), true, schematronAutoUpdate, ErrorCode.Location.PEPPOLCN_OUT);
            ublValidator = new SchematronValidator(new FileSystemResource(ublSchemaFile.getFile()), true, schematronAutoUpdate, ErrorCode.Location.PEPPOLCN_OUT);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        configurableSupport.configure();
    }

    public Cen2PeppolCn(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration, ErrorCode.Location.PEPPOLCN_OUT);
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

        byte[] documentByteArray = createXmlFromDocument(document, errors);


        try {
            List<IConversionIssue> validationErrors = xsdValidator.validate(documentByteArray);
            if (validationErrors.isEmpty()) {
                log.info("Xsd validation succesful!");
            }
            errors.addAll(validationErrors);
            List<IConversionIssue> schematronCenErrors = cenValidator.validate(documentByteArray);
            if (schematronCenErrors.isEmpty()) {
                log.info("Schematron cen validation successful!");
            }
            errors.addAll(schematronCenErrors);
            List<IConversionIssue> schematronUblErrors = ublValidator.validate(documentByteArray);
            if (schematronUblErrors.isEmpty()) {
                log.info("Schematron ubl validation successful!");
            }
            errors.addAll(schematronUblErrors);

        } catch (IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, "Error during validation", ErrorCode.Location.PEPPOLCN_OUT, ErrorCode.Action.GENERIC, ErrorCode.Error.INVALID, Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
        }

        return new BinaryConversionResult(documentByteArray, errors);
    }

    private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors, ErrorCode.Location.PEPPOLCN_OUT, this.getConfiguration());
        }
    }

    @Override
    public boolean support(String format) {
        return FORMAT.equals(format.toLowerCase().trim());
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
        return "\\/CreditNote(\\/\\w+(\\[\\])*)*";
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
        return "converter-cen-peppolcn";
    }


    private void createRootNode(Document doc) {
        Element root = new Element("CreditNote");
        root.addNamespaceDeclaration(Namespace.getNamespace("cac", CAC_URI));
        root.addNamespaceDeclaration(Namespace.getNamespace("cbc", CBC_URI));
        root.setNamespace(Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"));
        doc.setRootElement(root);
    }
}

