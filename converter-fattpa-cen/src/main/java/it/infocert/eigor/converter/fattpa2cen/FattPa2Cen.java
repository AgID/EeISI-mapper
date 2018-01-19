package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ConversionIssueErrorCodeMapper;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianCodeStringToUntdid1001InvoiceTypeCodeConverter;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianCodeStringToUntdid2005DateTimePeriodQualifiersConverter;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianCodeStringToUntdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The FattPA to CEN format converter
 */
public class FattPa2Cen extends AbstractToCenConverter {

    private final static Logger log = LoggerFactory.getLogger(FattPa2Cen.class);
    private final static String FORMAT = "fatturapa";
    private final DefaultResourceLoader drl = new DefaultResourceLoader();
    private final EigorConfiguration configuration;
    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd"),
            StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            StringToDoubleConverter.newConverter(),
            StringToStringConverter.newConverter(),
            ItalianCodeStringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
            ItalianCodeStringToUntdid4461PaymentMeansCode.newConverter(),
            ItalianCodeStringToUntdid2005DateTimePeriodQualifiersConverter.newConverter(),
            StringToIdentifierConverter.newConverter()
    );

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.custom";

    private XSDValidator xsdValidator;

    public FattPa2Cen(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration);
        this.configuration = checkNotNull(configuration);
    }

    @Override public void configure() throws ConfigurationException {
        super.configure();

        // load the XSD.
        {
            String mandatoryString = this.configuration.getMandatoryString("eigor.converter.fatturapa-cen.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);

                xsdValidator = new XSDValidator(xsdFile.getFile());
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for FattPA2CEN from '" + mandatoryString + "'.", e);
            }
        }
        configurableSupport.configure();
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        List<IConversionIssue> errors = new ArrayList<>();
        InputStream clonedInputStream = null;

        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);

            clonedInputStream = new ByteArrayInputStream(bytes);

            List<IConversionIssue> validationErrors = xsdValidator.validate(bytes);
            if (validationErrors.isEmpty()) {
                log.info("Xsd validation succesful!");
            }
            errors.addAll(new ConversionIssueErrorCodeMapper(getName(), "XSDValidation").mapAll(validationErrors));
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

//    private String getNumber(Document doc) {
//        XPathFactory factory = XPathFactory.instance();
//        Text number = factory.compile("//FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero/text()", Filters.text()).evaluateFirst(doc);
//        return number.getText();
//    }

    @Override
    protected String getOne2OneMappingPath() {
        return configuration.getMandatoryString(ONE2ONE_MAPPING_PATH);
    }

    @Override
    protected String getMany2OneMappingPath() {
        return configuration.getMandatoryString(MANY2ONE_MAPPING_PATH);
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return configuration.getMandatoryString(ONE2MANY_MAPPING_PATH);
    }

    @Override
    protected String getCustomMappingPath() {
        return CUSTOM_CONVERTER_MAPPING_PATH;
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
        return Sets.newHashSet(FORMAT);
    }

    @Override
    public String getMappingRegex() {
        return "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?";
    }

    @Override
    public String getName() {
        return "converter-fattpa-cen";
    }
}
