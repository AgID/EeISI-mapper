package it.infocert.eigor.api.conversion.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

public class StringToBigDecimalPercentageConverter extends FromStringTypeConverter<BigDecimal> {

    private StringToBigDecimalPercentageConverter() {
    }

    @Override public BigDecimal convert(String in) {
        DecimalFormat df = new DecimalFormat("#0.0%");
        df.setParseBigDecimal(true);
        try {
            return (BigDecimal) df.parse(in);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Class<BigDecimal> getTargetClass() {
        return BigDecimal.class;
    }

    public static TypeConverter newConverter() {
        return new StringToBigDecimalPercentageConverter();
    }
}
