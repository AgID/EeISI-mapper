package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class Cen2UblTest {
    private static final Logger log = LoggerFactory.getLogger(Cen2UblTest.class);

    private Cen2Ubl converter;
    private XPathFactory xPathFactory;

    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new DefaultEigorConfigurationLoader().loadConfiguration();
        converter = new Cen2Ubl(new Reflections("it.infocert"), conf);
        converter.configure();
        xPathFactory = XPathFactory.newInstance();
    }

    @Test
    public void shouldApplyCustomMappings() throws Exception {
        byte[] ublXML = converter.convert(createInvoice()).getResult();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(ublXML));

        String invoiceNumber = getStringByXPath(doc, "/*[local-name()='Invoice']/ID/text()");
        assertNotNull(invoiceNumber);
        assertEquals("1", invoiceNumber);
    }

    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        BinaryConversionResult conversionResult = converter.convert(createInvoice());
        assertNotNull(conversionResult.getResult());
    }

    @Test
    public void shouldSupportUbl(){
        assertThat(converter.support("ubl"), is(true));
    }

    @Test
    public void shouldSupportedFormatsUbl(){
        assertThat(converter.getSupportedFormats(), contains("ubl"));
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
}
