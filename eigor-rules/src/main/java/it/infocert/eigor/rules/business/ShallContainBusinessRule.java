package it.infocert.eigor.rules.business;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.AbstractBTBG;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ShallContainBusinessRule implements Rule {

    private String invoicePath;
    private BG0000Invoice invoice;

    public ShallContainBusinessRule(String invoicePath, BG0000Invoice invoice) {
        this.invoicePath = invoicePath;
        this.invoice = invoice;
    }


    @Override
    public RuleOutcome isCompliant(BG0000Invoice coreInvoice) {
        if (contains()) {
            return RuleOutcome.newSuccessOutcome("Invoice contains %s", invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
        } else {
            return RuleOutcome.newFailedOutcome("Invoice doesn't contains %s", invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
        }

    }

    private boolean contains() {
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        return invoiceUtils.hasChild(invoicePath, invoice);
    }
}
