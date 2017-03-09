package it.infocert.eigor.model.core;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso4217CurrencyCode;
import it.infocert.eigor.model.core.model.BT006VatAccountingCurrencyCode;
import it.infocert.eigor.model.core.model.BT001InvoiceNumber;
import it.infocert.eigor.model.core.model.CoreInvoice;
import it.infocert.eigor.model.core.rules.Br002AnInvoiceShallHaveAnInvoiceNumberRule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class Seed {

    @Test
    public void shouldApplyRule() {

        // given
        Br002AnInvoiceShallHaveAnInvoiceNumberRule rule = new Br002AnInvoiceShallHaveAnInvoiceNumberRule();

        CoreInvoice invoiceWithCoreInvoiceNumber = new CoreInvoice();
        invoiceWithCoreInvoiceNumber.getBt001InvoiceNumbers().add(new BT001InvoiceNumber( new Identifier("1234") ));

        CoreInvoice invoiceWithoutCoreInvoiceNumber = new CoreInvoice();

        // when
        boolean outcome1 = rule.issCompliant(invoiceWithCoreInvoiceNumber);
        boolean outcome2 = rule.issCompliant(invoiceWithoutCoreInvoiceNumber);

        // then
        assertThat( outcome1, is(true) );
        assertThat( outcome2, is(false) );

    }



    @Test
    public void justStart() {

        // given
        CoreInvoice coreInvoice = new CoreInvoice();
        BT001InvoiceNumber invoiceNumber = new BT001InvoiceNumber( new Identifier("1234") );

        // when
        coreInvoice.getBt001InvoiceNumbers().add(invoiceNumber);
        coreInvoice.getBt006VatAccountingCurrencyCodes().add( new BT006VatAccountingCurrencyCode(Iso4217CurrencyCode.EUR) );

        // then
        assertThat( coreInvoice.getBt001InvoiceNumbers().get(0), is(invoiceNumber) );

    }

}
