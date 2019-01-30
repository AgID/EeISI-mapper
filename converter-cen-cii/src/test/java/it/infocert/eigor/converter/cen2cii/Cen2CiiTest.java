package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.api.xml.DomUtils;
import it.infocert.eigor.converter.cen2ubl.ConverterUnitTest;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0002ProcessControl;
import it.infocert.eigor.model.core.model.BT0023BusinessProcessType;
import it.infocert.eigor.model.core.model.BT0024SpecificationIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class Cen2CiiTest extends ConverterUnitTest {
    private static final Logger log = LoggerFactory.getLogger(Cen2CiiTest.class);

    private Cen2Cii converter;

    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new DefaultEigorConfigurationLoader().loadConfiguration();
        converter = new Cen2Cii(new JavaReflections(), conf);
        converter.configure();
    }

    @Test
    public void eisi122_Bt23ShouldBeRenderedAsItIs() throws SyntaxErrorInInvoiceFormatException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        BG0000Invoice invoice = new BG0000Invoice();
        BG0002ProcessControl bg2 = new BG0002ProcessControl();
        bg2.getBT0023BusinessProcessType().add(new BT0023BusinessProcessType("this-is-bt23"));
        invoice.getBG0002ProcessControl().add(bg2);

        // when
        Document document = bytesToDom(converter.convert(invoice).getResult());
        Document dom = document;

        // then
        String profileId = getStringByXPath(dom, "/*[local-name()='CrossIndustryInvoice']" +
                "/*[local-name()='ExchangedDocumentContext']" +
                "/*[local-name()='BusinessProcessSpecifiedDocumentContextParameter']" +
                "/*[local-name()='ID']" +
                "/text()");

        assertEquals( DomUtils.toPrettyXml(dom), "this-is-bt23", profileId );

    }

    @Test
    public void eisi122_MissingBt23ShouldBeRenderedAsEmpty() throws SyntaxErrorInInvoiceFormatException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        // given an invoice without BT24
        BG0000Invoice invoice = new BG0000Invoice();
        BG0002ProcessControl bg2 = new BG0002ProcessControl();
        invoice.getBG0002ProcessControl().add(bg2);

        // when
        Document document = bytesToDom(converter.convert(invoice).getResult());
        Document dom = document;

        // then
        String profileId = getStringByXPath(dom, "/*[local-name()='CrossIndustryInvoice']" +
                "/*[local-name()='ExchangedDocumentContext']" +
                "/*[local-name()='BusinessProcessSpecifiedDocumentContextParameter']" +
                "/*[local-name()='ID']" +
                "/text()");

        assertEquals( DomUtils.toPrettyXml(dom), "", profileId );

    }

    @Test
    public void eisi122_MissingBt24ShouldBeRenderedAs_En16931_2017() throws SyntaxErrorInInvoiceFormatException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        // given an invoice without BT24
        BG0000Invoice invoice = new BG0000Invoice();
        BG0002ProcessControl bg2 = new BG0002ProcessControl();
        invoice.getBG0002ProcessControl().add(bg2);

        // when
        Document document = bytesToDom(converter.convert(invoice).getResult());
        Document dom = document;

        // then
        String profileId = getStringByXPath(dom, "/*[local-name()='CrossIndustryInvoice']" +
                "/*[local-name()='ExchangedDocumentContext']" +
                "/*[local-name()='GuidelineSpecifiedDocumentContextParameter']" +
                "/*[local-name()='ID']" +
                "/text()");

        assertEquals( DomUtils.toPrettyXml(dom), "urn:cen.eu:en16931:2017", profileId );

    }

    @Test
    public void eisi122_SpecifiedBt24ShouldBeRenderedAsItIs() throws SyntaxErrorInInvoiceFormatException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        // given an invoice without BT24
        BG0000Invoice invoice = new BG0000Invoice();
        BG0002ProcessControl bg2 = new BG0002ProcessControl();
        bg2.getBT0024SpecificationIdentifier().add(new BT0024SpecificationIdentifier("xyz"));
        invoice.getBG0002ProcessControl().add(bg2);

        // when
        Document document = bytesToDom(converter.convert(invoice).getResult());
        Document dom = document;

        // then
        String profileId = getStringByXPath(dom, "/*[local-name()='CrossIndustryInvoice']" +
                "/*[local-name()='ExchangedDocumentContext']" +
                "/*[local-name()='GuidelineSpecifiedDocumentContextParameter']" +
                "/*[local-name()='ID']" +
                "/text()");

        assertEquals( DomUtils.toPrettyXml(dom), "xyz", profileId );

    }

    @Test
    public void shouldSupportCii() {
        assertThat(converter.support("cii"), is(true));
    }

    @Test
    public void shouldSupportedFormatsCii() {
        assertThat(converter.getSupportedFormats(), contains("cii"));
    }
}
