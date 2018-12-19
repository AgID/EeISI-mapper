package it.infocert.eigor.converter.ublcn2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The UBL CreditNote to CEN format converter
 */
@SuppressWarnings("unchecked")
public class UblCn2Cen extends AbstractToCenConverter {

    private static final Logger log = LoggerFactory.getLogger(UblCn2Cen.class);
    private static final String FORMAT = "ublcn";
    private final DefaultResourceLoader drl = new DefaultResourceLoader();
    private final EigorConfiguration configuration;
    private static final ConversionRegistry conversionRegistry = initConversionStrategy();

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.ublcn-cen.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.ublcn-cen.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.ublcn-cen.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.ublcn-cen.mapping.custom";

    private XSDValidator xsdValidator;
    private IXMLValidator ublValidator;
    private IXMLValidator ciusValidator;

    public UblCn2Cen(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry,  configuration, ErrorCode.Location.UBLCN_IN);
        this.configuration = checkNotNull(configuration);
    }

    @Override public void configure() throws ConfigurationException {
        super.configure();

        // load the XSD.
        {
            String mandatoryString = this.configuration.getMandatoryString("eigor.converter.ublcn-cen.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);

                xsdValidator = new XSDValidator(xsdFile.getFile(), ErrorCode.Location.UBLCN_IN);
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for UBLCN2CEN from '" + mandatoryString + "'.", e);
            }
        }

        // load the UBL schematron validator.
        try {
            Resource ublSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.ublcn-cen.schematron") );
            boolean schematronAutoUpdate = "true".equals(this.configuration.getMandatoryString("eigor.converter.ublcn-cen.schematron.auto-update-xslt"));
            ublValidator = new SchematronValidator(ublSchemaFile.getFile(), true, schematronAutoUpdate, ErrorCode.Location.UBLCN_IN);
        } catch (Exception e) {
            throw new ConfigurationException("An error occurred while loading configuring " + this + ".", e);
        }

        // load the CIUS schematron validator.
        try {
            Resource ciusSchemaFile = drl.getResource( this.configuration.getMandatoryString("eigor.converter.ublcn-cen.cius") );
            boolean ciusAutoUpdate = "true".equals(this.configuration.getMandatoryString("eigor.converter.ublcn-cen.cius.auto-update-xslt"));
            ciusValidator = new SchematronValidator(ciusSchemaFile.getFile(), true, ciusAutoUpdate, ErrorCode.Location.UBLCN_IN);
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

			errors.addAll(validationErrors);
            errors.addAll(schematronErrors);
            errors.addAll(ciusErrors);

        } catch (IOException | IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, ErrorMessage.builder()
                    .message("Error during validation")
                    .location(ErrorCode.Location.UBLCN_IN)
                    .action(ErrorCode.Action.GENERIC)
                    .error(ErrorCode.Error.INVALID)
                    .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                    .build()
            ));
        }

        Document document;
        try {
            document = getDocument(clonedInputStream);
        } catch (RuntimeException e) {
            throw new EigorRuntimeException(ErrorMessage.builder()
                    .message("Error during XML parsing")
                    .location(ErrorCode.Location.UBLCN_IN)
                    .action(ErrorCode.Action.XML_PARSING)
                    .error(ErrorCode.Error.INVALID)
                    .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                    .build()
            );
        }
        ConversionResult<BG0000Invoice> result = applyOne2OneTransformationsBasedOnMapping(document, errors);

        result = applyMany2OneTransformationsBasedOnMapping(result.getResult(), document, errors);
        result = applyOne2ManyTransformationsBasedOnMapping(result.getResult(), document, errors);
        applyCustomMapping(result.getResult(), document, errors);
        return result;
    }

    private void applyCustomMapping(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        List<CustomMapping<Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors, ErrorCode.Location.UBLCN_IN);
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
        return "converter-ublcn-cen";
    }

    private static ConversionRegistry initConversionStrategy(){
        return new ConversionRegistry(

                // enums
                CountryNameToIso31661CountryCodeConverter.newConverter(),
                LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),

                StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
                LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),

                StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
                LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),

                StringToUntdid5305DutyTaxFeeCategoriesConverter.newConverter(),
                LookUpEnumConversion.newConverter(Untdid5305DutyTaxFeeCategories.class),

                StringToUnitOfMeasureConverter.newConverter(),
                LookUpEnumConversion.newConverter(UnitOfMeasureCodes.class),

                LookUpEnumConversion.newConverter(VatExemptionReasonsCodes.class),

                Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
                Iso31661CountryCodesToStringConverter.newConverter(),
                StringToUntdid4461PaymentMeansCode.newConverter(),
                UnitOfMeasureCodesToStringConverter.newConverter(),

                StringToUntdid5189ChargeAllowanceDescriptionCodesConverter.newConverter(),
                StringToUntdid2005DateTimePeriodQualifiers.newConverter(),

                // dates
                StringToJavaLocalDateConverter.newConverter("dd-MMM-yy"),
                StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd"),
                JavaLocalDateToStringConverter.newConverter(),
                JavaLocalDateToStringConverter.newConverter("dd-MMM-yy"),

                // numbers
                StringToBigDecimalConverter.newConverter(),
                BigDecimalToStringConverter.newConverter("#.00"),

                // binaries
                Base64StringToBinaryConverter.newConverter(),

                // string
                StringToStringConverter.newConverter()


        );
    }
}
