package it.infocert.eigor.model.core;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0006VatAccountingCurrencyCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Br002AnInvoiceShallHaveAnInvoiceNumberRule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class Seed {

    @Test
    public void shouldApplyRule() {

        // given
        Br002AnInvoiceShallHaveAnInvoiceNumberRule rule = new Br002AnInvoiceShallHaveAnInvoiceNumberRule();

        BG0000Invoice invoiceWithBG0000InvoiceNumber = new BG0000Invoice();
        invoiceWithBG0000InvoiceNumber.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber( "1234" ));

        BG0000Invoice invoiceWithoutBG0000InvoiceNumber = new BG0000Invoice();

        // when
        boolean outcome1 = rule.issCompliant(invoiceWithBG0000InvoiceNumber);
        boolean outcome2 = rule.issCompliant(invoiceWithoutBG0000InvoiceNumber);

        // then
        assertThat( outcome1, is(true) );
        assertThat( outcome2, is(false) );

    }



    @Test
    public void justStart() {

        // given
        BG0000Invoice coreInvoice = new BG0000Invoice();
        BT0001InvoiceNumber invoiceNumber = new BT0001InvoiceNumber( "1234" );

        // when
        coreInvoice.getBT0001InvoiceNumber().add(invoiceNumber);
        coreInvoice.getBT0006VatAccountingCurrencyCode().add( new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR) );

        // then
        assertThat( coreInvoice.getBT0001InvoiceNumber().get(0), is(invoiceNumber) );

    }

}
