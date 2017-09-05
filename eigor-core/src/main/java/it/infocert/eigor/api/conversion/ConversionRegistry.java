package it.infocert.eigor.api.conversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class ConversionRegistry {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<TypeConverter> converters;



    public ConversionRegistry(List<TypeConverter> converters) {
        this.converters = new ArrayList<>(converters);
    }

    public ConversionRegistry(TypeConverter... converters) {
        this.converters = Arrays.asList(converters);
    }

    /**
     * Converts the given value of type sourceClz, into the corresponding value of type targetClz.
     *
     * @param sourceClz The type of the value that should be converted.
     * @param targetClz The type value should be converted to.
     * @param value     The value that should be converted to targetClz.
     * @throws IllegalArgumentException When it is not able to convert the given value to the desired class.
     */
    public <T, S> T convert(Class<? extends S> sourceClz, Class<? extends T> targetClz, S value) {

        for (TypeConverter converter : converters) {

            if (value.getClass().isAssignableFrom(converter.getSourceClass())) {
                if (targetClz.isAssignableFrom(converter.getTargetClass())) {
                    log.trace("Trying to convert value '{}' with converter '{}'.", value, converter);
                    try {
                        return (T) converter.convert(value);
                    } catch (Exception e) {
                        log.trace("Skipped converter '{}' because of error.", converter, e);
                    }
                } else {
                    log.trace("Skipped converter '{}' because it convertes to '{}' of type '{}' but required target is '{}'.",
                            converter,
                            sourceClz.getName(),
                            converter.getTargetClass().getName(),
                            targetClz.getName());
                }
            } else {
                log.trace("Skipped converter '{}' because it converts from type '{}' but required source is '{}'.",
                        converter,
                        converter.getSourceClass().getName(),
                        sourceClz.getName());
            }
        }
        throw new IllegalArgumentException(
                format("Cannot convert value '%s' of declared type '%s' to the desired type '%s'.",
                        String.valueOf(value), sourceClz.getSimpleName(), targetClz.getSimpleName())
        );

    }
}
