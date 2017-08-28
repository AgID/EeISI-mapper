package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ToStringTypeConverter;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;


public class Untdid7161SpecialServicesCodesToItalianCodeStringConverter extends ToStringTypeConverter<Untdid7161SpecialServicesCodes> {
    @Override
    public String convert(Untdid7161SpecialServicesCodes code) {
        switch (code) {
            default:
                return "TC01";
        }

    }

    @Override
    public Class<Untdid7161SpecialServicesCodes> getSourceClass() {
        return Untdid7161SpecialServicesCodes.class;
    }
}
