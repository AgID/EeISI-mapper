package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import org.junit.Test;

import java.io.InputStream;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UblUblcnPeppolbisPeppolcnFieldMappingTest extends AbstractIssueTest {

    @Test
    public void cenToPeppolbisMapping() throws Exception {
        InputStream inputFatturaCenXml = invoiceAsStream("/issues/issue-generic-check-not-mapped-fields-xmlcen.xml");
        ConversionResult<byte[]> convert = api.convert("xmlcen", "peppolbis", inputFatturaCenXml);
        String evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='ProjectReference']//*[local-name()='ID']/text()");
        assertEquals("456", evaluate);
        String invoice = new String(convert.getResult());

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='InvoiceTypeCode']/text()");
        assertEquals("380", evaluate);

        // start BG-13
        assertTrue(invoice.contains("<cac:Delivery"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='DeliveryParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Deliver to party name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/text()");
        assertEquals("deliver location identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0045", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='ActualDeliveryDate']/text()");
        assertEquals("2018-12-04", evaluate);
        // end BG-13

        // start BG-11
        assertTrue(invoice.contains("<cac:TaxRepresentativeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Tax representative name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyTaxScheme']//*[local-name()='CompanyID']/text()");
        assertEquals("DE3949053", evaluate);
        assertTrue(invoice.contains("<cac:PostalAddress"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='StreetName']/text()");
        assertEquals("Tax representative address line 1", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AdditionalStreetName']/text()");
        assertEquals("Tax representative address line 2", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AddressLine']//*[local-name()='Line']/text()");
        assertEquals("Tax representative address line 3", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CityName']/text()");
        assertEquals("Tax representative city", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='PostalZone']/text()");
        assertEquals("23455", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CountrySubentity']/text()");
        assertEquals("Tax representative country subdivision", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='Country']//*[local-name()='IdentificationCode']/text()");
        assertEquals("DE", evaluate);
        // end BG-11

        // start BG-10
        assertTrue(invoice.contains("<cac:PayeeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Payee name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/text()");
        assertEquals("Payee identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0098", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/text()");
        assertEquals("Payee legal registration identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/@schemeID");
        assertEquals("0099", evaluate);
        // end BG-10

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='DueDate']/text()");
        assertEquals("2018-11-30", evaluate);
    }

    @Test
    public void cenToPeppolcnMapping() throws Exception {
        InputStream inputFatturaCenXml = invoiceAsStream("/issues/issue-generic-check-not-mapped-fields-xmlcen.xml");
        ConversionResult<byte[]> convert = api.convert("xmlcen", "peppolcn", inputFatturaCenXml);
        String evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='AdditionalDocumentReference']//*[local-name()='ID']/text()");
        assertEquals("456", evaluate);
        String invoice = new String(convert.getResult());

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='CreditNoteTypeCode']/text()");
        assertEquals("380", evaluate);


        // start BG-13
        assertTrue(invoice.contains("<cac:Delivery"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='DeliveryParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Deliver to party name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/text()");
        assertEquals("deliver location identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0045", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='ActualDeliveryDate']/text()");
        assertEquals("2018-12-04", evaluate);
        // end BG-13

        // start BG-11
        assertTrue(invoice.contains("<cac:TaxRepresentativeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Tax representative name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyTaxScheme']//*[local-name()='CompanyID']/text()");
        assertEquals("DE3949053", evaluate);
        assertTrue(invoice.contains("<cac:PostalAddress"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='StreetName']/text()");
        assertEquals("Tax representative address line 1", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AdditionalStreetName']/text()");
        assertEquals("Tax representative address line 2", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AddressLine']//*[local-name()='Line']/text()");
        assertEquals("Tax representative address line 3", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CityName']/text()");
        assertEquals("Tax representative city", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='PostalZone']/text()");
        assertEquals("23455", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CountrySubentity']/text()");
        assertEquals("Tax representative country subdivision", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='Country']//*[local-name()='IdentificationCode']/text()");
        assertEquals("DE", evaluate);
        // end BG-11

        // start BG-10
        assertTrue(invoice.contains("<cac:PayeeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Payee name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/text()");
        assertEquals("Payee identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0098", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/text()");
        assertEquals("Payee legal registration identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/@schemeID");
        assertEquals("0099", evaluate);
        // end BG-10

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PaymentMeans']//*[local-name()='PaymentDueDate']/text()");
        assertEquals("2018-11-30", evaluate);
    }

    @Test
    public void cenToUblMapping() throws Exception {
        InputStream inputFatturaCenXml = invoiceAsStream("/issues/issue-generic-check-not-mapped-fields-xmlcen.xml");
        ConversionResult<byte[]> convert = api.convert("xmlcen", "ubl", inputFatturaCenXml);
        String evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='ProjectReference']//*[local-name()='ID']/text()");
        assertEquals("456", evaluate);
        String invoice = new String(convert.getResult());

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='InvoiceTypeCode']/text()");
        assertEquals("380", evaluate);

        // start BG-13
        assertTrue(invoice.contains("<cac:Delivery"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='DeliveryParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Deliver to party name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/text()");
        assertEquals("deliver location identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0045", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='Delivery']//*[local-name()='ActualDeliveryDate']/text()");
        assertEquals("2018-12-04", evaluate);
        // end BG-13

        // start BG-11
        assertTrue(invoice.contains("<cac:TaxRepresentativeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Tax representative name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyTaxScheme']//*[local-name()='CompanyID']/text()");
        assertEquals("DE3949053", evaluate);
        assertTrue(invoice.contains("<cac:PostalAddress"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='StreetName']/text()");
        assertEquals("Tax representative address line 1", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AdditionalStreetName']/text()");
        assertEquals("Tax representative address line 2", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AddressLine']//*[local-name()='Line']/text()");
        assertEquals("Tax representative address line 3", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CityName']/text()");
        assertEquals("Tax representative city", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='PostalZone']/text()");
        assertEquals("23455", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CountrySubentity']/text()");
        assertEquals("Tax representative country subdivision", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='Country']//*[local-name()='IdentificationCode']/text()");
        assertEquals("DE", evaluate);
        // end BG-11

        // start BG-10
        assertTrue(invoice.contains("<cac:PayeeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Payee name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/text()");
        assertEquals("Payee identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0098", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/text()");
        assertEquals("Payee legal registration identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/@schemeID");
        assertEquals("0099", evaluate);
        // end BG-10

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='Invoice']//*[local-name()='DueDate']/text()");
        assertEquals("2018-11-30", evaluate);
    }

    @Test
    public void cenToUblcnMapping() throws Exception {
        InputStream inputFatturaCenXml = invoiceAsStream("/issues/issue-generic-check-not-mapped-fields-xmlcen.xml");
        ConversionResult<byte[]> convert = api.convert("xmlcen", "ublcn", inputFatturaCenXml);
        String evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='AdditionalDocumentReference']//*[local-name()='ID']/text()");
        assertEquals("456", evaluate);
        String invoice = new String(convert.getResult());

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='CreditNoteTypeCode']/text()");
        assertEquals("380", evaluate);

        // start BG-13
        assertTrue(invoice.contains("<cac:Delivery"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='DeliveryParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Deliver to party name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/text()");
        assertEquals("deliver location identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='DeliveryLocation']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0045", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='Delivery']//*[local-name()='ActualDeliveryDate']/text()");
        assertEquals("2018-12-04", evaluate);
        // end BG-13

        // start BG-11
        assertTrue(invoice.contains("<cac:TaxRepresentativeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Tax representative name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PartyTaxScheme']//*[local-name()='CompanyID']/text()");
        assertEquals("DE3949053", evaluate);
        assertTrue(invoice.contains("<cac:PostalAddress"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='StreetName']/text()");
        assertEquals("Tax representative address line 1", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AdditionalStreetName']/text()");
        assertEquals("Tax representative address line 2", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='AddressLine']//*[local-name()='Line']/text()");
        assertEquals("Tax representative address line 3", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CityName']/text()");
        assertEquals("Tax representative city", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='PostalZone']/text()");
        assertEquals("23455", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='CountrySubentity']/text()");
        assertEquals("Tax representative country subdivision", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='TaxRepresentativeParty']" +
                "//*[local-name()='PostalAddress']//*[local-name()='Country']//*[local-name()='IdentificationCode']/text()");
        assertEquals("DE", evaluate);
        // end BG-11

        // start BG-10
        assertTrue(invoice.contains("<cac:PayeeParty"));
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']" +
                "//*[local-name()='PartyName']//*[local-name()='Name']/text()");
        assertEquals("Payee name", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/text()");
        assertEquals("Payee identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyIdentification']" +
                "//*[local-name()='ID']/@schemeID");
        assertEquals("0098", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/text()");
        assertEquals("Payee legal registration identifier", evaluate);
        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PayeeParty']//*[local-name()='PartyLegalEntity']" +
                "//*[local-name()='CompanyID']/@schemeID");
        assertEquals("0099", evaluate);
        // end BG-10

        evaluate = evalXpathExpressionAsString(convert, "//*[local-name()='CreditNote']//*[local-name()='PaymentMeans']//*[local-name()='PaymentDueDate']/text()");
        assertEquals("2018-11-30", evaluate);
    }
}
