package it.infocert.eigor.rules.constraints;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.reflections.Reflections;

public class ConditionalShallContainRule extends ShallContainRule {

    private final Reflections reflections;
    private final String invoicePath;
    private final String conditionPath;

    public ConditionalShallContainRule(String invoicePath, String conditionPath, Reflections reflections) {
        super(invoicePath, reflections);
        this.invoicePath = invoicePath;
        this.conditionPath = conditionPath;
        this.reflections = reflections;
    }

    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(reflections);
        if (invoiceUtils.hasChild(conditionPath, invoice)) {
            RuleOutcome ruleOutcome = super.isCompliant(invoice);
            if (RuleOutcome.Outcome.SUCCESS.equals(ruleOutcome.outcome())) {
                return RuleOutcome.newSuccessOutcome("Since invoice contains %s, it should also contains %s. It does indeed.",
                        conditionPath.substring(conditionPath.lastIndexOf("/") + 1),
                        invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
            } else {
                return RuleOutcome.newFailedOutcome("Since invoice contains %s, it should also contains %s. It does not.",
                        conditionPath.substring(conditionPath.lastIndexOf("/") + 1),
                        invoicePath.substring(invoicePath.lastIndexOf("/") + 1));
            }
        } else {
            return RuleOutcome.newUnapplicableOutcome("Invoice doesn't contain %s, rule is not applicable",
                    conditionPath.substring(conditionPath.lastIndexOf("/") + 1));

        }
    }
}
