package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.conversion.converter.Untdid1001InvoiceTypeCodesToStringConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Untdid1001InvoiceTypeCodesToStringConverterTest {

    private TypeConverter<Untdid1001InvoiceTypeCode, String> sut= Untdid1001InvoiceTypeCodesToStringConverter.newConverter();

    @Test public void shouldConvertCode380() throws ConversionFailedException {
        assertEquals("380",  sut.convert(Untdid1001InvoiceTypeCode.Code380));
    }

    @Test(expected = ConversionFailedException.class) public void shouldNotConvertNull() throws ConversionFailedException {
        sut.convert(null);
    }
}