package it.infocert.eigor.rules.constraints;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.reflections.Reflections;

public class ConditionalShallContainRule extends ShallContainRule {

    private final Reflections reflections;
    private final String invoicePath;

    public ConditionalShallContainRule(String invoicePath, String conditionPath, Reflections reflections) {
        super(invoicePath, reflections);
        this.invoicePath = invoicePath;
        this.reflections = reflections;
    }

    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(reflections);

        return super.isCompliant(invoice);
    }
}
