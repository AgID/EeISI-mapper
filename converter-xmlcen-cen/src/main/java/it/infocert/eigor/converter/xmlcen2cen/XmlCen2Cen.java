package it.infocert.eigor.converter.xmlcen2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.configuration.ConfigurationException;
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
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import it.infocert.eigor.model.core.model.structure.CenStructure;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class XmlCen2Cen implements ToCenConversion {

    private final CenStructure cenStructure;
    private final InvoiceUtils utils;

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

    private Logger log = LoggerFactory.getLogger(XmlCen2Cen.class);

    public XmlCen2Cen(IReflections reflections) {
        cenStructure = new CenStructure();
        utils = new InvoiceUtils(reflections);
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        List<IConversionIssue> errors = new ArrayList<>();

        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
            InputStream clonedInputStream = new ByteArrayInputStream(bytes);

            //Reading xml file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(clonedInputStream);
            doc.getDocumentElement().normalize();
            //TODO applying xsd validation

            //converting xml to CEN
            convertXmlToCen(doc);


        } catch(ParserConfigurationException | SAXException | IOException e) {
            log.error("Cannot parse xml", e);
        }

        return null;
    }

    @Override
    public boolean support(String format) {
        return "xmlcen".equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList("xmlcen" ));
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getName() {
        return "xmlcen-cen";
    }

    @Override
    public void configure() throws ConfigurationException {
        // nothing to configure
    }

    public <E extends Enum<E>> void test(Class<E> t) {

    }

    private BG0000Invoice convertXmlToCen(Document doc) {
        if(Objects.isNull(doc)) {
            final String msg = "Document cannot be null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        BG0000Invoice invoice = new BG0000Invoice();

        final Node bt1 = doc.getElementsByTagName("BT-1").item(0);
        if(Objects.nonNull(bt1)) {
            invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber(bt1.getFirstChild().getNodeValue()));
        }
        final Node bt2 = doc.getElementsByTagName("BT-2").item(0);
        if(Objects.nonNull(bt2)) {
            final LocalDate bt2Date = conversionRegistry.convert(String.class, LocalDate.class, bt2.getFirstChild().getNodeValue());
            invoice.getBT0002InvoiceIssueDate().add(new BT0002InvoiceIssueDate(bt2Date));
        }
        return invoice;
    }

}
