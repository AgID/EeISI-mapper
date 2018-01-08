package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;

public class StringToUntdid5189ChargeAllowanceDescriptionCodesConverter extends FilteringEnumConversion<String, Untdid5189ChargeAllowanceDescriptionCodes> {

    StringToUntdid5189ChargeAllowanceDescriptionCodesConverter() {
        super(Untdid5189ChargeAllowanceDescriptionCodes.class);
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    protected Filter<Untdid5189ChargeAllowanceDescriptionCodes> buildFilter(String value) {
        final int k;
        try {
            k = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return new Filter<Untdid5189ChargeAllowanceDescriptionCodes>() {
                @Override
                public boolean apply(Untdid5189ChargeAllowanceDescriptionCodes untdid5189ChargeAllowanceDescriptionCodes) {
                    return false;
                }
            };
        }
        return new Filter<Untdid5189ChargeAllowanceDescriptionCodes>() {
            @Override
            public boolean apply(Untdid5189ChargeAllowanceDescriptionCodes item) {
                return item.getCode() == k;
            }
        };
    }

    public static TypeConverter<String, Untdid5189ChargeAllowanceDescriptionCodes> newConverter() {
        return new StringToUntdid5189ChargeAllowanceDescriptionCodesConverter();
    }
}
