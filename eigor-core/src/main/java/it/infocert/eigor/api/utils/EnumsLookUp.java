package it.infocert.eigor.api.utils;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public class EnumsLookUp {
    private static final Logger log = LoggerFactory.getLogger(EnumsLookUp.class);

    public static Enum getEnumElementFromFields(Class<? extends Enum> enumClass, final Object... parameters) {

        Constructor<?>[] constructors = enumClass.getDeclaredConstructors();
        Set<Constructor<?>> validConstructors = Stream.create(constructors).filter(new Filter<Constructor<?>>() {
            @Override
            public boolean apply(Constructor<?> constructor) {
                int count = 0;
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                for (int i = 2; i < parameterTypes.length; i++) {
                    int valid = 0;
                    for (Object parameter : parameters) {
                        if (parameterTypes[i].equals(parameter.getClass())) {
                            valid++;
                        }
                    }
                    if (valid > 0) {
                        count++;
                    }
                }

                int actualLenght = parameterTypes.length - 2 ;
                return count == actualLenght && actualLenght == parameters.length;
            }
        }).toSet();

        for (Constructor<?> validConstructor : validConstructors) {
            try {
                validConstructor.setAccessible(true);
                return (Enum) validConstructor.newInstance(parameters);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
