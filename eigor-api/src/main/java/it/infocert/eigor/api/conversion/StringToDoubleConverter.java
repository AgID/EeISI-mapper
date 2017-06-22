package it.infocert.eigor.api.conversion;

public class StringToDoubleConverter extends FromStringTypeConverter<Double> {

    @Override public Double convert(String in) {
        return Double.parseDouble(in);
    }

    @Override
    public Class<Double> getTargetClass() {
        return Double.class;
    }


}
