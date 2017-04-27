package it.infocert.eigor.rules.constraints;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.reflections.Reflections;

public class ShallContainRule implements Rule {

    private String invoicePath;
    private Reflections reflections;

    public ShallContainRule(String invoicePath, Reflections reflections) {
        this.invoicePath = invoicePath;
        this.reflections = reflections;
    }


    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        if (contains(invoice)) {
            return RuleOutcome.newSuccessOutcome("Invoice contains %s", invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
        } else {
            return RuleOutcome.newFailedOutcome("Invoice doesn't contain %s", invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
        }

    }

    private boolean contains(BG0000Invoice invoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(reflections);
        return invoiceUtils.hasChild(invoicePath, invoice);
    }

    public String getInvoicePath() {
        return invoicePath;
    }

}
