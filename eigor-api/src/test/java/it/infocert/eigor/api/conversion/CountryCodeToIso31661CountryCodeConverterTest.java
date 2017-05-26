package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountryCodeToIso31661CountryCodeConverterTest {

    LookUpEnumConversion sut = new LookUpEnumConversion<Iso31661CountryCodes>(Iso31661CountryCodes.class);

    @Test public void convertStringToIso() {

        // when
        Iso31661CountryCodes it = (Iso31661CountryCodes) sut.convert("IT");

        // then
        Iso31661CountryCodes it1 = Iso31661CountryCodes.IT;
        assertThat( it, is(it1) );

    }

}