package it.infocert.eigor.api.conversion;

import java.text.DecimalFormat;
import java.text.ParseException;

public class StringToDoublePercentageConverter extends FromStringTypeConverter<Double> {

    StringToDoublePercentageConverter() {
    }

    @Override public Double convert(String in) {
        DecimalFormat df = new DecimalFormat("#0.0%");
        try {
            return df.parse(in).doubleValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Class<Double> getTargetClass() {
        return Double.class;
    }

    public static TypeConverter newConverter() {
        return new StringToDoublePercentageConverter();
    }
}
