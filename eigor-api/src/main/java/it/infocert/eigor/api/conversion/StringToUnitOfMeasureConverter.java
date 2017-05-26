package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;

import java.util.Arrays;
import java.util.function.Predicate;

public class StringToUnitOfMeasureConverter implements TypeConverter<String, UnitOfMeasureCodes> {
    @Override public UnitOfMeasureCodes convert(final String s) {

        try {
            return UnitOfMeasureCodes.valueOf(s);
        }catch(IllegalArgumentException iae){

        }

        Filter<UnitOfMeasureCodes> f = new Filter<UnitOfMeasureCodes>() {
            @Override public boolean apply(UnitOfMeasureCodes uom) {
                return uom.getName().equalsIgnoreCase(s);
            }
        };

        UnitOfMeasureCodes result = Stream.create(UnitOfMeasureCodes.values()).filter(f).first();

        if(result == null) throw new IllegalArgumentException();

        return result;

    }
}
