package it.infocert.eigor.converter.csvcen2cen;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
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
                "BT-01,Invoice Number,2017/123,,",
                "BG-01,Invoice Note,,,",
                "BT-021,Invoice Note Subject Code,Code#1,,",
                "BT-022,Invoice Note,This is note #1,,",
                "BG-01,Invoice Note,,,",
                "BT-021,Invoice Note Subject Code,Code#2,,",
                "BT-022,Invoice Note,This is note #2,,");

        // when
        BG0000Invoice invoice = sut.convert(inputStream);

        // then
        List<BG0001InvoiceNote> notes = invoice.getBG0001InvoiceNote();
        assertThat( notes.get(0).getBT0021InvoiceNoteSubjectCode().get(0).toString(), is("Code#1") );
        assertThat( notes.get(0).getBT0022InvoiceNote().get(0).toString(), is("This is note #1") );
        assertThat( notes.get(1).getBT0021InvoiceNoteSubjectCode().get(0).toString(), is("Code#2") );
        assertThat( notes.get(1).getBT0022InvoiceNote().get(0).toString(), is("This is note #2") );
        assertThat( notes, hasSize(2));

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