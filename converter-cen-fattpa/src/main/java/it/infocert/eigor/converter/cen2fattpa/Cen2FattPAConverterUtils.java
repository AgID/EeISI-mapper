package it.infocert.eigor.converter.cen2fattpa;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

class Cen2FattPAConverterUtils {

    static XMLGregorianCalendar fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(LocalDate dateTime) {
        XMLGregorianCalendar invoiceDate;
        try {
            invoiceDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            invoiceDate.setDay(dateTime.getDayOfMonth());
            invoiceDate.setMonth(dateTime.getMonthValue());
            invoiceDate.setYear(dateTime.getYear());

        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        return invoiceDate;
    }


    static String getCountryFromVATString(String vat) {
        if (vat == null
                || vat.length() < 2
                || !Character.isAlphabetic(vat.charAt(0))
                || !Character.isAlphabetic(vat.charAt(1))) {
            return "";
        }
        return vat.substring(0, 2);
    }

    static String getCodeFromVATString(String vat) {
        if (vat == null || vat.length() < 2) {
            return "";
        }
        if (!Character.isAlphabetic(vat.charAt(0))
                || !Character.isAlphabetic(vat.charAt(1))) {
            // if no country code, the whole vat is considered vat code
            return vat.trim();
        }
        return vat.substring(2).trim();
    }

    static BigDecimal doubleToBigDecimalWith2Decimals(Double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd;
    }
}
