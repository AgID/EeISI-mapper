package it.infocert.eigor.model.core.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class BG001InvoiceNote implements BTBG {

    private List<BT021InvoiceNoteSubjectCode> bt021InvoiceNoteSubjectCodes = new ArrayList<>(0);;
    private List<BT022InvoiceNote> bt022InvoiceNotes = new ArrayList<>(0);;

    public BG001InvoiceNote() {
    }

    public List<BT021InvoiceNoteSubjectCode> getBt021InvoiceNoteSubjectCodes() {
        return bt021InvoiceNoteSubjectCodes;
    }

    public void setBt021InvoiceNoteSubjectCodes(List<BT021InvoiceNoteSubjectCode> bt021InvoiceNoteSubjectCodes) {
        this.bt021InvoiceNoteSubjectCodes = bt021InvoiceNoteSubjectCodes;
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
        list.addAll(this.bt021InvoiceNoteSubjectCodes);
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
