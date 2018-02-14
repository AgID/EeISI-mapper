package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;

public class
StringToUnitOfMeasureConverter extends FromStringTypeConverter<UnitOfMeasureCodes> {

    StringToUnitOfMeasureConverter() {
    }

    @Override
    public UnitOfMeasureCodes convert(final String s) throws ConversionFailedException {

        try {
            return UnitOfMeasureCodes.valueOf(s);
        }catch(IllegalArgumentException iae){

        }

        Filter<UnitOfMeasureCodes> f = new Filter<UnitOfMeasureCodes>() {
            @Override public boolean apply(UnitOfMeasureCodes uom) {
                return uom.getName().equalsIgnoreCase(s) || uom.getCommonCode().equalsIgnoreCase(s);
            }
        };

        UnitOfMeasureCodes result = Stream.create(UnitOfMeasureCodes.values()).filter(f).first();

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
