package it.infocert.eigor.api.conversion.converter;


import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.ConversionRegistry;

/**
 * Generic service that converts a value in another format.
 * I.e. it can convert a {@link java.util.Date} in a {@link String}, a {@link String} in a {@link Number} and so on.
 *<p>
 * {@link TypeConverter converters} can be grouped in a {@link ConversionRegistry} to have an easier way to convert values.
 *</p>
 * <p>
 *     A {@link TypeConverter converter} should throw a {@link RuntimeException runtime exception} if it is not able to convert the given value.
 * </p>
 *
 * @param <Source> The type of the variable this converter is able to convert in something else.
 * @param <Target> The type this converter can convert to.
 *
 * @see ConversionRegistry
 */
public interface TypeConverter<Source, Target> {

    Target convert(Source source) throws ConversionFailedException;

    Class<Target> getTargetClass();

    Class<Source> getSourceClass();

}
