package it.infocert.eigor.api.conversion;

public abstract class FromStringTypeConverter<T> implements TypeConverter<String, T> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }
}
