package it.infocert.eigor.model.core;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class Seed {

    @Test
    public void shouldApplyRule() {

        // given
        AnInvoiceShallHaveAnInvoiceNumberRule rule = new AnInvoiceShallHaveAnInvoiceNumberRule();

        Invoice invoiceWithInvoiceNumber = new Invoice();
        invoiceWithInvoiceNumber.setInvoiceNumber(new InvoiceNumber());

        Invoice invoiceWithoutInvoiceNumber = new Invoice();

        // when
        boolean outcome1 = rule.satidfied(invoiceWithInvoiceNumber);
        boolean outcome2 = rule.satidfied(invoiceWithoutInvoiceNumber);

        // then
        assertThat( outcome1, is(true) );
        assertThat( outcome2, is(false) );

    }



    @Test
    public void justStart() {

        // given
        Invoice invoice = new Invoice();
        InvoiceNumber invoiceNumber = new InvoiceNumber();

        // when
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setVatAccountingCurrencyCode(new CurrencyCode("GBP"));
        invoice.addInvoiceLine(new InvoiceLine());

        // then
        assertThat( invoice.getInvoiceNumber(), is(invoiceNumber) );

    }

}
