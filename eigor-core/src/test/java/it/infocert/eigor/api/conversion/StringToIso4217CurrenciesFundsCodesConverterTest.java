package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringToIso4217CurrenciesFundsCodesConverterTest {

    StringToIso4217CurrenciesFundsCodesConverter sut = new StringToIso4217CurrenciesFundsCodesConverter();

    @Test
    public void convertByName() {

        assertEquals( Iso4217CurrenciesFundsCodes.DKK, sut.convert("Danish Krone") );
        assertEquals( Iso4217CurrenciesFundsCodes.DKK, sut.convert("Danish krone") );
        assertEquals( Iso4217CurrenciesFundsCodes.EUR, sut.convert("euro") );

    }

}