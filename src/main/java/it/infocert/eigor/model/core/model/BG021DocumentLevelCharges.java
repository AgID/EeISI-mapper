package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.enums.TemplateSampleUntdid4451InvoiceNoteSubjectCode;

public class BG021DocumentLevelCharges implements BTBG {
    private TemplateSampleUntdid4451InvoiceNoteSubjectCode bt21InvoiceNoteSubjectCode;

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
