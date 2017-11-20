package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import it.infocert.eigor.model.core.model.BT0123SupportingDocumentDescription;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AdditionalSupportingDocumentsConverterTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = createInvoiceWithBT0123();
    }

    @Test
    public void documentDescriptionMustExistIfBG0024hasBT0123() throws Exception {
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        Document document = new Document(new Element("Invoice"));
        converter.map(invoice, document, Lists.newArrayList());

        Element additionalSupportingDocuments = document.getRootElement().getChild("AdditionalDocumentReference");
        Element documentDescription = additionalSupportingDocuments.getChild("DocumentDescription");
        assertTrue("TEST".equals(documentDescription.getText()));
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