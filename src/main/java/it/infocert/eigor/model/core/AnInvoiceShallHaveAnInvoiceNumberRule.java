package it.infocert.eigor.model.core;

public class AnInvoiceShallHaveAnInvoiceNumberRule {

    public boolean satidfied(Invoice invoice) {
        return invoice.getInvoiceNumber() != null;
    }

}
