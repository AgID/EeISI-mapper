package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;

public class Iso31661CountryCodesToStringConverter extends ToStringTypeConverter<Iso31661CountryCodes> {
    @Override
    public String convert(Iso31661CountryCodes iso31661CountryCodes) {
        return iso31661CountryCodes.getIso2charCode();
    }

    @Override
    public Class<Iso31661CountryCodes> getSourceClass() {
        return Iso31661CountryCodes.class;
    }
}
