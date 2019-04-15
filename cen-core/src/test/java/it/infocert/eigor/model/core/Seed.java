package it.infocert.eigor.model.core;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0006VatAccountingCurrencyCode;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class Seed {

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
