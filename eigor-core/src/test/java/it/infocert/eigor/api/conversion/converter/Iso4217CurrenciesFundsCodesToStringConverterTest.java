package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Iso4217CurrenciesFundsCodesToStringConverterTest {

    Iso4217CurrenciesFundsCodesToStringConverter sut = (Iso4217CurrenciesFundsCodesToStringConverter) Iso4217CurrenciesFundsCodesToStringConverter.newConverter();

    @Test public void shouldConvertNullInNull() {
        assertNull(sut.convert(null));
    }

    @Test public void shouldConvertValue() {
        assertEquals("EUR", sut.convert(Iso4217CurrenciesFundsCodes.EUR));
    }
}