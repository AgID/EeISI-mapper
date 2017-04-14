package it.infocert.eigor.api.conversion;

import java.text.DecimalFormat;
import java.text.ParseException;

public class StringToDoublePercentageConverter implements TypeConverter<String, Double> {

    @Override public Double convert(String in) {
        DecimalFormat df = new DecimalFormat("#0.0%");
        try {
            return (Double) df.parse(in).doubleValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
