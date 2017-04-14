package it.infocert.eigor.api.conversion;

public class StringToDoubleConverter implements TypeConverter<String, Double> {

    @Override public Double convert(String in) {
        return Double.parseDouble(in);
    }
}
