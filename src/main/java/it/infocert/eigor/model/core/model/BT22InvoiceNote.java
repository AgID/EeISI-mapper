package it.infocert.eigor.model.core.model;

public class BT22InvoiceNote implements BTBG {

    private final String note;

    public BT22InvoiceNote(String note) {
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
}
