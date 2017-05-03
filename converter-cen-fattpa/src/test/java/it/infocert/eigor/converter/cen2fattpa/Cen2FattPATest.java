package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.converter.csvcen2cen.CsvCen2Cen;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Cen2FattPATest {

    private CsvCen2Cen csvCen2Cen;
    private Cen2FattPAConverter cen2FattPA;
    private XPathFactory xPathfactory;


    @Before
    public void setUp() {
        csvCen2Cen = new CsvCen2Cen();
        cen2FattPA = new Cen2FattPAConverter();
        xPathfactory = XPathFactory.newInstance();
    }

    @Test
    public void checkFattPAXMLsimple() throws SyntaxErrorInInvoiceFormatException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        BG0000Invoice invoice = csvCen2Cen.convert(getClass().getClassLoader().getResourceAsStream("samplecen_simple.csv"));
        byte[] fattpaXML = cen2FattPA.convert(invoice).getResult();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));


        String invoiceNumber = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero/text()");
        assertThat("invoiceNumber", invoiceNumber, is("TOSL110"));

        String invoiceDate = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Data/text()");
        assertThat("invoiceDate", invoiceDate, is("2013-04-10"));

        String documentType = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/TipoDocumento/text()");
        assertThat("documentType", documentType, is("TD01"));

        String currencyCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Divisa/text()");
        assertThat("currencyCode", currencyCode, is("DKK"));

        String dueDate = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiPagamento/DettaglioPagamento/DataScadenzaPagamento/text()");
        assertThat("dueDate", dueDate, is("2013-05-10"));

        String sellerName = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione/text()");
        assertThat("sellerName", sellerName, is("SellerCompany"));

        String sellerVatIdCountry = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdPaese/text()");
        assertThat("sellerVatIdCountry", sellerVatIdCountry, is("IE"));

        String sellerVatIdCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice/text()");
        assertThat("sellerVatIdCode", sellerVatIdCode, is("123456789"));

        String sellerAddress = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Indirizzo/text()");
        assertThat("sellerAddress", sellerAddress, is("Indirizzo obbligatorio"));

        String sellerCity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Comune/text()");
        assertThat("sellerCity", sellerCity, is("comune obbligatorio"));

        String sellerPostCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/CAP/text()");
        assertThat("sellerPostCode", sellerPostCode, is("20100"));

        String sellerCountryCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CedentePrestatore/Sede/Nazione/text()");
        assertThat("sellerCountryCode", sellerCountryCode, is("DK"));

        String buyerName = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/Anagrafica/Denominazione/text()");
        assertThat("buyerName", buyerName, is("Buyercompany ltd"));

        String buyerVatIdCountry = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdPaese/text()");
        assertThat("buyerVatIdCountry", buyerVatIdCountry, is("DK"));

        String buyerVatIdCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/DatiAnagrafici/IdFiscaleIVA/IdCodice/text()");
        assertThat("buyerVatIdCode", buyerVatIdCode, is("12345678"));

        String buyerElectroAddress = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/CodiceDestinatario/text()");
        assertThat("buyerElectroAddress", buyerElectroAddress, is("UFF123"));

        String buyerIdScheme = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/FormatoTrasmissione/text()");
        assertThat("buyerIdScheme", buyerIdScheme, is("FPA12"));

        String buyerAddress = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Indirizzo/text()");
        assertThat("buyerAddress", buyerAddress, is("Indirizzo obbligatorio"));

        String buyerCity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Comune/text()");
        assertThat("buyerCity", buyerCity, is("comune obbligatorio"));

        String buyerPostCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/CAP/text()");
        assertThat("buyerPostCode", buyerPostCode, is("20100"));

        String buyerCountryCode = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaHeader/CessionarioCommittente/Sede/Nazione/text()");
        assertThat("buyerCountryCode", buyerCountryCode, is("DK"));

        String invoiceTotalWithVAT = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/ImportoTotaleDocumento/text()");
        assertThat("invoiceTotalWithVAT", invoiceTotalWithVAT, is("4675"));

        String invoiceAmountDue = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiPagamento/DettaglioPagamento/ImportoPagamento/text()");
        assertThat("invoiceAmountDue", invoiceAmountDue, is("4675"));

        String vat1TaxableAmount = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[1]/ImponibileImporto/text()");
        assertThat("vat1TaxableAmount", vat1TaxableAmount, is("1500"));

        String vat1Category = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[1]/Imposta/text()");
        assertThat("vat1Category", vat1Category, is("375"));

        String vat1Rate = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[1]/AliquotaIVA/text()");
        assertThat("vat1Rate", vat1Rate, is("0.25"));

        String vat2TaxableAmount = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[2]/ImponibileImporto/text()");
        assertThat("vat2TaxableAmount", vat2TaxableAmount, is("1500"));

        String vat2Category = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[2]/Imposta/text()");
        assertThat("vat2Category", vat2Category, is("375"));

        String vat2Rate = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DatiRiepilogo[2]/AliquotaIVA/text()");
        assertThat("vat2Rate", vat2Rate, is("0.25"));

        String line1Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/NumeroLinea/text()");
        assertThat("line1Number", line1Number, is("1"));

        String line1Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/Quantita/text()");
        assertThat("line1Quantity", line1Quantity, is("1000"));

        String line1UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/UnitaMisura/text()");
        assertThat("line1UnitOfMeasure", line1UnitOfMeasure, is("EA"));

        String line1TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/PrezzoTotale/text()");
        assertThat("line1TotalPrice", line1TotalPrice, is("1000"));

        String line1UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/PrezzoUnitario/text()");
        assertThat("line1UnitPrice", line1UnitPrice, is("1"));

        String line2Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/NumeroLinea/text()");
        assertThat("line2Number", line2Number, is("2"));

        String line2Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/Quantita/text()");
        assertThat("line2Quantity", line2Quantity, is("100"));

        String line2UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/UnitaMisura/text()");
        assertThat("line2UnitOfMeasure", line2UnitOfMeasure, is("EA"));

        String line2TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoTotale/text()");
        assertThat("line2TotalPrice", line2TotalPrice, is("500"));

        String line2UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoUnitario/text()");
        assertThat("line2UnitPrice", line2UnitPrice, is("5"));

        String line3Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/NumeroLinea/text()");
        assertThat("line3Number", line3Number, is("3"));

        String line3Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/Quantita/text()");
        assertThat("line3Quantity", line3Quantity, is("500"));

        String line3UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/UnitaMisura/text()");
        assertThat("line3UnitOfMeasure", line3UnitOfMeasure, is("EA"));

        String line3TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/PrezzoTotale/text()");
        assertThat("line3TotalPrice", line3TotalPrice, is("2500"));

        String line3UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/PrezzoUnitario/text()");
        assertThat("line3UnitPrice", line3UnitPrice, is("5"));

        String line4Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/NumeroLinea/text()");
        assertThat("line4Number", line4Number, is("4"));

        String line4Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/Quantita/text()");
        assertThat("line4Quantity", line4Quantity, is("0.5"));

        String line4UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/UnitaMisura/text()");
        assertThat("line4UnitOfMeasure", line4UnitOfMeasure, is("12.0 STL"));

        String line4TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/PrezzoTotale/text()");
        assertThat("line4TotalPrice", line4TotalPrice, is("5"));

        String line4UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/PrezzoUnitario/text()");
        assertThat("line4UnitPrice", line4UnitPrice, is("10"));

        String line4BaseUnit = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/AltriDatiGestionali/RiferimentoTesto/text()");
        assertThat("line4BaseUnit", line4BaseUnit, is("STL"));

        String line4BaseQty = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/AltriDatiGestionali/RiferimentoNumero/text()");
        assertThat("line4BaseQty", line4BaseQty, is("12"));
    }

    @Test
    public void checkFattPAXMLwithDiscount() throws SyntaxErrorInInvoiceFormatException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        BG0000Invoice invoice = csvCen2Cen.convert(getClass().getClassLoader().getResourceAsStream("samplecen_discount.csv"));
        byte[] fattpaXML = cen2FattPA.convert(invoice).getResult();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));


        String line1Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/NumeroLinea/text()");
        assertThat("line1Number", line1Number, is("1"));

        String line1Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/Quantita/text()");
        assertThat("line1Quantity", line1Quantity, is("4"));

        String line1UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/UnitaMisura/text()");
        assertThat("line1UnitOfMeasure", line1UnitOfMeasure, is("6.0 EA"));

        String line1TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/PrezzoTotale/text()");
        assertThat("line1TotalPrice", line1TotalPrice, is("2000"));

        String line1UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[1]/PrezzoUnitario/text()");
        assertThat("line1UnitPrice", line1UnitPrice, is("500"));

        String line2Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/NumeroLinea/text()");
        assertThat("line2Number", line2Number, is("2"));

        String line2Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/Quantita/text()");
        assertThat("line2Quantity", line2Quantity, is("1"));

        String line2UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/UnitaMisura/text()");
        assertThat("line2UnitOfMeasure", line2UnitOfMeasure, is("EA"));

        String line2TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoTotale/text()");
        assertThat("line2TotalPrice", line2TotalPrice, is("2"));

        String line2UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoUnitario/text()");
        assertThat("line2UnitPrice", line2UnitPrice, is("2"));

        String line3Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/NumeroLinea/text()");
        assertThat("line3Number", line3Number, is("3"));

        String line3Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/Quantita/text()");
        assertThat("line3Quantity", line3Quantity, is("1"));

        String line3UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/UnitaMisura/text()");
        assertThat("line3UnitOfMeasure", line3UnitOfMeasure, is("EA"));

        String line3TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/PrezzoTotale/text()");
        assertThat("line3TotalPrice", line3TotalPrice, is("-100"));

        String line3UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[3]/PrezzoUnitario/text()");
        assertThat("line3UnitPrice", line3UnitPrice, is("-100"));

        String line4Number = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/NumeroLinea/text()");
        assertThat("line4Number", line4Number, is("4"));

        String line4Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/Quantita/text()");
        assertThat("line4Quantity", line4Quantity, is("1"));

        String line4UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/UnitaMisura/text()");
        assertThat("line4UnitOfMeasure", line4UnitOfMeasure, is("EA"));

        String line4TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/PrezzoTotale/text()");
        assertThat("line4TotalPrice", line4TotalPrice, is("-2"));

        String line4UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/PrezzoUnitario/text()");
        assertThat("line4UnitPrice", line4UnitPrice, is("-2"));
    }

    @Test
    public void checkFattPAXMLwithLineLevelChargesOrDiscount() throws SyntaxErrorInInvoiceFormatException, ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        BG0000Invoice invoice = csvCen2Cen.convert(getClass().getClassLoader().getResourceAsStream("samplecen_line_charges.csv"));
        byte[] fattpaXML = cen2FattPA.convert(invoice).getResult();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(fattpaXML));

        // line 2 should be Line level surcharge for line 1

        String line2Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/Quantita/text()");
        assertThat("line2Quantity", line2Quantity, is("1"));

        String line2UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/UnitaMisura/text()");
        assertThat("line2UnitOfMeasure", line2UnitOfMeasure, is("EA"));

        String line2TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoTotale/text()");
        assertThat("line2TotalPrice", line2TotalPrice, is("100"));

        String line2UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[2]/PrezzoUnitario/text()");
        assertThat("line2UnitPrice", line2UnitPrice, is("100"));

        // line 4 should be Line level discount for line 3

        String line4Quantity = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/Quantita/text()");
        assertThat("line4Quantity", line4Quantity, is("1"));

        String line4UnitOfMeasure = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/UnitaMisura/text()");
        assertThat("line4UnitOfMeasure", line4UnitOfMeasure, is("EA"));

        String line4TotalPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/PrezzoTotale/text()");
        assertThat("line4TotalPrice", line4TotalPrice, is("-50"));

        String line4UnitPrice = getStringByXPath(doc, "/FatturaElettronica/FatturaElettronicaBody/DatiBeniServizi/DettaglioLinee[4]/PrezzoUnitario/text()");
        assertThat("line4UnitPrice", line4UnitPrice, is("-50"));
    }

    @Test
    public void shouldSupportCsvCen() {
        assertThat(cen2FattPA.support("cenfattpa"), is(true));
        assertThat(cen2FattPA.support("CenFattPA"), is(true));
        assertThat(cen2FattPA.support("xml"), is(false));
    }

    @Test
    public void testVatIdSplitting() {
        String[] testStrings = {
                "RO151590954",
                "IT 41440312",
                "IE123456Z89"};

        String[] expectedCountry = {
                "RO",
                "IT",
                "IE"};

        String[] expectedCodes = {
                "151590954",
                "41440312",
                "123456Z89"};

        for (int i = 0; i < testStrings.length; i++) {
            String testString = testStrings[i];
            assertTrue(expectedCountry[i].equals(Cen2FattPAConverterUtils.getCountryFromVATString(testString)));
            assertTrue(expectedCodes[i].equals(Cen2FattPAConverterUtils.getCodeFromVATString(testString)));
        }
    }

    @Test
    public void testNullOrEmptyVatIdSplitting() {

        assertTrue("".equals(Cen2FattPAConverterUtils.getCountryFromVATString("")));
        assertTrue("".equals(Cen2FattPAConverterUtils.getCodeFromVATString("")));

        assertTrue("".equals(Cen2FattPAConverterUtils.getCountryFromVATString(null)));
        assertTrue("".equals(Cen2FattPAConverterUtils.getCodeFromVATString(null)));
    }

    private String getStringByXPath(Document doc, String xpath) throws XPathExpressionException {
        XPathExpression xPathExpression = xPathfactory.newXPath().compile(xpath);
        return (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
    }

}