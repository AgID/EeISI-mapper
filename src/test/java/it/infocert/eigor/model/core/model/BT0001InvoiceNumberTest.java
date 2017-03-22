package it.infocert.eigor.model.core.model;

import it.infocert.eigor.model.core.datatypes.Identifier;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class BT0001InvoiceNumberTest {

    @Test
    public void shouldWork() {
        BT0001InvoiceNumber bt001 = new BT0001InvoiceNumber("abc");
        assertThat( bt001.toString(), equalTo("abc") );
    }



}