package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;
import it.infocert.eigor.model.core.model.BTBG;
import org.junit.Test;
import org.reflections.Reflections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class InvoiceUtilsTest {

    @Test
    public void ensuringPath() throws Exception {
        String s = "/BG0025/BG0026";
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, new BG0000Invoice());
        assertThat(invoice.getBG0025InvoiceLine(), hasSize(1));
        assertThat(invoice.getBG0025InvoiceLine().get(0).getBG0026InvoiceLinePeriod(), hasSize(1));
    }

    @Test
    public void ensuringPathShouldFail() throws Exception {
        String s = "/BG0025/BG0027/BG0026";
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        try {
            BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, new BG0000Invoice());
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage().toLowerCase(), allOf(
                    containsString(s.toLowerCase()),
                    containsString("wrong")
            ));
        }
    }

    @Test
    public void tooManyBGs() throws Exception {
        String s = "/BG0025";

        BG0000Invoice inv = new BG0000Invoice();
        inv.getBG0025InvoiceLine().add(new BG0025InvoiceLine());
        inv.getBG0025InvoiceLine().add(new BG0025InvoiceLine());

        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        try {
            BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, inv);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage().toLowerCase(), allOf(
                    containsString(s.toLowerCase()),
                    containsString("wrong"),
                    containsString("too many")
            ));
        }
    }

    @Test
    public void getChildTest() throws Exception {
        String s = "/BG0025/BG0027";
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        BG0000Invoice invoice = new BG0000Invoice();
        BG0025InvoiceLine bg25 = new BG0025InvoiceLine();
        bg25.getBG0027InvoiceLineAllowances().add(new BG0027InvoiceLineAllowances());
        invoice.getBG0025InvoiceLine().add(bg25);
        BTBG child = invoiceUtils.getChild(s, invoice);
        assertTrue(child instanceof BG0027InvoiceLineAllowances);
    }
}