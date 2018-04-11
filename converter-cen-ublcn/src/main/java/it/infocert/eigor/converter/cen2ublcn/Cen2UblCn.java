package it.infocert.eigor.converter.cen2ublcn;

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
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.commons.cen2ubl.XmlNamespaceApplier;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Cen2UblCn extends AbstractFromCenConverter {

    private final Logger log = LoggerFactory.getLogger(Cen2UblCn.class);

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.cen-ublcn.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.cen-ublcn.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.cen-ublcn.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.cen-ublcn.mapping.custom";

    private static final String FORMAT = "ublcn";

    private final String CBC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
    private final String CAC_URI = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
    private final EigorConfiguration configuration;
    private final DefaultResourceLoader drl = new DefaultResourceLoader();

    private XSDValidator xsdValidator;
    private IXMLValidator ublValidator;

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            StringToStringConverter.newConverter(),
            Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            JavaLocalDateToStringConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToStringConverter.newConverter(),
            Untdid1001InvoiceTypeCodesToStringConverter.newConverter(),
            DoubleToStringConverter.newConverter("0.00"),
            Iso31661CountryCodesToStringConverter.newConverter(),
            IdentifierToStringConverter.newConverter(),
            Untdid4461PaymentMeansCodeToString.newConverter()
    );

    @Override
    public void configure() throws ConfigurationException {
        super.configure();
        // load the XSD.
        {
            String mandatoryString = this.configuration.getMandatoryString("eigor.converter.cen-ublcn.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);

                xsdValidator = new XSDValidator(xsdFile.getFile(), ErrorCode.Location.UBLCN_OUT);
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for CEN2UBLCN from '" + mandatoryString + "'.", e);
            }
        }

        // load the UBL schematron validator.
        try {
            Resource ublSchemaFile = drl.getResource(this.configuration.getMandatoryString("eigor.converter.cen-ublcn.schematron"));
            boolean schematronAutoUpdate = "true".equals(this.configuration.getMandatoryString("eigor.converter.cen-ublcn.schematron.auto-update-xslt"));
            ublValidator = new SchematronValidator(ublSchemaFile.getFile(), true, schematronAutoUpdate, ErrorCode.Location.UBLCN_OUT);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        configurableSupport.configure();
    }

    public Cen2UblCn(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration, ErrorCode.Location.UBLCN_OUT);
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
            List<IConversionIssue> schematronErrors = ublValidator.validate(documentByteArray);
            if (schematronErrors.isEmpty()) {
                log.info("Schematron validation successful!");
            }
            errors.addAll(schematronErrors);

        } catch (IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, "Error during validation", ErrorCode.Location.UBLCN_OUT, ErrorCode.Action.GENERIC, ErrorCode.Error.INVALID, Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
        }

        return new BinaryConversionResult(documentByteArray, errors);
    }

    private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors, ErrorCode.Location.UBLCN_OUT);
        }

        // PEPPOL hardcoding
        final Element root = document.getRootElement();

        root.addContent(0, new Element("CustomizationID").setText(this.configuration.getMandatoryString("eigor.converter.cen-ublcn.customization-id")));
        root.addContent(1, new Element("ProfileID").setText(this.configuration.getMandatoryString("eigor.converter.cen-ublcn.profile-id")));
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
        return "converter-cen-ublcn";
    }



    private void createRootNode(Document doc) {
        Element root = new Element("CreditNote");
        root.addNamespaceDeclaration(Namespace.getNamespace("cac", CAC_URI));
        root.addNamespaceDeclaration(Namespace.getNamespace("cbc", CBC_URI));
        root.setNamespace(Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"));
        doc.setRootElement(root);
    }
}

