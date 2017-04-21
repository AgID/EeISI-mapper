package it.infocert.eigor.model.core;

import it.infocert.eigor.model.core.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class ParentAwareListTest {

    @Test
    public void firstLevel() throws Exception {
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBG0025InvoiceLine().add(new BG0025InvoiceLine());
        BG0000Invoice parent = (BG0000Invoice) invoice.getBG0025InvoiceLine().get(0).getParent();
        assertEquals(invoice, parent);
    }

    @Test
    public void shouldSetTheParentAlsoWhenUsingPlainSetters() {

        // given
        BG0000Invoice invoice = new BG0000Invoice();

        // ...let's just create a bg25 at position 0.
        invoice.getBG0025InvoiceLine().add(new BG0025InvoiceLine());

        // when

        // ...the item is reset through the index...
        BG0025InvoiceLine toAdd = new BG0025InvoiceLine();
        List<BG0025InvoiceLine> bg0025InvoiceLine = invoice.getBG0025InvoiceLine();
        bg0025InvoiceLine.set(0, toAdd);

        // then
        // ...it should reference the parent
        Assert.assertThat(toAdd.getParent(), is(invoice));

    }

    @Test
    public void deepLevel() throws Exception {
        BG0032ItemAttributes itemAttributes = new BG0032ItemAttributes();
        itemAttributes.getBT0161ItemAttributeValue().add(new BT0161ItemAttributeValue("Attribute"));
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBG0032ItemAttributes().add(itemAttributes);
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBG0031ItemInformation().add(itemInformation);
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        assertEquals(invoice, invoice.getBG0025InvoiceLine().get(0).getParent());
        assertEquals(invoiceLine, invoiceLine.getBG0031ItemInformation().get(0).getParent());
        assertEquals(itemInformation, itemInformation.getBG0032ItemAttributes().get(0).getParent());
        assertEquals(itemAttributes, itemAttributes.getBT0161ItemAttributeValue().get(0).getParent());
    }
}
