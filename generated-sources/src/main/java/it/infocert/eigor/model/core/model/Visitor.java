package it.infocert.eigor.model.core.model;

public interface Visitor {
    void startInvoice(CoreInvoice invoice);

    void endInvoice(CoreInvoice invoice);

    void startBTBG(BTBG invoiceNumber);

    void endBTBG(BTBG invoiceNumber);
}
