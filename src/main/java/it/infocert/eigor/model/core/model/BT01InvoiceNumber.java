package it.infocert.eigor.model.core.model;

import com.google.common.base.Preconditions;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.dump.Visitor;

public class BT01InvoiceNumber implements BTBG {

    private final Identifier invoiceNumber;

    public BT01InvoiceNumber(Identifier invoiceNumber) {
        this.invoiceNumber = Preconditions.checkNotNull( invoiceNumber );
    }

    @Override
    public String toString() {
        return "1234";
    }

    @Override
    public int order() {
        return 1;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }
}
