package it.infocert.eigor.converter.fattpa2cen.ciao;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Test;
import org.reflections.Reflections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class DodoTest {

    @Test
    public void dodo() throws Exception {
        String s = "BG0000/BG0025/BG0026";
        Dodo dodo = new Dodo(new Reflections("it.infocert"));
        BG0000Invoice invoice = dodo.stuff(s, new BG0000Invoice());
        assertThat(invoice.getBG0025InvoiceLine(), hasSize(1));
        assertThat(invoice.getBG0025InvoiceLine().get(0).getBG0026InvoiceLinePeriod(), hasSize(1));
    }

    @Test
    public void dodo2() throws Exception {
        String s = "BG0000/BG0025/BG0027";
        Dodo dodo = new Dodo(new Reflections("it.infocert"));
        BG0000Invoice invoice = dodo.stuff(s, new BG0000Invoice());
        assertThat(invoice.getBG0025InvoiceLine(), hasSize(1));
        assertThat(invoice.getBG0025InvoiceLine().get(0).getBG0027InvoiceLineAllowances(), hasSize(1));
    }




}