package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.enums.Untdid4451InvoiceNoteSubjectCodeSample;

public class BG021DocumentLevelCharges implements BTBG {
    private Untdid4451InvoiceNoteSubjectCodeSample bt21InvoiceNoteSubjectCode;

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
