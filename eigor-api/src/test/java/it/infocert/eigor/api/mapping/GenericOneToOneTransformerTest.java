package it.infocert.eigor.api.mapping;

import com.google.common.io.Resources;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom2.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GenericOneToOneTransformerTest {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformer.class);
    private BG0000Invoice invoice;
    private ArrayList<ConversionIssue> errors;
    private Reflections reflections;
    private Document document;
    private SAXBuilder saxBuilder;
    private ConversionRegistry conversionRegistry;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();

        saxBuilder = new SAXBuilder();
        document = new Document(new Element("FatturaElettronica"));
        errors = new ArrayList<>(0);
        reflections = new Reflections("it.infocert");
        conversionRegistry = new ConversionRegistry(
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
                new StringToDoubleConverter(),
                new StringToStringConverter(),
                new JavaLocalDateToStringConverter(),
                new JavaLocalDateToStringConverter("dd-MMM-yy"),
                new Iso4217CurrenciesFundsCodesToStringConverter(),
                new Iso31661CountryCodesToStringConverter(),
                new DoubleToStringConverter("#.00"),
                new UnitOfMeasureCodesToStringConverter()
        );
    }

    @Test
    public void mappingToCen() throws Exception {
        URL italianInvoiceUrl = Resources.getResource("examples/fattpa/fatt-pa-plain-vanilla.xml");
        Document doc = null;
        try {

            doc = saxBuilder.build(italianInvoiceUrl.toURI().toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        assert doc != null;

        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        final String cenPath = "/BG0004/BT0027";

        GenericOneToOneTransformer transformator = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);

        transformator.transformXmlToCen(doc, invoice, errors);

        assertThat(invoice.getBG0004Seller(), hasSize(1));
        assertThat(invoice.getBG0004Seller().get(0).getBT0027SellerName(), hasSize(1));
        assertEquals("AZIENDA TEST S.p.A.", invoice.getBG0004Seller().get(0).getBT0027SellerName().get(0).toString());
    }

    @Test
    public void shouldConvertASingleBT() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero";

        final String cenPath = "/BT0001";

        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));


        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);
        transformer.transformCenToXml(invoice, document, errors);

        Element item = CommonConversionModule.evaluateXpath(document, xPathExpression).get(0);
        assertEquals("1", item.getText());
    }

    @Test
    public void shouldConvertFromDate() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data";

        final String cenPath = "/BT0002";
        invoice.getBT0002InvoiceIssueDate().add(new BT0002InvoiceIssueDate(LocalDate.parse("2017-03-21", DateTimeFormat.forPattern("yyyy-MM-dd"))));

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);
        transformer.transformCenToXml(invoice, document, errors);

        Element item = CommonConversionModule.evaluateXpath(document, xPathExpression).get(0);
        assertEquals("2017-03-21", item.getText());
    }

    @Test
    public void shouldConvertDeepBT() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";

        final String cenPath = "/BG0004/BT0027";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        invoice.getBG0004Seller().add(seller);

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);
        transformer.transformCenToXml(invoice, document, errors);
        Element item = CommonConversionModule.evaluateXpath(document, xPathExpression).get(0);
        assertEquals("name", item.getText());
    }

    @Test
    public void shouldNotConvertIfSourceElementIsAbsent() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        final String cenPath = "/BG0004/BT0027";

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);
        transformer.transformCenToXml(invoice, document, errors);
        List<Element> elementList = CommonConversionModule.evaluateXpath(document, xPathExpression);
        assertTrue(elementList.isEmpty());

    }

    @Test
    public void shouldThrowErrorIfCenPathIsInvalid() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        final String cenPath = "/Wrong";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        invoice.getBG0004Seller().add(seller);
        Document document = new Document(new Element("FatturaElettronica"));

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);
        transformer.transformCenToXml(invoice, document, errors);
        List<Element> items = CommonConversionModule.evaluateXpath(document, xPathExpression);

        assertTrue(items.isEmpty());
        assertEquals("Cannot format Wrong, should starts with either \"BT\" or \"BG\" followed by numbers. Example: \"BT0001\", \"BG0\", \"bt-12\" and similars.", errors.get(0).getMessage());
    }

    @Test
    public void shouldThrowErrorIfDeepCenPathIsInvalid() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        final String cenPath = "/BG0004/Wrong";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        invoice.getBG0004Seller().add(seller);
        Document document = new Document(new Element("FatturaElettronica"));

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry);
        transformer.transformCenToXml(invoice, document, errors);
        List<Element> items = CommonConversionModule.evaluateXpath(document, xPathExpression);

        assertTrue(items.isEmpty());
        assertEquals("Cannot format Wrong, should starts with either \"BT\" or \"BG\" followed by numbers. Example: \"BT0001\", \"BG0\", \"bt-12\" and similars.", errors.get(0).getMessage());
    }
}