package it.infocert.eigor.api.conversion;

public abstract class ToStringTypeConverter<T> implements TypeConverter<T, String> {
    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }
}
