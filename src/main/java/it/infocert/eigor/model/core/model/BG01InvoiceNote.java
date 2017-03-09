package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.dump.Visitor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG01InvoiceNote implements BTBG {

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

    @Override
    public int order() {
        return 20;
    }

    @Override
    public void accept(Visitor v) {
        v.startBTBG(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bt21InvoiceNoteSubjectCodes);
        list.addAll(this.bt22InvoiceNotes);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }
}
