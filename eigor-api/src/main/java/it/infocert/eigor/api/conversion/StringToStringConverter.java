package it.infocert.eigor.api.conversion;

public class StringToStringConverter extends FromStringTypeConverter<String> {

    @Override public String convert(String in) {
        return in;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }


}
