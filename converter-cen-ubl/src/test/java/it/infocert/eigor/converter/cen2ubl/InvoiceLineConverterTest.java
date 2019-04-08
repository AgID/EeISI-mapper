package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2ubl.InvoiceLineConverter;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvoiceLineConverterTest {
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = new Document(new Element("Invoice", Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")));
    }

    @Test
    public void invoiceLineWithBT0128shouldHaveDocumentReferenceWithDefaultSchemeIdAndTypeCode() throws Exception {

        // given
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0128("321", "001");
        InvoiceLineConverter converter = new InvoiceLineConverter();

        // when
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);

        // then
        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");

        Element documentReference = invoiceLine.getChild("DocumentReference");

        Element id = documentReference.getChild("ID");
        assertEquals("001", id.getText());
        assertEquals("ZZZ", id.getAttribute("schemeID").getValue() );

        Element documentTypeCode = documentReference.getChild("DocumentTypeCode");
        assertEquals("130", documentTypeCode.getText());
    }

    @Test
    public void invoiceLineWithBT0128shouldHaveDocumentReferenceWithProperSchemeIdAndTypeCode() throws Exception {

        // given
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0128("AGN", "001");
        InvoiceLineConverter converter = new InvoiceLineConverter();

        // when
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.PEPPOL_OUT, null);

        // then
        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");

        Element documentReference = invoiceLine.getChild("DocumentReference");

        Element id = documentReference.getChild("ID");
        assertEquals("001", id.getText());
        assertEquals("AGN", id.getAttribute("schemeID").getValue() );

        Element documentTypeCode = documentReference.getChild("DocumentTypeCode");
        assertEquals("130", documentTypeCode.getText());
    }



    @Test
    public void invoiceQuantitiesShouldBeMapped() throws Exception {
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0129AndBT0149();
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<>(), ErrorCode.Location.UBL_OUT, null);
        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");
        Element invoicedQuantity = invoiceLine.getChild("InvoicedQuantity");
        Element price = invoiceLine.getChild("Price");
        Element baseQuantity = price.getChild("BaseQuantity");
        Assert.assertEquals("2.00000000", invoicedQuantity.getText());
        Assert.assertEquals("2.00", baseQuantity.getText());
    }


    private BG0000Invoice makeCenInvoiceWithBT0128(String identificationSchema, String identifier) {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier(identificationSchema, identifier));
        invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);

        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }

    private BG0000Invoice makeCenInvoiceWithBT0129AndBT0149() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BT0129InvoicedQuantity bt0129InvoicedQuantity = new BT0129InvoicedQuantity(new BigDecimal(2));
        invoiceLine.getBT0129InvoicedQuantity().add(bt0129InvoicedQuantity);

        BG0029PriceDetails bg0029PriceDetails = new BG0029PriceDetails();
        BT0149ItemPriceBaseQuantity bt0149ItemPriceBaseQuantity = new BT0149ItemPriceBaseQuantity(new BigDecimal(2));
        bg0029PriceDetails.getBT0149ItemPriceBaseQuantity().add(bt0149ItemPriceBaseQuantity);
        invoiceLine.getBG0029PriceDetails().add(bg0029PriceDetails);

        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }
}
