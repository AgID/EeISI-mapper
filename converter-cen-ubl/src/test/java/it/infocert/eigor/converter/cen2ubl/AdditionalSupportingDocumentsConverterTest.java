package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AdditionalSupportingDocumentsConverterTest {

    @Test
    public void additionalDocumentReferenceDocumentTypeCodeShouldBe916IfBG0024hasBT0122() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBT0122();
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.newArrayList());

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");
        Element documentTypeCode = additionalSupportingDocuments.getChild("DocumentTypeCode");
        assertTrue("916".equals(documentTypeCode.getText()));
    }

    @Test
    public void additionalDocumentReferenceDocumentTypeCodeShouldBe130IfBG0024hasBT0018() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBT0122();
        invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add(new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(new Identifier("AED", "TESTID")));
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.newArrayList());

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");

        Element documentTypeCode = additionalSupportingDocuments.getChild("DocumentTypeCode");
        assertTrue("130".equals(documentTypeCode.getText()));

        Element id = additionalSupportingDocuments.getChild("ID");
        assertTrue("TESTID".equals(id.getValue()));

        String schemeID = id.getAttributeValue("schemeID");
        assertTrue("AED".equals(schemeID));
    }

    @Test
    public void documentDescriptionMustExistIfBG0024hasBT0123() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBT0123();
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.newArrayList());

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");
        Element documentDescription = additionalSupportingDocuments.getChild("DocumentDescription");
        assertTrue("TEST".equals(documentDescription.getText()));
    }

    private BG0000Invoice createInvoiceWithBT0122() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0024AdditionalSupportingDocuments bg0024 = new BG0024AdditionalSupportingDocuments();
        BT0122SupportingDocumentReference bt0122 = new BT0122SupportingDocumentReference("TESTID");
        bg0024.getBT0122SupportingDocumentReference().add(bt0122);
        invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);

        return invoice;
    }

    private BG0000Invoice createInvoiceWithBT0123() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0024AdditionalSupportingDocuments bg0024 = new BG0024AdditionalSupportingDocuments();
        BT0123SupportingDocumentDescription bt0123 = new BT0123SupportingDocumentDescription("TEST");
        bg0024.getBT0123SupportingDocumentDescription().add(bt0123);
        invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);

        return invoice;
    }

}