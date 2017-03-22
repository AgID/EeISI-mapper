package it.infocert.eigor.model.core.rules;

import it.infocert.eigor.model.core.model.CoreInvoice;

public class Br002AnInvoiceShallHaveAnInvoiceNumberRule implements Rule {

    @Override
    public RuleOutcome isCompliant(CoreInvoice coreInvoice) {
        if(coreInvoice.getBt0001InvoiceNumbers().size() == 1){
            return RuleOutcome.newOutcome(
                    RuleOutcome.Outcome.SUCCESS,
                    "An invoice shall have an invoice number, it has: %s.", coreInvoice.getBt0001InvoiceNumbers().get(0)
            );
        }else{
            return RuleOutcome.newOutcome(
                    RuleOutcome.Outcome.FAILED,
                    "An invoice shall have an invoice number, but it has %d.", coreInvoice.getBt0001InvoiceNumbers().size()
            );
        }
    }


}
