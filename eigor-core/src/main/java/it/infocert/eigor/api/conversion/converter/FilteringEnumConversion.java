package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.EnumConversionFailedException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    @Override
    public final Target convert(final Source value) throws ConversionFailedException {

        Predicate<Target> f = buildFilter(value);

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

        Target result = enumValues.stream()
                .filter(f).findFirst().orElse(null);
        if(result==null) throw new EnumConversionFailedException( String.format("Value '%s' not found among %d entries in %s.", value, enumValues.size(), theEnum.getSimpleName()) );
        return result;

    }

    /**
     * Should return a {@link Predicate} that among all entries of the enum select the only one corresponding to the source value.
     */
    protected abstract Predicate<Target> buildFilter(Source value);

    @Override
    public Class<Target> getTargetClass() {
        return (Class<Target>) theEnum;
    }


}
