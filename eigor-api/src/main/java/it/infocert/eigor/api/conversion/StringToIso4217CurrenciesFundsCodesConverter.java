package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;

import java.util.Arrays;

public class StringToIso4217CurrenciesFundsCodesConverter implements TypeConverter<String, Iso4217CurrenciesFundsCodes> {
    @Override public Iso4217CurrenciesFundsCodes convert(String s) {

        try {
            return Iso4217CurrenciesFundsCodes.valueOf(s);
        }catch(IllegalArgumentException iae){

        }

        return Arrays.stream(Iso4217CurrenciesFundsCodes.values())
                .filter(iso -> {
                    return iso.getCurrency().equalsIgnoreCase(s);
                })
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
