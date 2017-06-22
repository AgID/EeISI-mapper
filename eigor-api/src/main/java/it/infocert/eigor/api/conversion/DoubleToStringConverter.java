package it.infocert.eigor.api.conversion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DoubleToStringConverter extends ToStringTypeConverter<Double>{

    private final String formatPattern;

    public DoubleToStringConverter(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    @Override
    public String convert(Double value) {
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat(formatPattern, dfs);
        return df.format(value);
    }

    @Override
    public Class<Double> getSourceClass() {
        return Double.class;
    }
}
