package it.infocert.eigor.model.core.model;

import com.google.common.base.Preconditions;
import it.infocert.eigor.model.core.datatypes.Identifier;

import java.util.Objects;

public class BT001InvoiceNumber implements BTBG {

    private final Identifier invoiceNumber;

    public BT001InvoiceNumber(Identifier invoiceNumber) {
        this.invoiceNumber = Preconditions.checkNotNull( invoiceNumber );
    }

    @Override
    public String toString() {
        return invoiceNumber.toString();
    }

    @Override
    public int order() {
        return 1;
    }

    public void accept(Visitor v) {
        v.startBTBG(this);
        v.endBTBG(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BT001InvoiceNumber)) return false;
        BT001InvoiceNumber that = (BT001InvoiceNumber) o;
        return Objects.equals(invoiceNumber, that.invoiceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceNumber);
    }
}
