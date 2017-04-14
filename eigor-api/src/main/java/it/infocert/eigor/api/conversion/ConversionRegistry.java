package it.infocert.eigor.api.conversion;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class ConversionRegistry {

    private final List<TypeConverter> converters;

    public ConversionRegistry(List<TypeConverter> converters) {
        this.converters = new ArrayList<>( converters );
    }

    public ConversionRegistry(TypeConverter... converters) {
        this.converters = Arrays.asList(converters);
    }

    /**
     * @throws IllegalArgumentException When it is not able to convert the given value to the desired class.
     */
    public <T,S> T convert(Class<S> sourceClz, Class<T> targetClz, S value) {

        for (TypeConverter converter : converters) {

            Type[] genericInterfaces = converter.getClass().getGenericInterfaces();
            ParameterizedType typeConverterIface = (ParameterizedType) genericInterfaces[0];
            Type sourceType = typeConverterIface.getActualTypeArguments()[0];
            Type targetType = typeConverterIface.getActualTypeArguments()[1];

            if(sourceType.equals(sourceClz) && targetType.equals(targetClz)){
                try {
                    return (T) converter.convert(value);
                } catch (RuntimeException e) {
                    // ok, let's proceed with the next converter
                }
            }
        }
        throw new IllegalArgumentException(
                format("Cannot convert value '%s' of declared type '%s' to the desired type '%s'.",
                        String.valueOf(value), sourceClz.getSimpleName(), targetClz.getSimpleName())
        );

    }

}
