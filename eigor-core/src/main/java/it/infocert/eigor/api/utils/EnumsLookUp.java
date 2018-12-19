package it.infocert.eigor.api.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumsLookUp {
    private static final Logger log = LoggerFactory.getLogger(EnumsLookUp.class);

    public static Enum getEnumElementFromFields(Class<? extends Enum> enumClass, final Object... parameters) {

        Constructor<?>[] constructors = enumClass.getDeclaredConstructors();

        Set<Constructor<?>> validConstructors = Arrays.stream(constructors)
                .filter( constructor ->
                    {
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
                    })
                .collect(Collectors.toSet());

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
