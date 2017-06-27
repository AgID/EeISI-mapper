package it.infocert.eigor.converter.ubl2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom2.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The UBL to CEN format converter
 */
@SuppressWarnings("unchecked")
public class Ubl2Cen extends Abstract2CenConverter {

    private static final Logger log = LoggerFactory.getLogger(Ubl2Cen.class);
    private static final String FORMAT = "ubl";
    private static final ConversionRegistry conversionRegistry = new ConversionRegistry(
            new CountryNameToIso31661CountryCodeConverter(),
            new LookUpEnumConversion(Iso31661CountryCodes.class),
            new StringToJavaLocalDateConverter("dd-MMM-yy"),
            new StringToJavaLocalDateConverter("yyyy-MM-dd"),
            new StringToUntdid1001InvoiceTypeCodeConverter(),
            new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),
            new StringToIso4217CurrenciesFundsCodesConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
            new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
            new LookUpEnumConversion(Untdid5305DutyTaxFeeCategories.class),
            new StringToUnitOfMeasureConverter(),
            new LookUpEnumConversion(UnitOfMeasureCodes.class),
            new LookUpEnumConversion(VatExemptionReasonsCodes.class),
            new StringToDoubleConverter(),
            new JavaLocalDateToStringConverter(),
            new JavaLocalDateToStringConverter("dd-MMM-yy"),
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new Iso31661CountryCodesToStringConverter(),
            new DoubleToStringConverter("#.00"),
            new UnitOfMeasureCodesToStringConverter(),
            new StringToStringConverter()

    );

    public static final String MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/one_to_one.properties";


    public Ubl2Cen(Reflections reflections) {
        super(reflections, conversionRegistry);
        setMappingRegex("/(BG|BT)[0-9]{4}(-[0-9]{1})?");
    }

    /**
     * 1. read document (from xml to Document obj)
     * 2. maps each path into BG/BT obj
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

        IXMLValidator ublValidator;
        IXMLValidator ciusValidator;
        try {

            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
            clonedInputStream = new ByteArrayInputStream(bytes);

            ublValidator = new SchematronValidator(ublSchemaFile, true);
            errors.addAll(ublValidator.validate(bytes));

            ciusValidator = new SchematronValidator(ciusSchemaFile, true);
            errors.addAll(ciusValidator.validate(bytes));

        } catch (IOException | IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, "Schematron validation error!"));
        }

        Document document = getDocument(clonedInputStream);
        ConversionResult<BG0000Invoice> result = applyOne2OneTransformationsBasedOnMapping(document, errors);

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
    public String getMappingPath() {
        return MAPPING_PATH;
    }

}
