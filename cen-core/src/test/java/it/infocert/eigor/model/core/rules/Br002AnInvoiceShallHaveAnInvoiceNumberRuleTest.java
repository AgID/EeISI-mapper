package it.infocert.eigor.model.core.rules;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class Br002AnInvoiceShallHaveAnInvoiceNumberRuleTest {

    @Test
    public void shouldRefuseAnInvoiceWithoutNumber(){

        // given
        BG0000Invoice coreInvoice = new BG0000Invoice();

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
        BG0000Invoice coreInvoice = new BG0000Invoice();
        coreInvoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber( "4321" ));

        Br002AnInvoiceShallHaveAnInvoiceNumberRule sut = new Br002AnInvoiceShallHaveAnInvoiceNumberRule();

        // when
        RuleOutcome outcome = sut.isCompliant(coreInvoice);

        // then
        assertThat( outcome.description(), is("An invoice shall have an invoice number, it has: 4321.") );
        assertThat( outcome.outcome(), is(RuleOutcome.Outcome.SUCCESS) );

    }
    
}