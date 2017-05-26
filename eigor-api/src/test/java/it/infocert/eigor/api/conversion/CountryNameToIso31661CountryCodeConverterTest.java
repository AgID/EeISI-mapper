package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountryNameToIso31661CountryCodeConverterTest {

    CountryNameToIso31661CountryCodeConverter sut = new CountryNameToIso31661CountryCodeConverter();

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
        CountryNameToIso31661CountryCodeConverter sut = new CountryNameToIso31661CountryCodeConverter();

        // when
        Iso31661CountryCodes iso = sut.convert("-not-a-country-");

    }

}