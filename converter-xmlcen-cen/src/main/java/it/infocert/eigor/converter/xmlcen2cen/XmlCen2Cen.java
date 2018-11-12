package it.infocert.eigor.converter.xmlcen2cen;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.AbstractToCenConverter;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.CustomMappingLoader;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.CountryNameToIso31661CountryCodeConverter;
import it.infocert.eigor.api.conversion.converter.StringToBigDecimalConverter;
import it.infocert.eigor.api.conversion.converter.StringToBigDecimalPercentageConverter;
import it.infocert.eigor.api.conversion.converter.StringToIdentifierConverter;
import it.infocert.eigor.api.conversion.converter.StringToIso4217CurrenciesFundsCodesConverter;
import it.infocert.eigor.api.conversion.converter.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.conversion.converter.StringToStringConverter;
import it.infocert.eigor.api.conversion.converter.StringToUnitOfMeasureConverter;
import it.infocert.eigor.api.conversion.converter.StringToUntdid1001InvoiceTypeCodeConverter;
import it.infocert.eigor.api.conversion.converter.StringToUntdid5189ChargeAllowanceDescriptionCodesConverter;
import it.infocert.eigor.api.conversion.converter.StringToUntdid5305DutyTaxFeeCategoriesConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.AbstractBT;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0020PaymentTerms;
import it.infocert.eigor.model.core.model.BT0109InvoiceTotalAmountWithoutVat;
import it.infocert.eigor.model.core.model.BT0110InvoiceTotalVatAmount;
import it.infocert.eigor.model.core.model.BT0112InvoiceTotalAmountWithVat;
import it.infocert.eigor.model.core.model.BT0113PaidAmount;
import it.infocert.eigor.model.core.model.BT0115AmountDueForPayment;
import it.infocert.eigor.model.core.model.structure.CenStructure;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class XmlCen2Cen extends AbstractToCenConverter {

    private Logger log = LoggerFactory.getLogger(XmlCen2Cen.class);
    private final static String FORMAT = "xmlcen" ;
    private final DefaultResourceLoader drl = new DefaultResourceLoader();
    private final EigorConfiguration configuration;
    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("dd-MMM-yy" ),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd" ),
            StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),
            StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            StringToUntdid5305DutyTaxFeeCategoriesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid5305DutyTaxFeeCategories.class),
            StringToUnitOfMeasureConverter.newConverter(),
            StringToBigDecimalPercentageConverter.newConverter(),
            StringToBigDecimalConverter.newConverter(),
            StringToStringConverter.newConverter(),
            StringToUntdid5189ChargeAllowanceDescriptionCodesConverter.newConverter(),
            StringToIdentifierConverter.newConverter()
    );

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.one-to-one" ;
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.many-to-one" ;
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.one-to-many" ;
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.fatturapa-cen.mapping.custom" ;

    private XSDValidator xsdValidator;

    public XmlCen2Cen(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration, ErrorCode.Location.XMLCEN_IN);
        this.configuration = checkNotNull(configuration);
    }

    @Override
    public void configure() throws ConfigurationException {
        super.configure();

        // load the XSD.
        {
            String mandatoryString = this.configuration.getMandatoryString("eigor.converter.xmlcen-cen.xsd");
            xsdValidator = null;
            try {
                Resource xsdFile = drl.getResource(mandatoryString);

                xsdValidator = new XSDValidator(xsdFile.getFile(), ErrorCode.Location.XMLCEN_IN);
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for XMLCEN2CEN from '" + mandatoryString + "'.", e);
            }
        }
        configurableSupport.configure();
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        configurableSupport.checkConfigurationOccurred();

        List<IConversionIssue> errors = new ArrayList<>();
        InputStream clonedInputStream = null;

        /*
        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);

            clonedInputStream = new ByteArrayInputStream(bytes);

            List<IConversionIssue> validationErrors = xsdValidator.validate(bytes);
            if (validationErrors.isEmpty()) {
                log.info("Xsd validation succesful!");
            }
            errors.addAll(validationErrors);
        } catch (IOException | IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, "Error during validation", ErrorCode.Location.XMLCEN_IN, ErrorCode.Action.GENERIC, ErrorCode.Error.INVALID, Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
        }

        org.jdom2.Document document;
        try {
            document = getDocument(clonedInputStream);
        } catch (RuntimeException e) {
            throw new EigorRuntimeException(new ErrorMessage(e.getMessage(), ErrorCode.Location.XMLCEN_IN, ErrorCode.Action.GENERIC, ErrorCode.Error.INVALID), e);
        }

        ConversionResult<BG0000Invoice> result = applyOne2OneTransformationsBasedOnMapping(document, errors);
        result = applyMany2OneTransformationsBasedOnMapping(result.getResult(), document, errors);
        result = applyOne2ManyTransformationsBasedOnMapping(result.getResult(), document, errors);
        applyCustomMapping(result.getResult(), document, errors);


        // fix https://gitlab.com/tgi-infocert-eigor/eigor/issues/252
        BG0000Invoice invoice = result.getResult();
        // bt-112
        {
            BigDecimal bt109 = null;
            {
                BG0022DocumentTotals bg022cen = invoice.getBG0022DocumentTotals(0);
                BT0109InvoiceTotalAmountWithoutVat bt109cen = bg022cen.getBT0109InvoiceTotalAmountWithoutVat(0);
                bt109 = bt109cen.getValue();
                if (bt109 == null) bt109 = BigDecimal.ZERO;
            }

            BigDecimal bt110 = null;
            {
                BG0022DocumentTotals bg022cen = invoice.getBG0022DocumentTotals(0);
                BT0110InvoiceTotalVatAmount bt110cen = bg022cen.getBT0110InvoiceTotalVatAmount(0);
                bt110 = bt110cen.getValue();
                if (bt110 == null) bt110 = BigDecimal.ZERO;
            }

            BT0112InvoiceTotalAmountWithVat bt112cen = new BT0112InvoiceTotalAmountWithVat(bt109.add(bt110));
            invoice.getBG0022DocumentTotals(0).getBT0112InvoiceTotalAmountWithVat().set(0, bt112cen);


        }

        // bt-113
        if (invoice.getBG0022DocumentTotals(0).getBT0113PaidAmount().isEmpty()) {
            invoice.getBG0022DocumentTotals(0).getBT0113PaidAmount().add(new BT0113PaidAmount(BigDecimal.ZERO));
        }

        // bt-115
        {

            {
                BG0022DocumentTotals bg022cen = invoice.getBG0022DocumentTotals(0);
                BigDecimal bt112 = bg022cen.getBT0112InvoiceTotalAmountWithVat(0).getValue();
                BigDecimal bt114 = bg022cen.getBT0114RoundingAmount().isEmpty() ? BigDecimal.ZERO : bg022cen.getBT0114RoundingAmount(0).getValue();
                BigDecimal bt113 = bg022cen.getBT0113PaidAmount().isEmpty() ? BigDecimal.ZERO : bg022cen.getBT0113PaidAmount(0).getValue();

                BigDecimal bt115 = bt112.add(bt114).subtract(bt113);

                List<BT0115AmountDueForPayment> bt115s = invoice.getBG0022DocumentTotals(0).getBT0115AmountDueForPayment();
                if (bt115s.isEmpty()) {
                    bt115s.add(new BT0115AmountDueForPayment(bt115));
                } else {
                    bt115s.set(0, new BT0115AmountDueForPayment(bt115));
                }

            }
        }

        // when bt-115 is present, either bt-20 or bt-9
        BT0020PaymentTerms bt20cen = null;
        if (invoice.getBT0020PaymentTerms().isEmpty()) {
            invoice.getBT0020PaymentTerms().add(new BT0020PaymentTerms("N/A Payement Terms"));
        }


        return result;*/
        return null;
    }

    private void applyCustomMapping(BG0000Invoice invoice, org.jdom2.Document document, List<IConversionIssue> errors) {
        List<CustomMapping<org.jdom2.Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors, ErrorCode.Location.XMLCEN_IN);
        }
    }

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
        if (format == null) {
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
        return "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?" ;
    }

    @Override
    public String getName() {
        return "converter-xmlcen-cen" ;
    }
}
