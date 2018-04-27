package it.infocert.eigor.api.conversion.converter;

import java.math.BigDecimal;

public class StringToBigDecimalConverter extends FromStringTypeConverter<BigDecimal> {

    private StringToBigDecimalConverter() {
    }

    @Override
    public BigDecimal convert(String in) {
        return new BigDecimal(in);
    }

    @Override
    public Class<BigDecimal> getTargetClass() {
        return BigDecimal.class;
    }

    public static TypeConverter<String, BigDecimal> newConverter() {
        return new StringToBigDecimalConverter();
    }
}
