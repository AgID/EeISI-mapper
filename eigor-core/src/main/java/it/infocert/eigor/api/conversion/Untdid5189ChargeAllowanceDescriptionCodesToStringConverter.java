package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;

public class Untdid5189ChargeAllowanceDescriptionCodesToStringConverter extends ToStringTypeConverter<Untdid5189ChargeAllowanceDescriptionCodes> {
    @Override
    public String convert(Untdid5189ChargeAllowanceDescriptionCodes untdid5189ChargeAllowanceDescriptionCodes) {
        return String.valueOf(untdid5189ChargeAllowanceDescriptionCodes.getCode());
    }

    @Override
    public Class<Untdid5189ChargeAllowanceDescriptionCodes> getSourceClass() {
        return Untdid5189ChargeAllowanceDescriptionCodes.class;
    }
}
