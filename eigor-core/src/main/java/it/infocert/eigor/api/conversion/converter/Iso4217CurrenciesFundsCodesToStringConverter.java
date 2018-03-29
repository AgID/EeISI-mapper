package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;

public class Iso4217CurrenciesFundsCodesToStringConverter extends ToStringTypeConverter<Iso4217CurrenciesFundsCodes> {

    public static TypeConverter<Iso4217CurrenciesFundsCodes, String> newConverter(){
        return new Iso4217CurrenciesFundsCodesToStringConverter();
    }

    private Iso4217CurrenciesFundsCodesToStringConverter() {
    }

    @Override
    public String convert(Iso4217CurrenciesFundsCodes iso4217CurrenciesFundsCodes) {
        return iso4217CurrenciesFundsCodes!=null ? iso4217CurrenciesFundsCodes.getCode() : null;
    }

    @Override
    public Class<Iso4217CurrenciesFundsCodes> getSourceClass() {
        return Iso4217CurrenciesFundsCodes.class;
    }
}
