package it.infocert.eigor.converter.cen2peoppl;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.converter.cen2peoppl.Cen2PEPPOLBSI;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class Cen2PEPPOLBSITest {
    private static final Logger log = LoggerFactory.getLogger(Cen2PEPPOLBSITest.class);

    private Cen2PEPPOLBSI converter;
    private XPathFactory xPathFactory;
   
    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new DefaultEigorConfigurationLoader().loadConfiguration();
        converter = new Cen2PEPPOLBSI(new JavaReflections(), conf);
        converter.configure();
        xPathFactory = XPathFactory.newInstance();
    }
    
    
    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = createInvoice();
		BinaryConversionResult conversionResult = converter.convert(invoice);
        assertNotNull(conversionResult.getResult());
    }

    @Ignore
    @Test
    public void shouldApplyCustomMappings() throws Exception {
        byte[] peppolXML = converter.convert(createInvoice()).getResult();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(peppolXML));

        String invoiceNumber = getStringByXPath(doc, "/*[local-name()='Invoice']/*[name()='cbc:ID']/text()");
        assertNotNull(invoiceNumber);
        assertEquals("1", invoiceNumber);
    }



    @Ignore
    @Test
    public void testLineConverterCustomMappings() throws Exception {
        BG0000Invoice invoice = createInvoice();
        populateWithBG25(invoice);
        BinaryConversionResult conversionResult = converter.convert(invoice);
        byte[] ublXML = conversionResult.getResult();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(ublXML));

        String xPathLine = "/*[local-name()='Invoice']/*[name()='cac:InvoiceLine']" ;

        String invoiceLineNumber = getStringByXPath(doc, xPathLine + "/*[name()='cbc:ID']/text()");
        assertNotNull(invoiceLineNumber);
        assertEquals("9999", invoiceLineNumber);

        String itemNetPrice = getStringByXPath(doc, xPathLine + "/*[name()='cac:Price']/*[name()='cbc:PriceAmount']/text()");
        assertNotNull(itemNetPrice);
        assertEquals("20.00", itemNetPrice);

        String ItemPriceBaseQuantity = getStringByXPath(doc, xPathLine + "/*[name()='cac:Price']/*[name()='cbc:BaseQuantity']/text()");
        assertNotNull(ItemPriceBaseQuantity);
        assertEquals("1.00", ItemPriceBaseQuantity);

        String itemInformation = getStringByXPath(doc, xPathLine + "/*[name()='cac:Item']/*[name()='cbc:Name']/text()");
        assertNotNull(itemInformation);
        assertEquals("Name", itemInformation);
    }
    
    
    @Test
    public void shouldSupportPEPPOL() {
        assertThat(converter.support("peppolbis"), is(true));
    }
    
    
    @Test
    public void shouldSupportedFormatsPEPPOL() {
        assertThat(converter.getSupportedFormats(), contains("peppolbis"));
    }

    private BG0000Invoice createInvoice() {
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        return invoice;
    }

    private String getStringByXPath(Document doc, String xpath) throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xPath.compile(xpath);
        return (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
    }


    private void populateWithBG25(BG0000Invoice invoice) {
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBT0126InvoiceLineIdentifier().add(new BT0126InvoiceLineIdentifier("9999"));
        populateBG25WithBG29(invoiceLine);
        populateBG25WithBG31(invoiceLine);
        invoice.getBG0025InvoiceLine().add(invoiceLine);
    }

    private void populateBG25WithBG29(BG0025InvoiceLine invoiceLine) {
        BG0029PriceDetails priceDetails = new BG0029PriceDetails();
        priceDetails.getBT0146ItemNetPrice().add(new BT0146ItemNetPrice(new BigDecimal(20.0)));
        priceDetails.getBT0149ItemPriceBaseQuantity().add(new BT0149ItemPriceBaseQuantity(BigDecimal.ONE));
        priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(UnitOfMeasureCodes.EACH_EA));

        invoiceLine.getBG0029PriceDetails().add(priceDetails);
    }

    private void populateBG25WithBG31(BG0025InvoiceLine invoiceLine) {
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBT0153ItemName().add(new BT0153ItemName("Name"));

        invoiceLine.getBG0031ItemInformation().add(itemInformation);
    }
}
