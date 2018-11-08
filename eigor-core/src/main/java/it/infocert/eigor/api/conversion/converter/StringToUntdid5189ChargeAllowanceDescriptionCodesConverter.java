package it.infocert.eigor.api.conversion.converter;


import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;

import java.util.function.Predicate;

public class StringToUntdid5189ChargeAllowanceDescriptionCodesConverter extends FilteringEnumConversion<String, Untdid5189ChargeAllowanceDescriptionCodes> {

    private StringToUntdid5189ChargeAllowanceDescriptionCodesConverter() {
        super(Untdid5189ChargeAllowanceDescriptionCodes.class);
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    protected Predicate<Untdid5189ChargeAllowanceDescriptionCodes> buildFilter(String value) {
        final int k;
        try {
            k = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return untdid5189ChargeAllowanceDescriptionCodes -> false;
        }
        return item -> item.getCode() == k;
    }

    public static TypeConverter<String, Untdid5189ChargeAllowanceDescriptionCodes> newConverter() {
        return new StringToUntdid5189ChargeAllowanceDescriptionCodesConverter();
    }
}
