package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BT0021InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class InvoiceNoteConverterTest {

    @Test
    public void shouldHaveBT0021IfInvoiceNoteHasPrependedSubjectCode() throws Exception {
        Document document = makeDocumentWithInvoiceNote("#TESTSUBJECTCODE#TESTNOTE");
        InvoiceNoteConverter converter = new InvoiceNoteConverter();
        BG0000Invoice invoice = new BG0000Invoice();
        converter.map(invoice, document, new ArrayList<IConversionIssue>());
        BT0021InvoiceNoteSubjectCode bt0021 = invoice.getBG0001InvoiceNote(0).getBT0021InvoiceNoteSubjectCode(0);
        BT0022InvoiceNote bt0022 = invoice.getBG0001InvoiceNote(0).getBT0022InvoiceNote(0);

        assertEquals("TESTSUBJECTCODE", bt0021.getValue());
        assertEquals("TESTNOTE", bt0022.getValue());
    }

    @Test
    public void shouldNotHaveBT0021IfInvoiceNoteHasNoPrependedSubjectCode() throws Exception {
        Document document = makeDocumentWithInvoiceNote("TESTNOTE");
        InvoiceNoteConverter converter = new InvoiceNoteConverter();
        BG0000Invoice invoice = new BG0000Invoice();
        converter.map(invoice, document, new ArrayList<IConversionIssue>());
        BG0001InvoiceNote bg0001 = invoice.getBG0001InvoiceNote(0);
        BT0022InvoiceNote bt0022 = bg0001.getBT0022InvoiceNote(0);

        assertEquals("TESTNOTE", bt0022.getValue());
        assertEquals(true, bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty());
    }


    private Document makeDocumentWithInvoiceNote(String noteText) {
        Document document = new Document();
        Namespace defaultNs = Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        Namespace cacNs = Namespace.getNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        Namespace cbcNs = Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        Element rootElement = new Element("Invoice", defaultNs);
        rootElement.addNamespaceDeclaration(defaultNs);
        rootElement.addNamespaceDeclaration(cacNs);
        rootElement.addNamespaceDeclaration(cbcNs);
        document.setRootElement(rootElement);

        Element note = new Element("Note", cbcNs);

        note.setText(noteText);
        rootElement.addContent(note);

        return document;
    }
}