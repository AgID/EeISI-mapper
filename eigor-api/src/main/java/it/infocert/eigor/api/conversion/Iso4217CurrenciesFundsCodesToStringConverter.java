package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;

public class Iso4217CurrenciesFundsCodesToStringConverter extends ToStringTypeConverter<Iso4217CurrenciesFundsCodes> {

        @Override
        public String convert(Iso4217CurrenciesFundsCodes iso4217CurrenciesFundsCodes) {
            return iso4217CurrenciesFundsCodes.getCode();
        }

        @Override
        public Class<Iso4217CurrenciesFundsCodes> getSourceClass() {
            return Iso4217CurrenciesFundsCodes.class;
        }
}
