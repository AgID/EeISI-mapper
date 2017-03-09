package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.enums.Untdid4451InvoiceNoteSubjectCode;

import static com.google.common.base.Preconditions.checkNotNull;

public class BT21InvoiceNoteSubjectCode implements BTBG {

    private Untdid4451InvoiceNoteSubjectCode subjectCode;

    public BT21InvoiceNoteSubjectCode(Untdid4451InvoiceNoteSubjectCode subjectCode) {
        this.subjectCode = checkNotNull( subjectCode );
    }

    @Override
    public int order() {
        return 21;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }
}
