package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.ToStringTypeConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;


public class Untdid7161SpecialServicesCodesToItalianCodeStringConverter extends ToStringTypeConverter<Untdid7161SpecialServicesCodes> {

    Untdid7161SpecialServicesCodesToItalianCodeStringConverter() {
    }

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

    public static TypeConverter<Untdid7161SpecialServicesCodes,String> newConverter() {
        return new Untdid7161SpecialServicesCodesToItalianCodeStringConverter();
    }
}
