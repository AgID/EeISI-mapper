package it.infocert.eigor.api.conversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LookUpEnumConversion<Target> implements TypeConverter<String, Target> {

    private Class theEnum;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public LookUpEnumConversion(Class theEnum){
        if(!theEnum.isEnum()) throw new IllegalArgumentException("Provided class '" + theEnum + "' is not an enum.");
        this.theEnum = theEnum;
    }

    public Target convert(final String value) {
        try {
            Method theMethod = theEnum.getMethod("valueOf", new Class[] { String.class });
            Target invoke = (Target) theMethod.invoke(theEnum, new String[] { value });
            log.trace("Value '{}' converted to '{}'.", value, invoke);
            return invoke;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override public String toString() {
        return this.getClass().getSimpleName() + " on " + theEnum.getSimpleName();
    }
}
