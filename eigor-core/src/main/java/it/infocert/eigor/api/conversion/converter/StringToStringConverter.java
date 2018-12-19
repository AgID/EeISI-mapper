package it.infocert.eigor.api.conversion.converter;

public class StringToStringConverter extends FromStringTypeConverter<String> {

    private StringToStringConverter() {
    }

    @Override public String convert(String in) {
        return in;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    public static TypeConverter<String, String> newConverter() {
        return new StringToStringConverter();
    }
}
