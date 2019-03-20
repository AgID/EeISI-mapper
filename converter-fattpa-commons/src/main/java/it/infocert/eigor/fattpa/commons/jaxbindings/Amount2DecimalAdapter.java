package it.infocert.eigor.fattpa.commons.jaxbindings;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Amount2DecimalAdapter {

    public static String marshal(BigDecimal v) {
        return new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US)).format(v);
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
