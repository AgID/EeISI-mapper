package it.infocert.eigor.model.core.rules;

import it.infocert.eigor.model.core.model.CoreInvoice;

public class AnInvoiceShallHaveAnInvoiceNumberRule {

    public boolean satidfied(CoreInvoice coreInvoice) {
        return coreInvoice.getBt01InvoiceNumbers().size() == 1;
    }

}
