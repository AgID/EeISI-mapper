package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common functionality to transform a value (String, Integer, ...) in an entry of an enum.
 * @param <Source> The type that is needed to be converted into an enum.
 * @param <Target> The enum.
 */
public abstract class FilteringEnumConversion<Source, Target extends Enum<Target>> implements TypeConverter<Source, Target> {

    private Class<Enum<Target>> theEnum;

    FilteringEnumConversion(Class theEnum){
        this.theEnum = theEnum;
    }

    public final Target convert(final Source value) {

        Filter<Target> f = buildFilter(value);

        List<Target> enumValues;
        try {
            Object[] values = (Object[]) theEnum.getMethod("values", new Class[]{}).invoke(theEnum);
            enumValues = new ArrayList<>();
            for (Object theValue : values) {
                enumValues.add((Target) theValue);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Target result = Stream.create(enumValues).filter(f).first();
        if(result==null) throw new IllegalArgumentException( String.format("Value '%s' not found among %d entries in %s.", value, enumValues.size(), theEnum.getSimpleName()) );
        return result;

    }

    /**
     * Should return a {@link Filter} that among all entries of the enum select the only one corresponding to the source value.
     */
    protected abstract Filter<Target> buildFilter(Source value);

    protected abstract static class FilterByValue<E,V> implements Filter<E> {

        protected final V value;

        FilterByValue(V value) {
            this.value = value;
        }

    }

}
