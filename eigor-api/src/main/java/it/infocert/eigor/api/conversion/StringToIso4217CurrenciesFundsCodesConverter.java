package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;

public class StringToIso4217CurrenciesFundsCodesConverter extends FilteringEnumConversion<String, Iso4217CurrenciesFundsCodes> {

    public StringToIso4217CurrenciesFundsCodesConverter() {
        super(Iso4217CurrenciesFundsCodes.class);
    }

    protected Filter<Iso4217CurrenciesFundsCodes> buildFilter(final String value) {
        return new FilterByValue<Iso4217CurrenciesFundsCodes, String>(value){
            @Override public boolean apply(Iso4217CurrenciesFundsCodes iso) {
                return iso.getCurrency().equalsIgnoreCase(value);
            }
        };
    }



}
