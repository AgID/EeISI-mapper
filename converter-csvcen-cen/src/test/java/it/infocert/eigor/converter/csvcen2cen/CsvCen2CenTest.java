package it.infocert.eigor.converter.csvcen2cen;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvCen2CenTest {

    private CsvCen2Cen sut;

    @Before
    public void setUp() {
        sut = new CsvCen2Cen();
    }

    @Test
    public void shouldLoadASample() throws SyntaxErrorInInvoiceFormatException {

        // given
        InputStream inputStream = asStream(
                "BG/BT,Business Term Name,Value,Remarks,Calculations",
                "BG-25,INVOICE LINE 1,,,",
                "BT-129,Invoiced quantity:,5,,",
                "BT-130,Invoiced quantity unit of measure:,Bottle,,",
                "BT-146,Item net price:,12,EUR,",
                "BT-149,Item price base quantity:,1,,",
                "BT-131,Invoice line net amount:,60,EUR,\"Invoiced quantity x (Item net price/Item price base quantity)\"",
                "BT-151,Invoiced item VAT category code:,Standard rate,,",
                "BT-152 ,Invoiced item VAT rate:,25,%,");

        // when
        BG0000Invoice invoice = sut.convert(inputStream);

        // then
        assertThat( invoice.getBG0025InvoiceLine().get(0).getBT0129InvoicedQuantity().get(0), is(5) );
        assertThat( invoice.getBG0025InvoiceLine().get(0).getBT0130InvoicedQuantityUnitOfMeasureCode().get(0), is(3) );
        // ??? assertThat( invoice.getBG0025InvoiceLine().get(0).getBT0146
        // ??? assertThat( invoice.getBG0025InvoiceLine().get(0).getBT0149
        assertThat( invoice.getBG0025InvoiceLine().get(0).getBT0131InvoiceLineNetAmount().get(0), is(60));
        // ??? assertThat( invoice.getBG0025InvoiceLine().get(0).getBT01 31InvoiceLineNetAmount().get(0), is(60);
        // ??? assertThat( invoice.getBG0025InvoiceLine().get(0).getBT0152

    }

    private InputStream asStream(String... lines) {
        String join = String.join("\n", asList(lines));
        return new ByteArrayInputStream(join.getBytes());
    }

    @Test
    public void shouldSupportCsvCen() {
        assertThat( sut.support("csvcen"), is(true));
        assertThat( sut.support("CsvCen"), is(true));
        assertThat( sut.support("xml"), is(false));
    }

}