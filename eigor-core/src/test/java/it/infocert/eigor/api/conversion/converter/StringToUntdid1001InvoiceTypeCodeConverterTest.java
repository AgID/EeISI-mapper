package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.converter.StringToUntdid1001InvoiceTypeCodeConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringToUntdid1001InvoiceTypeCodeConverterTest {

    StringToUntdid1001InvoiceTypeCodeConverter sut = (StringToUntdid1001InvoiceTypeCodeConverter) StringToUntdid1001InvoiceTypeCodeConverter.newConverter();


    @Test public void convertStringToIsoBasedOnDescription() {

        // when
        Untdid1001InvoiceTypeCode iso = sut.convert("Consignment invoice");

        // then
        assertThat( iso, is(Untdid1001InvoiceTypeCode.Code395) );

    }

    @Test public void convertStringToIso() {

        // when
        Untdid1001InvoiceTypeCode iso = sut.convert("14");

        // then
        assertThat( iso, is(Untdid1001InvoiceTypeCode.Code14) );

    }

    @Test(expected = IllegalArgumentException.class) public void shouldThrowExceptionIfConversionIsNotPossible() {

        // when
        Untdid1001InvoiceTypeCode iso = sut.convert("-not-a-code-");

    }

}