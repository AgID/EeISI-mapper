package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;

public class UnitOfMeasureCodesToStringConverter extends ToStringTypeConverter<UnitOfMeasureCodes> {

    UnitOfMeasureCodesToStringConverter() {
    }

    @Override
    public String convert(UnitOfMeasureCodes unitOfMeasureCodes) {
        return unitOfMeasureCodes.getCommonCode();
    }

    @Override
    public Class<UnitOfMeasureCodes> getSourceClass() {
        return UnitOfMeasureCodes.class;
    }

    public static TypeConverter<UnitOfMeasureCodes, String> newConverter() {
        return new UnitOfMeasureCodesToStringConverter();
    }
}
