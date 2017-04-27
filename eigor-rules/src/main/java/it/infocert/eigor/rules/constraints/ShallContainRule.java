package it.infocert.eigor.rules.constraints;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShallContainRule implements Rule {

    private String invoicePath;
    private Reflections reflections;
    private String[] invoicePaths;

    public ShallContainRule(String invoicePath, Reflections reflections) {
        this.invoicePath = invoicePath;
        this.reflections = reflections;
    }

    public ShallContainRule(String[] invoicePaths, Reflections reflections) {
        this.invoicePaths = invoicePaths;
        this.reflections = reflections;
    }


    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        if (invoicePaths != null) {
            List<RuleOutcome> outcomes = new ArrayList<>(1);
            Arrays.stream(invoicePaths).forEach(p -> {
                if (contains(p, invoice)) {
                    outcomes.add(0, RuleOutcome.newSuccessOutcome("Invoice contains %s", p.substring(p.lastIndexOf("/") + 1)));
                } else {
                    outcomes.add(0, RuleOutcome.newFailedOutcome("Invoice doesn't contain %s", p.substring(p.lastIndexOf("/") + 1)));
                }
            });
            return outcomes.get(0);
        } else {
            if (contains(invoicePath, invoice)) {
                return RuleOutcome.newSuccessOutcome("Invoice contains %s", invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
            } else {
                return RuleOutcome.newFailedOutcome("Invoice doesn't contain %s", invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
            }
        }
    }

    private boolean contains(String path, BG0000Invoice invoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(reflections);
        return invoiceUtils.hasChild(path, invoice);
    }

    public String getInvoicePath() {
        return invoicePath;
    }

}
