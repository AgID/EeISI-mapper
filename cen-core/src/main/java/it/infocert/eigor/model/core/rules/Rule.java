package it.infocert.eigor.model.core.rules;


import it.infocert.eigor.model.core.model.BG0000Invoice;

public abstract class Rule {

    public boolean issCompliant(BG0000Invoice invoice) {
        return isCompliant(invoice).outcome() == RuleOutcome.Outcome.FAILED ? false : true;
    }

    public abstract RuleOutcome isCompliant(BG0000Invoice coreInvoice);
}
