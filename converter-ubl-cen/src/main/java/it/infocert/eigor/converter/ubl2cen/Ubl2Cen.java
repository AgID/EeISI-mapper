package it.infocert.eigor.converter.ubl2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The UBL to CEN format converter
 */
@SuppressWarnings("unchecked")
public class Ubl2Cen extends AbstractToCenConverter {

    private static final Logger log = LoggerFactory.getLogger(Ubl2Cen.class);
    private static final String FORMAT = "ubl";
    private static final ConversionRegistry conversionRegistry = new ConversionRegistry(

            // enums
            new CountryNameToIso31661CountryCodeConverter(),
            new LookUpEnumConversion(Iso31661CountryCodes.class),

            new StringToUntdid1001InvoiceTypeCodeConverter(),
            new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),

            new StringToIso4217CurrenciesFundsCodesConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),

            new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
            new LookUpEnumConversion(Untdid5305DutyTaxFeeCategories.class),

            new StringToUnitOfMeasureConverter(),
            new LookUpEnumConversion(UnitOfMeasureCodes.class),

            new LookUpEnumConversion(VatExemptionReasonsCodes.class),

            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new Iso31661CountryCodesToStringConverter(),
            new StringToUntdid4461PaymentMeansCode(),
            new UnitOfMeasureCodesToStringConverter(),

            new StringToUntdid5189ChargeAllowanceDescriptionCodesConverter(),

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

    public static final String ONE2ONE_MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/one_to_one.properties";
    public static final String MANY2ONE_MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/many_to_one.properties";
    public static final String ONE2MANY_MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/one_to_many.properties";


    public Ubl2Cen(Reflections reflections) {
        super(reflections, conversionRegistry);
        setMappingRegex("(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?");
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
        List<ConversionIssue> errors = new ArrayList<>();

        InputStream clonedInputStream = null;
        File ublSchemaFile = new File("converterdata/converter-ubl-cen/ubl/schematron-xslt/EN16931-UBL-validation.xslt");
        File ciusSchemaFile = new File("converterdata/converter-ubl-cen/cius/schematron-xslt/CIUS-validation.xslt");
        File xsdFile = new File("converterdata/converter-ubl-cen/ubl/xsd/UBL-Invoice-2.1.xsd");

        IXMLValidator ublValidator;
        IXMLValidator ciusValidator;
        try {

            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
            clonedInputStream = new ByteArrayInputStream(bytes);
            
            XSDValidator xsdValidator = new XSDValidator(xsdFile);
            List<ConversionIssue> validationErrors = xsdValidator.validate(bytes);
            if(validationErrors.isEmpty()){
            	log.info("Xsd validation succesful!");
            }
			errors.addAll(validationErrors);

            ublValidator = new SchematronValidator(ublSchemaFile, true);
            errors.addAll(ublValidator.validate(bytes));

            ciusValidator = new SchematronValidator(ciusSchemaFile, true);
            errors.addAll(ciusValidator.validate(bytes));

        } catch (IOException | IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, e.getMessage()));
        }

        Document document = getDocument(clonedInputStream);
        ConversionResult<BG0000Invoice> result = applyOne2OneTransformationsBasedOnMapping(document, errors);

        result = applyMany2OneTransformationsBasedOnMapping(result.getResult(), document, errors);
        
        return result;
    }

    @Override
    public boolean support(String format) {
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList(FORMAT));
    }

    @Override
    public String getOne2OneMappingPath() {
        return ONE2ONE_MAPPING_PATH;
    }

    @Override
    public String getMany2OneMappingPath() {
        return MANY2ONE_MAPPING_PATH;
    }
}
