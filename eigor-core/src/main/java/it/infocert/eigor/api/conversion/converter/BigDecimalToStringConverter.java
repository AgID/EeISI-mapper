package it.infocert.eigor.api.conversion.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class BigDecimalToStringConverter extends ToStringTypeConverter<BigDecimal> {

    private final String formatPattern;

    public static TypeConverter<BigDecimal, String> newConverter(String formatPattern) {
        return new BigDecimalToStringConverter(formatPattern);
    }

    private BigDecimalToStringConverter(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    @Override
    public String convert(BigDecimal value) {
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat(formatPattern, dfs);
        return df.format(value);
    }

    @Override
    public Class<BigDecimal> getSourceClass() {
        return BigDecimal.class;
    }
}
