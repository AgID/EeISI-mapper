package it.infocert.eigor.api.conversion.converter;



import it.infocert.eigor.api.conversion.ConversionBetweenTypesFailedException;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;

import java.util.Arrays;
import java.util.function.Predicate;

public class
StringToUnitOfMeasureConverter extends FromStringTypeConverter<UnitOfMeasureCodes> {

    private StringToUnitOfMeasureConverter() {
    }

    @Override
    public UnitOfMeasureCodes convert(final String s) throws ConversionFailedException {

        try {
            return UnitOfMeasureCodes.valueOf(s);
        }catch(IllegalArgumentException ignored){

        }

        Predicate<UnitOfMeasureCodes> f = unitOfMeasureCodes -> unitOfMeasureCodes.getName().equalsIgnoreCase(s) || unitOfMeasureCodes.getCommonCode().equalsIgnoreCase(s);

        UnitOfMeasureCodes result = Arrays.stream(UnitOfMeasureCodes.values())
                .filter(f)
                .findFirst().orElse(UnitOfMeasureCodes.C62_ONE);

        if(result == null) throw new ConversionBetweenTypesFailedException(
                String.class, UnitOfMeasureCodes.class,
                s);

        return result;

    }

    @Override
    public Class<UnitOfMeasureCodes> getTargetClass() {
        return UnitOfMeasureCodes.class;
    }

    public static TypeConverter<String, UnitOfMeasureCodes> newConverter() {
        return new StringToUnitOfMeasureConverter();
    }
}
