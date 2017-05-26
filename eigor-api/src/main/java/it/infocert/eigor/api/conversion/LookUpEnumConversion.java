package it.infocert.eigor.api.conversion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LookUpEnumConversion<Target> implements TypeConverter<String, Target> {

    private Class theEnum;

    public LookUpEnumConversion(Class theEnum){
        if(!theEnum.isEnum()) throw new IllegalArgumentException("Not an enum");
        this.theEnum = theEnum;
    }

    public Target convert(final String value) {

        try {
            Method theMethod = theEnum.getMethod("valueOf", new Class[] { String.class });
            return (Target) theMethod.invoke(theEnum, new String[]{value});
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

}
