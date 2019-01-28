package it.infocert.eigor.api.mapping;

import com.google.common.io.Resources;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class GenericOneToOneTransformerTest {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformerTest.class);
    private BG0000Invoice invoice;
    private ArrayList<IConversionIssue> errors;
    private IReflections reflections;
    private Document document;
    private SAXBuilder saxBuilder;
    private ConversionRegistry conversionRegistry;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        saxBuilder = new SAXBuilder();
        document = new Document(new Element("FatturaElettronica"));
        errors = new ArrayList<>(0);
        reflections = new JavaReflections();
        conversionRegistry = ConversionRegistry.DEFAULT_REGISTRY;
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

        GenericOneToOneTransformer transformator = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);

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


        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
        transformer.transformCenToXml(invoice, document, errors);

        Element item = CommonConversionModule.evaluateXpath(document, xPathExpression).get(0);
        assertEquals("1", item.getText());
    }

    @Test
    public void shouldConvertFromDate() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data";

        final String cenPath = "/BT0002";
        invoice.getBT0002InvoiceIssueDate().add(new BT0002InvoiceIssueDate(LocalDate.parse("2017-03-21", DateTimeFormat.forPattern("yyyy-MM-dd"))));

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
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

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
        transformer.transformCenToXml(invoice, document, errors);
        Element item = CommonConversionModule.evaluateXpath(document, xPathExpression).get(0);
        assertEquals("name", item.getText());
    }

    @Test
    public void shouldNotConvertIfSourceElementIsAbsent() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        final String cenPath = "/BG0004/BT0027";

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
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

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
        transformer.transformCenToXml(invoice, document, errors);
        List<Element> items = CommonConversionModule.evaluateXpath(document, xPathExpression);

        assertTrue(items.isEmpty());
        assertEquals("FATTPA_IN.CONFIGURED_MAP.INVALID - Cannot format 'Wrong', should starts with either \"BT\" or \"BG\" followed by numbers. Example: \"BT0001\", \"BG0\", \"bt-12\" and similars.", errors.get(0).getMessage());
    }

    @Test
    public void shouldThrowErrorIfDeepCenPathIsInvalid() throws Exception {
        final String xPathExpression = "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        final String cenPath = "/BG0004/Wrong";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        invoice.getBG0004Seller().add(seller);
        Document document = new Document(new Element("FatturaElettronica"));

        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
        transformer.transformCenToXml(invoice, document, errors);
        List<Element> items = CommonConversionModule.evaluateXpath(document, xPathExpression);

        assertTrue(items.isEmpty());
        assertEquals("FATTPA_IN.CONFIGURED_MAP.INVALID - Cannot format 'Wrong', should starts with either \"BT\" or \"BG\" followed by numbers. Example: \"BT0001\", \"BG0\", \"bt-12\" and similars.", errors.get(0).getMessage());
    }

    @Test
    public void shouldSupportCenPathAttributes() throws Exception {
        final String xPathExpression = "/Invoice/AccountingCustomerParty/Party/EndpointID";
        final String schemeXPathExpression = "/Invoice/AccountingCustomerParty/Party/EndpointID/@schemeID";
        final String cenPath = "/BG0007/BT0049";
        final String schemeCenPath = "/BG0007/BT0049/@schemeID";

        final Document ublInvoice = createUblInvoice();
        final BG0000Invoice invoice = new BG0000Invoice();
        //Normal tag
        GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(xPathExpression, cenPath, reflections, conversionRegistry, ErrorCode.Location.FATTPA_IN);
        transformer.transformXmlToCen(ublInvoice, invoice, errors);

        List<BG0007Buyer> buyers = invoice.getBG0007Buyer();
        assertFalse(buyers.isEmpty());

        List<BT0049BuyerElectronicAddressAndSchemeIdentifier> identifiers = buyers.get(0).getBT0049BuyerElectronicAddressAndSchemeIdentifier();
        assertFalse(identifiers.isEmpty());

        BT0049BuyerElectronicAddressAndSchemeIdentifier identifier = identifiers.get(0);
        assertEquals("Test", identifier.getValue().getIdentifier());
        assertEquals("ID", identifier.getValue().getIdentificationSchema());
    }

    private Document createUblInvoice() {
        Element root = new Element("Invoice");
        Element acp = new Element("AccountingCustomerParty");
        Element party = new Element("Party");
        Element endpointId = new Element("EndpointID");
        endpointId.setText("Test");
        endpointId.setAttribute("schemeID", "ID");
        party.addContent(endpointId);
        acp.addContent(party);
        root.addContent(acp);
        return new Document(root);
    }

}
