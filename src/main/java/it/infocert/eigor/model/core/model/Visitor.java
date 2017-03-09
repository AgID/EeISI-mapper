package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.CoreInvoice;

public interface Visitor {
    void startInvoice(CoreInvoice invoice);

    void endInvoice(CoreInvoice invoice);

    void startBTBG(BTBG invoiceNumber);

    void endBTBG(BTBG invoiceNumber);
}
