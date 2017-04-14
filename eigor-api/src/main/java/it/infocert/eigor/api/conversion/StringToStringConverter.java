package it.infocert.eigor.api.conversion;

public class StringToStringConverter implements TypeConverter<String, String> {

    @Override public String convert(String in) {
        return in;
    }
}
