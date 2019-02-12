package it.infocert.eigor.converter.cen2ublcn;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class CreditNoteLineConverterTest {
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = new Document(new Element("CreditNote", Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2")));
    }

    @Test
    public void invoiceLineWithBT0128shouldHaveDocumentReferenceAndTypeCode() throws Exception {
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0128();
        CreditNoteLineConverter converter = new CreditNoteLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT, null);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("CreditNoteLine");

        Element documentReference = invoiceLine.getChild("DocumentReference");

        Element id = documentReference.getChild("ID");
        assertTrue("001".equals(id.getText()));
        assertTrue("321".equals(id.getAttribute("schemeID").getValue()));

        Element documentTypeCode = documentReference.getChild("DocumentTypeCode");
        assertTrue("130".equals(documentTypeCode.getText()));
    }

    @Test
    public void invoiceLineWithBT0149shouldQuantityDividedByBaseQuantity() throws Exception {
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0129AndBT0149();
        CreditNoteLineConverter converter = new CreditNoteLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT, null);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("CreditNoteLine");

        Element invoicedQuantity = invoiceLine.getChild("CreditedQuantity");

        assertTrue(invoicedQuantity.getText().equals("1.00000000"));
    }


    private BG0000Invoice makeCenInvoiceWithBT0128() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier("321", "001"));
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
