package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionBetweenTypesFailedException;

public abstract class ToStringTypeConverter<T> implements TypeConverter<T, String> {
    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    /**
     * Utility method that throws a proper exception when the value to convert is null.
     * To be used in case a converter does not want to manage null values.
     */
    protected void checkNotNull(Object valueToConvert) throws ConversionBetweenTypesFailedException {
        if(valueToConvert == null) throw new ConversionBetweenTypesFailedException(
                getSourceClass(),
                String.class,
                null);
    }

}
