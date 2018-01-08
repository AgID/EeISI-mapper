package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountryNameToIso31661CountryCodeConverterTest {

    CountryNameToIso31661CountryCodeConverter sut = new CountryNameToIso31661CountryCodeConverter();

    @Test public void convertCountryNamesToIso() throws ConversionFailedException {

        Iso31661CountryCodes denmark = sut.convert("Denmark");

        assertThat( denmark, is(Iso31661CountryCodes.DK) );
    }
    
    @Test(expected = ConversionFailedException.class) public void shouldThrowExceptionIfConversionIsNotPossible() throws ConversionFailedException {

        // given
        CountryNameToIso31661CountryCodeConverter sut = new CountryNameToIso31661CountryCodeConverter();

        // when
        Iso31661CountryCodes iso = sut.convert("-not-a-country-");

    }

}