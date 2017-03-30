package it.infocert.eigor.model.core.rules;


import it.infocert.eigor.model.core.model.BG0000Invoice;

public interface Rule {

    default boolean issCompliant(BG0000Invoice invoice) {
        return isCompliant(invoice).outcome() == RuleOutcome.Outcome.FAILED ? false : true;
    }

    RuleOutcome isCompliant(BG0000Invoice coreInvoice);
}
