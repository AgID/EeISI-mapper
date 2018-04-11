package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.conversion.converter.FromStringTypeConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link TypeConverter Converter} that transform a {@link String} into an item of an enum with the same name.
 *
 * <p>
 *     For instance, let's suppose there's this enum.
 *     <pre>
 *         public enum Iso31661CountryCodes {
 *           AF, AX;
 *         }
 *     </pre>
 *
 *     It is possible to create and use such converter:
 *     <pre>
 *         LookUpEnumConversion sut = new LookUpEnumConversion<Iso31661CountryCodes>(Iso31661CountryCodes.class);
 *         sut.convert("AF"); // returns Iso31661CountryCodes.AF
 *     </pre>
 * </p>
 */
public class LookUpEnumConversion<Target> extends FromStringTypeConverter<Target> {

    private Class<Target> theEnum;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static <Target> TypeConverter<String, Target> newConverter(Class<Target> theEnum){
        return new LookUpEnumConversion(theEnum);
    }

    LookUpEnumConversion(Class<Target> theEnum){
        if(!theEnum.isEnum()) throw new IllegalArgumentException("Provided class '" + theEnum + "' is not an enum.");
        this.theEnum = theEnum;
    }

    public Target convert(final String value) {
        try {
            Method theMethod = theEnum.getMethod("valueOf", String.class);
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

    @Override
    public Class<Target> getTargetClass() {
        return theEnum;
    }


}
