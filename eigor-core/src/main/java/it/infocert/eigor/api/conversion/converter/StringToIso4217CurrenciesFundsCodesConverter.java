package it.infocert.eigor.api.conversion.converter;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;

/**
 * Converts "EURO" and "euro" in {@link it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes#EUR},
 * "croatian khuna" in {@link it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes#HRK} and so on.
 */
public class StringToIso4217CurrenciesFundsCodesConverter extends FilteringEnumConversion<String, Iso4217CurrenciesFundsCodes> {

    public static TypeConverter<String, Iso4217CurrenciesFundsCodes> newConverter() {
        return new StringToIso4217CurrenciesFundsCodesConverter();
    }

    private StringToIso4217CurrenciesFundsCodesConverter() {
        super(Iso4217CurrenciesFundsCodes.class);
    }

    protected Filter<Iso4217CurrenciesFundsCodes> buildFilter(final String value) {
        return new FilterByValue<Iso4217CurrenciesFundsCodes, String>(value){
            @Override public boolean apply(Iso4217CurrenciesFundsCodes iso) {
                return iso.getCurrency().equalsIgnoreCase(value);
            }
        };
    }

    @Override
    public Class<Iso4217CurrenciesFundsCodes> getTargetClass() {
        return Iso4217CurrenciesFundsCodes.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }


}
