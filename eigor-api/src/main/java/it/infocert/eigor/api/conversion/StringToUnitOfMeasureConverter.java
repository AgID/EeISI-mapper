package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;

import java.util.Arrays;

public class StringToUnitOfMeasureConverter implements TypeConverter<String, UnitOfMeasureCodes> {
    @Override public UnitOfMeasureCodes convert(String s) {

        try {
            return UnitOfMeasureCodes.valueOf(s);
        }catch(IllegalArgumentException iae){

        }

        return Arrays.stream(UnitOfMeasureCodes.values())
                .filter(uom -> {
                    return uom.getName().equalsIgnoreCase(s);
                })
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
