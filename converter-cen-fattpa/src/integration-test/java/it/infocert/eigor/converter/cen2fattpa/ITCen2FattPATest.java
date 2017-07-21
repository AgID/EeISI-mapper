package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.converter.csvcen2cen.CsvCen2Cen;
import it.infocert.eigor.model.core.dump.DumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore //TODO fix the xpaths with the new line converter
public class ITCen2FattPATest {

    private final Reflections reflections = new Reflections("it.infocert");
    private CsvCen2Cen csvCen2Cen;
    private Cen2FattPA cen2FattPA;
    private XPathFactory xPathfactory;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp() throws ConfigurationException {
        csvCen2Cen = new CsvCen2Cen(reflections);
        cen2FattPA = new Cen2FattPA(reflections, new DefaultEigorConfigurationLoader().loadConfiguration());
        cen2FattPA.configure();
        xPathfactory = XPathFactory.newInstance();
    }

    @Test
    public void checkFattPAXMLwithDiscount() throws SyntaxErrorInInvoiceFormatException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        BG0000Invoice invoice = csvCen2Cen.convert(getClass().getClassLoader().getResourceAsStream("samplecen_discount.csv")).getResult();

        dumpInvoice(invoice);

        byte[] fattpaXML = cen2FattPA.convert(invoice).getResult();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));

        String invoiceNumber = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero/text()");
        assertThat("invoiceNumber", invoiceNumber, is("TOSL110"));

        String invoiceDate = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data/text()");
        assertThat("invoiceDate", invoiceDate, is("2013-04-10"));

        String documentType = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/TipoDocumento/text()");
        assertThat("documentType", documentType, is("TD01"));

        String currencyCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Divisa/text()");
        assertThat("currencyCode", currencyCode, is("DKK"));

        String dueDate = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiPagamento/DettaglioPagamento/DataScadenzaPagamento/text()");
        assertThat("dueDate", dueDate, is("2013-05-10"));

        String sellerName = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione/text()");
        assertThat("sellerName", sellerName, is("SellerCompany"));

        String sellerVatIdCountry = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdPaese/text()");
        assertThat("sellerVatIdCountry", sellerVatIdCountry, is("IE"));

        String sellerVatIdCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice/text()");
        assertThat("sellerVatIdCode", sellerVatIdCode, is("123456789"));

        String sellerAddress = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo/text()");
        assertThat("sellerAddress", sellerAddress, is("Indirizzo obbligatorio"));

        String sellerCity = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Comune/text()");
        assertThat("sellerCity", sellerCity, is("comune obbligatorio"));

        String sellerPostCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/CAP/text()");
        assertThat("sellerPostCode", sellerPostCode, is("20100"));

        String sellerCountryCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CedentePrestatore/Sede/Nazione/text()");
        assertThat("sellerCountryCode", sellerCountryCode, is("DK"));

        String buyerName = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Denominazione/text()");
        assertThat("buyerName", buyerName, is("Buyercompany ltd"));

        String buyerVatIdCountry = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdPaese/text()");
        assertThat("buyerVatIdCountry", buyerVatIdCountry, is("DK"));

        String buyerVatIdCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdCodice/text()");
        assertThat("buyerVatIdCode", buyerVatIdCode, is("12345678"));

        String buyerElectroAddress = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/DatiTrasmissione/CodiceDestinatario/text()");
        assertThat("buyerElectroAddress", buyerElectroAddress, is("UFF123"));

        String buyerIdScheme = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/DatiTrasmissione/FormatoTrasmissione/text()");
        assertThat("buyerIdScheme", buyerIdScheme, is("FPA12"));

        String buyerAddress = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Indirizzo/text()");
        assertThat("buyerAddress", buyerAddress, is("Indirizzo obbligatorio"));

        String buyerCity = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune/text()");
        assertThat("buyerCity", buyerCity, is("comune obbligatorio"));

        String buyerPostCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP/text()");
        assertThat("buyerPostCode", buyerPostCode, is("20100"));

        String buyerCountryCode = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione/text()");
        assertThat("buyerCountryCode", buyerCountryCode, is("DK"));

        String invoiceTotalWithVAT = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento/text()");
        assertThat("invoiceTotalWithVAT", invoiceTotalWithVAT, is("2280.00"));

        String invoiceAmountDue = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiPagamento/DettaglioPagamento/ImportoPagamento/text()");
        assertThat("invoiceAmountDue", invoiceAmountDue, is("2280.00"));

        String vat1TaxableAmount = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[1]/ImponibileImporto/text()");
        assertThat("vat1TaxableAmount", vat1TaxableAmount, is("1900.00"));

        String vat1Category = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[1]/Imposta/text()");
        assertThat("vat1Category", vat1Category, is("380.00"));

        String vat1Rate = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[1]/AliquotaIVA/text()");
        assertThat("vat1Rate", vat1Rate, is("20.00"));

        String vat2TaxableAmount = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[2]/ImponibileImporto/text()");
        assertThat("vat2TaxableAmount", vat2TaxableAmount, is("1500.00"));

        String vat2Category = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[2]/Imposta/text()");
        assertThat("vat2Category", vat2Category, is("375.00"));

        String line1Number = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/NumeroLinea/text()");
        assertThat("line1Number", line1Number, is("1"));

        String line1Quantity = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/Quantita/text()");
        assertThat("line1Quantity", line1Quantity, is("4.00"));

        String line1UnitOfMeasure = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/UnitaMisura/text()");
        assertThat("line1UnitOfMeasure", line1UnitOfMeasure, is("6.0 EA"));

        String line1TotalPrice = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoTotale/text()");
        assertThat("line1TotalPrice", line1TotalPrice, is("2000.00"));

        String line1UnitPrice = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoUnitario/text()");
        assertThat("line1UnitPrice", line1UnitPrice, is("500.00"));

        String line2Number = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/NumeroLinea/text()");
        assertThat("line2Number", line2Number, is("2"));

        String line2Quantity = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/Quantita/text()");
        assertThat("line2Quantity", line2Quantity, is("1.00"));

        String line2UnitOfMeasure = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/UnitaMisura/text()");
        assertThat("line2UnitOfMeasure", line2UnitOfMeasure, is("EA"));

        String line2TotalPrice = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoTotale/text()");
        assertThat("line2TotalPrice", line2TotalPrice, is("2.00"));

        String line2UnitPrice = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoUnitario/text()");
        assertThat("line2UnitPrice", line2UnitPrice, is("2.00"));

        String causale = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Causale[1]/text()");
        assertThat("Causale", causale, is("Terms Code Note prova@pec.it buyer@mail.com Credit Card"));
    }


    private void dumpInvoice(BG0000Invoice invoice) {
        if (log.isDebugEnabled()) {
            Visitor v = new DumpVisitor();
            invoice.accept(v);
            log.debug(v.toString());
        }
    }

    @Test
    public void checkFattPAXMLwithLineLevelChargesOrDiscount() throws SyntaxErrorInInvoiceFormatException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        BG0000Invoice invoice = csvCen2Cen.convert(getClass().getClassLoader().getResourceAsStream("samplecen_line_charges.csv")).getResult();
        byte[] fattpaXML = cen2FattPA.convert(invoice).getResult();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));

        // line 2 should be Line level surcharge for line 1

        String line2Quantity = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/Quantita/text()");
        assertThat("line2Quantity", line2Quantity, is("100.00"));

        String line2UnitOfMeasure = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/UnitaMisura/text()");
        assertThat("line2UnitOfMeasure", line2UnitOfMeasure, is("EA"));

        String line2TotalPrice = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoTotale/text()");
        assertThat("line2TotalPrice", line2TotalPrice, is("500.00"));

        String line2UnitPrice = getStringByXPath(doc, "/*[local-name()='FatturaElettronica']/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoUnitario/text()");
        assertThat("line2UnitPrice", line2UnitPrice, is("5.00"));

    }

    private String getStringByXPath(Document doc, String xpath) throws XPathExpressionException {
        XPath xPath = xPathfactory.newXPath();
        XPathExpression xPathExpression = xPath.compile(xpath);
        return (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
    }
}