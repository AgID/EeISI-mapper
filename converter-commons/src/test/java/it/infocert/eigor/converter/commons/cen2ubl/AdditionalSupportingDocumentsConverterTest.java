package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AdditionalSupportingDocumentsConverterTest {

    @Test
    public void additionalDocumentReferenceDocumentTypeCodeShouldBe916IfBG0024hasBT0122ForCII() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBT0122();
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.CII_OUT);

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");
        Element documentTypeCode = additionalSupportingDocuments.getChild("DocumentTypeCode");
        assertTrue("916".equals(documentTypeCode.getText()));
    }

    @Test
    public void additionalDocumentReferenceDocumentTypeCodeShouldBe130IfBT0018() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBT0122();
        invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add(new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(new Identifier("AED", "TESTID")));
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");

        Element documentTypeCode = additionalSupportingDocuments.getChild("DocumentTypeCode");
        assertTrue("130".equals(documentTypeCode.getValue()));

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
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");
        Element documentDescription = additionalSupportingDocuments.getChild("DocumentDescription");
        assertTrue("TEST".equals(documentDescription.getText()));
    }

    @Test
    public void shouldMapBT0125() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBT0125();
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element additionalDocumentReference = document.getRootElement().getChild("AdditionalDocumentReference");
        Element attachment = additionalDocumentReference.getChild("Attachment");
        Element embeddedBinaryObject = attachment.getChild("EmbeddedDocumentBinaryObject");
        assertTrue("TESTCONTENT".equals(embeddedBinaryObject.getText()));
        assertThat(embeddedBinaryObject.getAttribute("filename").getValue(), is("test.csv"));
        assertThat(embeddedBinaryObject.getAttribute("mimeCode").getValue(), is("text/csv"));
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

    private BG0000Invoice createInvoiceWithBT0125() throws ConversionFailedException {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0024AdditionalSupportingDocuments bg0024 = new BG0024AdditionalSupportingDocuments();

        TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(
                DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.UBL_OUT);

        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 =
                new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(
                        strToBinConverter.convert(
                new Element("Attachment")
                        .setText("TESTCONTENT")
                        .setAttribute("mimeCode", "text/csv")
                        .setAttribute("filename", "test.csv")));

        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
        invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);

        return invoice;
    }

}