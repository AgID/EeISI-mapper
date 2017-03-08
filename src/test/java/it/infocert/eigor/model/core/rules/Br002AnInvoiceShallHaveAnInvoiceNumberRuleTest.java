package it.infocert.eigor.model.core.rules;

import it.infocert.eigor.model.core.model.BT01InvoiceNumber;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class Br002AnInvoiceShallHaveAnInvoiceNumberRuleTest {

    @Test
    public void shouldRefuseAnInvoiceWithoutNumber(){

        // given
        CoreInvoice coreInvoice = new CoreInvoice();

        Br002AnInvoiceShallHaveAnInvoiceNumberRule sut = new Br002AnInvoiceShallHaveAnInvoiceNumberRule();

        // when
        RuleOutcome outcome = sut.isCompliant(coreInvoice);

        // then
        assertThat( outcome.outcome(), is(RuleOutcome.Outcome.FAILED) );
        assertThat( outcome.description(), is("An invoice shall have an invoice number, but it has 0.") );

    }

    @Test
    public void shouldAcceptAnInvoiceWithNumber(){

        // given
        CoreInvoice coreInvoice = new CoreInvoice();
        coreInvoice.getBt01InvoiceNumbers().add(new BT01InvoiceNumber());

        Br002AnInvoiceShallHaveAnInvoiceNumberRule sut = new Br002AnInvoiceShallHaveAnInvoiceNumberRule();

        // when
        RuleOutcome outcome = sut.isCompliant(coreInvoice);

        // then
        assertThat( outcome.description(), is("An invoice shall have an invoice number, it has: 1234.") );
        assertThat( outcome.outcome(), is(RuleOutcome.Outcome.SUCCESS) );

    }
    
}