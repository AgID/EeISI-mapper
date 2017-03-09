package it.infocert.eigor.model.core.model;

public class BT022InvoiceNote implements BTBG {

    private final String note;

    public BT022InvoiceNote(String note) {
        this.note = note;
    }

    @Override
    public int order() {
        return 22;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }

    @Override
    public String toString() {
        return note;
    }
}
