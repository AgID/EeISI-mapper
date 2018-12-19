package it.infocert.eigor.model.core.rules;


import it.infocert.eigor.model.core.model.BG0000Invoice;

public class Br002AnInvoiceShallHaveAnInvoiceNumberRule extends Rule {

    @Override
    public RuleOutcome isCompliant(BG0000Invoice coreInvoice) {
        if(coreInvoice.getBT0001InvoiceNumber().size() == 1){
            return RuleOutcome.newOutcome(
                    RuleOutcome.Outcome.SUCCESS,
                    "An invoice shall have an invoice number, it has: %s.",
                    coreInvoice.getBT0001InvoiceNumber().get(0)
            );
        }else{
            return RuleOutcome.newOutcome(
                    RuleOutcome.Outcome.FAILED,
                    "An invoice shall have an invoice number, but it has %d.",
                    coreInvoice.getBT0001InvoiceNumber().size()
            );
        }
    }



}
