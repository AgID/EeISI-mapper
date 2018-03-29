package it.infocert.eigor.api.conversion.converter;

public abstract class FromStringTypeConverter<T> implements TypeConverter<String, T> {

    protected FromStringTypeConverter() {
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }
}
