package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG001InvoiceNote implements BTBG {

    private List<BT0021InvoiceNoteSubjectCode> bt0021InvoiceNoteSubjectCodes = new ArrayList<>(0);;
    private List<BT022InvoiceNote> bt022InvoiceNotes = new ArrayList<>(0);;

    public BG001InvoiceNote() {
    }

    public List<BT0021InvoiceNoteSubjectCode> getBt0021InvoiceNoteSubjectCodes() {
        return bt0021InvoiceNoteSubjectCodes;
    }

    public void setBt0021InvoiceNoteSubjectCodes(List<BT0021InvoiceNoteSubjectCode> bt0021InvoiceNoteSubjectCodes) {
        this.bt0021InvoiceNoteSubjectCodes = bt0021InvoiceNoteSubjectCodes;
    }

    public List<BT022InvoiceNote> getBt022InvoiceNotes() {
        return bt022InvoiceNotes;
    }

    public void setBt022InvoiceNotes(List<BT022InvoiceNote> bt022InvoiceNotes) {
        this.bt022InvoiceNotes = bt022InvoiceNotes;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public void accept(Visitor v) {
        v.startBTBG(this);

        List<BTBG> list = new ArrayList<>();
        list.addAll(this.bt0021InvoiceNoteSubjectCodes);
        list.addAll(this.bt022InvoiceNotes);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }

    @Override
    public String toString() {
        return "BG-01";
    }
}
