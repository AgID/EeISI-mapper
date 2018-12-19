package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringToIso4217CurrenciesFundsCodesConverterTest {

    StringToIso4217CurrenciesFundsCodesConverter sut = (StringToIso4217CurrenciesFundsCodesConverter) StringToIso4217CurrenciesFundsCodesConverter.newConverter();

    @Test
    public void convertByName() throws ConversionFailedException {

        assertEquals( Iso4217CurrenciesFundsCodes.DKK, sut.convert("Danish Krone") );
        assertEquals( Iso4217CurrenciesFundsCodes.DKK, sut.convert("Danish krone") );
        assertEquals( Iso4217CurrenciesFundsCodes.EUR, sut.convert("euro") );

    }

}
