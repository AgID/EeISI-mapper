package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Untdid4461PaymentMeansCodeToItalianCodeStringTest {

    TypeConverter<Untdid4461PaymentMeansCode, String> sut = Untdid4461PaymentMeansCodeToItalianCodeString.newConverter();

    @Test public void shouldConvertCode10() throws ConversionFailedException {
        assertEquals("MP01",  sut.convert(Untdid4461PaymentMeansCode.Code10));
    }

    @Test(expected = ConversionFailedException.class) public void shouldNotConvertNull() throws ConversionFailedException {
        sut.convert(null);
    }

}