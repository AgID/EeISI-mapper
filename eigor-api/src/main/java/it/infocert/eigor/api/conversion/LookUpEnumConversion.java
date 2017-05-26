package it.infocert.eigor.api.conversion;

import java.lang.reflect.InvocationTargetException;

public abstract class LookUpEnumConversion<Target extends Enum<Target>> implements TypeConverter<String, Target> {

    private Class<Enum<Target>> theEnum;

    public LookUpEnumConversion(Class theEnum){
        this.theEnum = theEnum;
    }

    public Target convert(final String value) {

        try {
            return (Target) theEnum.getClass().getMethod("valueOf", new Class[]{String.class}).invoke(value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException();
        }

    }

}
