package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.StringToIso31661CountryCodesConverter;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StringToIso31661CountryCodesConverterTest {

    StringToIso31661CountryCodesConverter sut = new StringToIso31661CountryCodesConverter();

    @Test public void convertCountryNamesToIso() {

        Iso31661CountryCodes denmark = sut.convert("Denmark");

        assertThat( denmark, is(Iso31661CountryCodes.DK) );
    }

    @Test public void convertStringToIso() {

        // when
        Iso31661CountryCodes iso = sut.convert("IT");

        // then
        assertThat( iso, is(Iso31661CountryCodes.IT) );

    }

    @Test(expected = IllegalArgumentException.class) public void shouldThrowExceptionIfConversionIsNotPossible() {

        // given
        StringToIso31661CountryCodesConverter sut = new StringToIso31661CountryCodesConverter();

        // when
        Iso31661CountryCodes iso = sut.convert("-not-a-country-");

    }

}