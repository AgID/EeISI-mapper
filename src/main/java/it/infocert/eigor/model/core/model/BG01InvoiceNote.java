package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

public class BG01InvoiceNote {

    private List<BT21InvoiceNoteSubjectCode> bt21InvoiceNoteSubjectCodes = new ArrayList<>(0);;
    private List<BT22InvoiceNote> bt22InvoiceNotes = new ArrayList<>(0);;

    public BG01InvoiceNote() {
    }

    public List<BT21InvoiceNoteSubjectCode> getBt21InvoiceNoteSubjectCodes() {
        return bt21InvoiceNoteSubjectCodes;
    }

    public void setBt21InvoiceNoteSubjectCodes(List<BT21InvoiceNoteSubjectCode> bt21InvoiceNoteSubjectCodes) {
        this.bt21InvoiceNoteSubjectCodes = bt21InvoiceNoteSubjectCodes;
    }

    public List<BT22InvoiceNote> getBt22InvoiceNotes() {
        return bt22InvoiceNotes;
    }

    public void setBt22InvoiceNotes(List<BT22InvoiceNote> bt22InvoiceNotes) {
        this.bt22InvoiceNotes = bt22InvoiceNotes;
    }
}
