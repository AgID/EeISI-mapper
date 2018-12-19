package it.infocert.eigor.converter.cen2fattpa.jaxbindings;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Amount8DecimalAdapter {

    public static String marshal(BigDecimal v) {
        return new DecimalFormat("#0.00000000", DecimalFormatSymbols.getInstance(Locale.US)).format(v);
    }

    public static BigDecimal unmarshal(String v) {
        BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Error parsing %s", v), e);
        }
        return bigDecimal;
    }

}
