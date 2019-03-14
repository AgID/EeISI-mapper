package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.api.xml.DomUtils;
import it.infocert.eigor.converter.cen2ubl.ConverterUnitTest;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class Cen2FattPATest extends ConverterUnitTest {
    private static final Logger log = LoggerFactory.getLogger(Cen2FattPATest.class);

    private Cen2FattPA converter;

    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new DefaultEigorConfigurationLoader().loadConfiguration();
        converter = new Cen2FattPA(new JavaReflections(), conf);
        converter.configure();
    }

    @Ignore("waiting for TD fix")
    @Test
    public void eisi26Bt3ToTipoDocumentShouldBeMapped() throws Exception{

        // given
        BG0000Invoice invoice = new BG0000Invoice();
        invoice
                .getBT0003InvoiceTypeCode()
                .add(new BT0003InvoiceTypeCode(Untdid1001InvoiceTypeCode.Code380));


        // when
        byte[] fattpaXML = converter.convert(invoice).getResult();

        // then
        Document dom = factory.newDocumentBuilder().parse(new ByteArrayInputStream(fattpaXML));

        String stringByXPath = getStringByXPath(dom,
                "/*[local-name()='FatturaElettronica']" +
                        "/*[local-name()='FatturaElettronicaBody']" +
                        "/*[local-name()='DatiGenerali']" +
                        "/*[local-name()='DatiGeneraliDocumento']" +
                        "/*[local-name()='TipoDocumento']/text()"
        );

        assertEquals("TD01", stringByXPath);

    }

    @Test
    public void shouldApplyCustomMappings() throws Exception {
        byte[] fattpaXML = converter.convert(createInvoice()).getResult();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));

        String lineNumber = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/NumeroLinea/text()");

        getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici[1]");

        assertNotNull(lineNumber);
        assertEquals(DomUtils.toPrettyXml(doc), "1", lineNumber);
    }

    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        BinaryConversionResult conversionResult = converter.convert(createInvoice());

        assertNotNull(conversionResult.getResult());
    }

    @Test
    public void checkBt32CodiceFiscale() throws Exception{
        byte[] fattpaXML = converter.convert(createInvoice()).getResult();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));

        String bt32 = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici[1]/CodiceFiscale");

        assertNotNull(bt32);
        assertEquals("01234567", bt32);
    }

    @Test
    public void shouldSupportFatturaPA() {
        assertThat(converter.support("fatturapa"), is(true));
    }

    @Test
    public void shouldSupportedFormatsFatturaPA() {
        assertThat(converter.getSupportedFormats(), contains("fatturapa"));
    }

    private BG0000Invoice createInvoice() {
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("seller-name"));
        seller.getBT0032SellerTaxRegistrationIdentifier().add(new BT0032SellerTaxRegistrationIdentifier("01234567"));

        final BG0005SellerPostalAddress sellerPostalAddress = new BG0005SellerPostalAddress();
        sellerPostalAddress.getBT0040SellerCountryCode().add(new BT0040SellerCountryCode(Iso31661CountryCodes.IT));
        seller.getBG0005SellerPostalAddress().add(sellerPostalAddress);
        invoice.getBG0004Seller().add(seller);

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBT0126InvoiceLineIdentifier().add(new BT0126InvoiceLineIdentifier("1"));
        BG0029PriceDetails priceDetails = new BG0029PriceDetails();
        priceDetails.getBT0149ItemPriceBaseQuantity().add(new BT0149ItemPriceBaseQuantity(BigDecimal.ONE));
        priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(UnitOfMeasureCodes.ACRE_ACR));
        invoiceLine.getBG0029PriceDetails().add(priceDetails);
        invoiceLine.getBT0131InvoiceLineNetAmount().add(new BT0131InvoiceLineNetAmount(new BigDecimal("12")));
        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }



}
