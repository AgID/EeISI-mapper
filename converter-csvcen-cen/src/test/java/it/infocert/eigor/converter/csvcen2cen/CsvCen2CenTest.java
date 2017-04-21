package it.infocert.eigor.converter.csvcen2cen;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BT0040SellerCountryCode;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CsvCen2CenTest {

    private CsvCen2Cen sut;

    @Before
    public void setUp() {
        sut = new CsvCen2Cen();
    }

    @Test
    public void shouldMapTheCountryCode() throws SyntaxErrorInInvoiceFormatException {

        // given
        InputStream inputStream = asStream(
                "BG/BT,Business Term Name,Value,Remarks,Calculations",
                "BG-4,,XXX,,",
                "BG-5,,XXX,,",
                "BT-40,,AE,,");

        // when
        BG0000Invoice invoice = sut.convert(inputStream);

        // then
        BT0040SellerCountryCode btCountryCode = invoice.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0040SellerCountryCode().get(0);
        assertThat( btCountryCode.getValue(), is(Iso31661CountryCodes.AE) );

    }

    @Test
    public void shouldRefuseACenInWhichElementsAreInTheWrongOrder() {

        // given
        InputStream inputStream = asStream(
                "BG/BT,Business Term Name,Value,Remarks,Calculations",
                "BG-12,,,,");

        // when
        SyntaxErrorInInvoiceFormatException exception = null;
        try {
            BG0000Invoice invoice = sut.convert(inputStream);
            fail();
        } catch (SyntaxErrorInInvoiceFormatException e) {
            exception = e;
        }

        // then
        assertThat(exception.getMessage(), allOf(
                containsString("'BG-12'"), // a reference to the element in the wrong order
                containsString("'/BG-12'"), // a reference to the path where we are trying to place the BG-12,
                containsString("'/BG-11/BG-12'"), // a reference to the path where the BG-12 should be placed instead,
                containsString("Record #1") // a reference to the wrong record in the CSV input
        ));

    }

    @Test
    public void shouldLoadASimpleCEN() throws SyntaxErrorInInvoiceFormatException {

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


    @Test
    public void shouldConvertA7MinimumContentStandardExample() throws SyntaxErrorInInvoiceFormatException {

        // given
        InputStream inputStream = getClass().getResourceAsStream("/cen-a7-minimum-content-with-std-values.csv");

        // when
        BG0000Invoice invoice = sut.convert(inputStream);

        // then
        // let's check some elements.
        assertThat( invoice.getBG0002ProcessControl().get(0).getBT0024SpecificationIdentifier().get(0).toString(), equalTo("urn:cen.eu:en16931:2017") );
        assertThat( invoice.getBG0007Buyer().get(0).getBT0044BuyerName().get(0).toString(), equalTo("Buyercompany ltd") );

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