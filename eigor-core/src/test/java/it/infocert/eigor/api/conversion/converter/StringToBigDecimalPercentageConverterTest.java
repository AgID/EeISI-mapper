package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class StringToBigDecimalPercentageConverterTest {

    @Test
    public void shouldConvertPercentageToDoubles() throws ConversionFailedException {
        final StringToBigDecimalPercentageConverter converter = (StringToBigDecimalPercentageConverter) StringToBigDecimalPercentageConverter.newConverter();
        assertTrue(converter.convert("25%").compareTo(new BigDecimal("0.25")) == 0);
    }

}