package it.infocert.eigor.model.core.rules;

import it.infocert.eigor.model.core.model.CoreInvoice;

public interface Rule {

    default boolean issCompliant(CoreInvoice invoice) {
        return isCompliant(invoice).outcome() == RuleOutcome.Outcome.FAILED ? false : true;
    }

    RuleOutcome isCompliant(CoreInvoice coreInvoice);
}
