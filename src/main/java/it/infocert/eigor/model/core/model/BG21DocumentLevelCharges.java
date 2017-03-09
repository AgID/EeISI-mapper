package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.enums.Untdid4451InvoiceNoteSubjectCode;

public class BG21DocumentLevelCharges implements BTBG {
    private Untdid4451InvoiceNoteSubjectCode bt21InvoiceNoteSubjectCode;

    @Override
    public int order() {
        return 122;
    }

    @Override
    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }


}
