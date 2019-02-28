package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.VatExemptionReasonsCodes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LookUpEnumConversionTest {

    LookUpEnumConversion sut = (LookUpEnumConversion) LookUpEnumConversion.newConverter(Iso31661CountryCodes.class);

    @Test public void convertStringToVat() throws ConversionFailedException {

        // given
        LookUpEnumConversion<VatExemptionReasonsCodes> sut = (LookUpEnumConversion<VatExemptionReasonsCodes>) LookUpEnumConversion.newConverter(VatExemptionReasonsCodes.class);

        // when
        VatExemptionReasonsCodes aam = sut.convert("AAM");

        // then
        assertThat( aam, is(VatExemptionReasonsCodes.AAM) );

    }

    @Test public void convertStringToIso() throws ConversionFailedException {

        // when
        Iso31661CountryCodes it = (Iso31661CountryCodes) sut.convert("IT");

        // then
        Iso31661CountryCodes it1 = Iso31661CountryCodes.IT;
        assertThat( it, is(it1) );

    }

    @Test public void convertStringHRToIso() throws ConversionFailedException {

        // when
        Iso31661CountryCodes it = (Iso31661CountryCodes) sut.convert("HR");

        // then
        Iso31661CountryCodes it1 = Iso31661CountryCodes.HR;
        assertThat( it, is(it1) );

    }

}
