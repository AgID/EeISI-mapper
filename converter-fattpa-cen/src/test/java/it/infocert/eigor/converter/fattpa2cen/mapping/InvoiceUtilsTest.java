package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.*;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
        BTBG child = invoiceUtils.getFirstChild(s, invoice);
        assertTrue(child instanceof BG0027InvoiceLineAllowances);
    }

    @Test
    public void getChildrenOfParent() throws Exception {
        // given
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBT0003InvoiceTypeCode().add(new BT0003InvoiceTypeCode(Untdid1001InvoiceTypeCode.Code1));

        // when
        List<BTBG> bt0003 = invoiceUtils.getChildrenAsList(invoice, "BT0003");

        // then
        assertThat( bt0003.size(), is(1) );
        assertThat( ((BT0003InvoiceTypeCode)(bt0003.get(0))).getValue(), is(Untdid1001InvoiceTypeCode.Code1) );


    }
}