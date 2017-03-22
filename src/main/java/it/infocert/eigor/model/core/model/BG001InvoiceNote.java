package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG001InvoiceNote implements BTBG {

    private List<BT0021InvoiceNoteSubjectCode> bt0021InvoiceNoteSubjectCodes = new ArrayList<>(0);;
    private List<BT0022InvoiceNote> bt0022InvoiceNotes = new ArrayList<>(0);;

    public BG001InvoiceNote() {
    }

    public List<BT0021InvoiceNoteSubjectCode> getBt0021InvoiceNoteSubjectCodes() {
        return bt0021InvoiceNoteSubjectCodes;
    }

    public void setBt0021InvoiceNoteSubjectCodes(List<BT0021InvoiceNoteSubjectCode> bt0021InvoiceNoteSubjectCodes) {
        this.bt0021InvoiceNoteSubjectCodes = bt0021InvoiceNoteSubjectCodes;
    }

    public List<BT0022InvoiceNote> getBt0022InvoiceNotes() {
        return bt0022InvoiceNotes;
    }

    public void setBt0022InvoiceNotes(List<BT0022InvoiceNote> bt0022InvoiceNotes) {
        this.bt0022InvoiceNotes = bt0022InvoiceNotes;
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
        list.addAll(this.bt0022InvoiceNotes);
        list.sort( comparing( o -> o.order() ) );

        list.forEach( o -> o.accept(v) );

        v.endBTBG(this);
    }

    @Override
    public String toString() {
        return "BG-01";
    }
}
