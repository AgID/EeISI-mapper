package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.ToStringTypeConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;



public class Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter extends ToStringTypeConverter<Untdid5189ChargeAllowanceDescriptionCodes> {

    Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter() {
    }

    @Override
    public String convert(Untdid5189ChargeAllowanceDescriptionCodes code) {
        switch (code) {
            case Code42:
                return "PR";
            case Code57:
            case Code95:
                return "SC";
            case Code100:
                return "AB";
            default:
                return "";
        }

    }

    @Override
    public Class<Untdid5189ChargeAllowanceDescriptionCodes> getSourceClass() {
        return Untdid5189ChargeAllowanceDescriptionCodes.class;
    }

    public static TypeConverter<Untdid5189ChargeAllowanceDescriptionCodes, String> newConverter() {
        return new Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter();
    }
}
