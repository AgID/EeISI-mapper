package it.infocert.eigor.converter.ubl2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ConversionIssueErrorCodeMapper;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The UBL to CEN format converter
 */
@SuppressWarnings("unchecked")
public class Ubl2Cen extends AbstractToCenConverter {

    private static final Logger log = LoggerFactory.getLogger(Ubl2Cen.class);
    private static final String FORMAT = "ubl";
    private final DefaultResourceLoader drl = new DefaultResourceLoader();
    private final EigorConfiguration configuration;
    private static final ConversionRegistry conversionRegistry = initConversionStrategy();

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.ubl-cen.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.ubl-cen.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.ubl-cen.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.ubl-cen.mapping.custom";

    private XSDValidator xsdValidator;
    private IXMLValidator ublValidator;
    private IXMLValidator ciusValidator;

    public Ubl2Cen(Reflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry,  configuration);
        this.configuration = checkNotNull(configuration);
    }

    @Override public void configure() throws ConfigurationException {
        super.configure();

        // load the XSD.
        {
            String mandatoryString = this.configuration.getMandatoryString("eigor.converter.ubl-cen.xsd");
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
            Resource ublSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.ubl-cen.schematron") );
            ublValidator = new SchematronValidator(ublSchemaFile.getFile(), true);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        // load the CIUS schematron validator.
        try {
            Resource ciusSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.ubl-cen.cius") );
            ciusValidator = new SchematronValidator(ciusSchemaFile.getFile(), true);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        configurableSupport.configure();
    }

    /**
     * 1. read document (from xml to Document obj)
     * 2. maps each path into BTBG obj
     *
     * @param sourceInvoiceStream The stream containing the representation of the invoice to be converted.
     * @return ConversionResult<BG0000Invoice>
     * @throws SyntaxErrorInInvoiceFormatException
     */
    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        List<IConversionIssue> errors = new ArrayList<>();

        InputStream clonedInputStream = null;

        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);

            clonedInputStream = new ByteArrayInputStream(bytes);

            List<IConversionIssue> validationErrors = xsdValidator.validate(bytes);
            if(validationErrors.isEmpty()){
            	log.info("Xsd validation successful!");
            }

            List<IConversionIssue> schematronErrors = ublValidator.validate(bytes);
            if (schematronErrors.isEmpty()) {
                log.info("Schematron validation successful!");
            }

            List<IConversionIssue> ciusErrors = ciusValidator.validate(bytes);
            if (ciusErrors.isEmpty()) {
                log.info("CIUS Schematron validation successful!");
            }

			errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "XSD").mapAll(validationErrors));
            errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "Schematron").mapAll(schematronErrors));
            errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "SchematronCIUS").mapAll(ciusErrors));

        } catch (IOException | IllegalArgumentException e) {
            errors.add(new ConversionIssueErrorCodeMapper(getName(), "Validation").map(ConversionIssue.newWarning(e, e.getMessage())));
        }

        Document document;
        try {
            document = getDocument(clonedInputStream);
        } catch (JDOMException | IOException e) {
            throw new EigorRuntimeException(new ErrorMessage(e.getMessage(), getName(), "DocumentBuilding", e.getClass().getSimpleName().replace("Exception", "")), e);
        }
        ConversionResult<BG0000Invoice> result = applyOne2OneTransformationsBasedOnMapping(document, errors);

        result = applyMany2OneTransformationsBasedOnMapping(result.getResult(), document, errors);
        result = applyOne2ManyTransformationsBasedOnMapping(result.getResult(), document, errors);
        applyCustomMapping(result.getResult(), document, errors);
        new ConversionIssueErrorCodeMapper(getName()).mapAll(errors);

        return result;
    }

    private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors);
        }
    }

    @Override
    public boolean support(String format) {
        if(format == null){
            log.error("NULL FORMAT");
            return false;
        }
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList(FORMAT));
    }

    @Override
    public String getMappingRegex() {
        return "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?";
    }

    @Override
    public String getOne2OneMappingPath() {
        return configuration.getMandatoryString(ONE2ONE_MAPPING_PATH);
    }

    @Override
    public String getMany2OneMappingPath() {
        return configuration.getMandatoryString(MANY2ONE_MAPPING_PATH);
    }

    @Override
    public String getOne2ManyMappingPath() {
        return configuration.getMandatoryString(ONE2MANY_MAPPING_PATH);
    }

    @Override
    protected String getCustomMappingPath() {
        return CUSTOM_CONVERTER_MAPPING_PATH;
    }

    @Override
    public String getName() {
        return "converter-ubl-cen";
    }

    private static ConversionRegistry initConversionStrategy(){
        return new ConversionRegistry(

                // enums
                new CountryNameToIso31661CountryCodeConverter(),
                new LookUpEnumConversion<>(Iso31661CountryCodes.class),

                new StringToUntdid1001InvoiceTypeCodeConverter(),
                new LookUpEnumConversion<>(Untdid1001InvoiceTypeCode.class),

                new StringToIso4217CurrenciesFundsCodesConverter(),
                new LookUpEnumConversion<>(Iso4217CurrenciesFundsCodes.class),

                new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
                new LookUpEnumConversion<>(Untdid5305DutyTaxFeeCategories.class),

                new StringToUnitOfMeasureConverter(),
                new LookUpEnumConversion<>(UnitOfMeasureCodes.class),

                new LookUpEnumConversion<>(VatExemptionReasonsCodes.class),

                new Iso4217CurrenciesFundsCodesToStringConverter(),
                new Iso31661CountryCodesToStringConverter(),
                new StringToUntdid4461PaymentMeansCode(),
                new UnitOfMeasureCodesToStringConverter(),

                new StringToUntdid5189ChargeAllowanceDescriptionCodesConverter(),
                new StringToUntdid2005DateTimePeriodQualifiers(),

                // dates
                new StringToJavaLocalDateConverter("dd-MMM-yy"),
                new StringToJavaLocalDateConverter("yyyy-MM-dd"),
                new JavaLocalDateToStringConverter(),
                new JavaLocalDateToStringConverter("dd-MMM-yy"),

                // numbers
                new StringToDoubleConverter(),
                new DoubleToStringConverter("#.00"),

                // binaries
                new Base64StringToBinaryConverter(),

                // string
                new StringToStringConverter()


        );
    }
}
