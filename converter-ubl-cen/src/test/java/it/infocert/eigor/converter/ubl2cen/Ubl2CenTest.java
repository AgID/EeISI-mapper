package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class Ubl2CenTest {

    private static final Logger log = LoggerFactory.getLogger(Ubl2CenTest.class);

    private Ubl2Cen sut;

    @Before
    public void setUp() {
        sut = new Ubl2Cen();
    }

    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("ubl-plain.xml");

        BG0000Invoice invoice = sut.convert(sourceInvoiceStream);

        assertThat(invoice, is(IsNull.notNullValue()));
    }

    @Test
    public void canReadDocument() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        InputStream sourceInvoiceStream = getClass().getClassLoader().getResourceAsStream("ubl-plain.xml");

        Document document = sut.getDocument(sourceInvoiceStream);

        assertThat(document, is(IsNull.notNullValue()));
    }

    @Test
    public void shouldSupportUbl() {
        assertThat(sut.support("ubl"), is(true));
    }

    @Test
    public void shouldSupportedFormatsUbl() {
        assertThat(sut.getSupportedFormats(), contains("ubl"));
    }

}