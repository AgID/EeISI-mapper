package it.infocert.eigor.api.conversion.converter;

public class StringToDoubleConverter extends FromStringTypeConverter<Double> {

    private StringToDoubleConverter() {
    }

    @Override public Double convert(String in) {
        return Double.parseDouble(in);
    }

    @Override
    public Class<Double> getTargetClass() {
        return Double.class;
    }

    public static TypeConverter<String, Double> newConverter() {
        return new StringToDoubleConverter();
    }
}
