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
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FirstLevelElementsConverterTest {


    @Test
    public void InvoiceNoteShouldHavePrependedSubjectCodeIfBT0021(){
        BG0000Invoice invoice = createInvoiceWithBG0001InvoiceNoteAndBT0022s(true);
        Document document = new Document(new Element("Invoice"));

        NoteConverter converter = new NoteConverter();
        converter.map(invoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT, null);

        List<Element> notes = document.getRootElement().getChildren("Note");

        assertEquals("#TESTSUBJECTCODE#TESTNOTE1", notes.get(0).getValue());
        assertEquals("#TESTSUBJECTCODE#TESTNOTE2", notes.get(1).getValue());
    }

    @Test
    public void InvoiceNoteShouldNotHavePrependedSubjectCodeWithoutBT0021(){
        BG0000Invoice invoice = createInvoiceWithBG0001InvoiceNoteAndBT0022s(false);
        Document document = new Document(new Element("Invoice"));

        NoteConverter converter = new NoteConverter();

        converter.map(invoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT, null);

        List<Element> notes = document.getRootElement().getChildren("Note");

        assertEquals("TESTNOTE1", notes.get(0).getValue());
        assertEquals("TESTNOTE2", notes.get(1).getValue());
    }

    private BG0000Invoice createInvoiceWithBG0001InvoiceNoteAndBT0022s(boolean withBT0021InvoiceNoteSubjectCode) {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0001InvoiceNote bg0001 = new BG0001InvoiceNote();

        if (withBT0021InvoiceNoteSubjectCode) {
            BT0021InvoiceNoteSubjectCode bt0021 = new BT0021InvoiceNoteSubjectCode("TESTSUBJECTCODE");
            bg0001.getBT0021InvoiceNoteSubjectCode().add(bt0021);
        }

        bg0001.getBT0022InvoiceNote().add(new BT0022InvoiceNote("TESTNOTE1"));
        bg0001.getBT0022InvoiceNote().add(new BT0022InvoiceNote("TESTNOTE2"));
        invoice.getBG0001InvoiceNote().add(bg0001);

        return invoice;
    }

}
