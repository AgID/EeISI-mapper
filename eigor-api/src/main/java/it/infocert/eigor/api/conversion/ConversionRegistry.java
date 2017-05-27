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

            log.trace("Trying to convert value '{}' with converter '{}'.", value, converter);

            Type[] genericInterfaces = converter.getClass().getGenericInterfaces();

            if(genericInterfaces == null || genericInterfaces.length == 0){

                try {
                    Method theConvertMethod = null;
                    Method[] methods = converter.getClass().getMethods();
                    for (Method method : methods) {
                        if(method.getName().equals("convert") && method.getParameterTypes().length == 1){
                            theConvertMethod = method;
                            break;
                        }
                    }

                    Object result = theConvertMethod.invoke(converter, value);
                    T actualResult = (T)result;
                    return actualResult;
                } catch (IllegalAccessException | InvocationTargetException | ClassCastException e ) {
                    log.trace("Skipped converter '{}' because of error.", converter, e);
                }

            }else{

                ParameterizedType typeConverterIface = (ParameterizedType) genericInterfaces[0];
                Type sourceType = typeConverterIface.getActualTypeArguments()[0];
                Type targetType = typeConverterIface.getActualTypeArguments()[1];

                try {
                    return (T) converter.convert(value);
                } catch (RuntimeException e) {
                    log.trace("Skipped converter '{}' because of error.", converter, e);
                }

            }


        }
        throw new IllegalArgumentException(
                format("Cannot convert value '%s' of declared type '%s' to the desired type '%s'.",
                        String.valueOf(value), sourceClz.getSimpleName(), targetClz.getSimpleName())
        );

    }

}
