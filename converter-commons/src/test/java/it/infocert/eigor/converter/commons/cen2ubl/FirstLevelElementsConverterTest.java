package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BT0021InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class FirstLevelElementsConverterTest {


    @Test
    public void InvoiceNoteShouldHavePrependedSubjectCodeIfBT0021(){
        BG0000Invoice invoice = createInvoiceWithBG0001InvoiceNoteAndBT0022(true);
        Document document = new Document(new Element("Invoice"));

        NoteConverter converter = new NoteConverter();
        converter.map(invoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT);

        Element note = document.getRootElement().getChild("Note");
        String noteText = note.getText();

        assertEquals("#TESTSUBJECTCODE#TESTNOTE", noteText);
    }

    @Test
    public void InvoiceNoteShouldNotHavePrependedSubjectCodeWithoutBT0021(){
        BG0000Invoice invoice = createInvoiceWithBG0001InvoiceNoteAndBT0022(false);
        Document document = new Document(new Element("Invoice"));

        NoteConverter converter = new NoteConverter();

        converter.map(invoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT);

        Element note = document.getRootElement().getChild("Note");
        String noteText = note.getText();

        assertEquals("TESTNOTE", noteText);
    }

    private BG0000Invoice createInvoiceWithBG0001InvoiceNoteAndBT0022(boolean withBT0021InvoiceNoteSubjectCode) {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0001InvoiceNote bg0001 = new BG0001InvoiceNote();
        BT0022InvoiceNote bt0022 = new BT0022InvoiceNote("TESTNOTE");

        if (withBT0021InvoiceNoteSubjectCode) {
            BT0021InvoiceNoteSubjectCode bt0021 = new BT0021InvoiceNoteSubjectCode("TESTSUBJECTCODE");
            bg0001.getBT0021InvoiceNoteSubjectCode().add(bt0021);
        }


        bg0001.getBT0022InvoiceNote().add(bt0022);
        invoice.getBG0001InvoiceNote().add(bg0001);

        return invoice;
    }

}