package it.infocert.eigor.converter.cen2fattpa;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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
        for (int i = 0; i < 2; i++) {
            char c = vat.charAt(i);
            if (!Character.isAlphabetic(c)) {
                return "";
            }
        }
        return vat.substring(0,2);
    }

    static String getCodeFromVATString(String vat) {
        for (int i = 0; i < 2; i++) {
            char c = vat.charAt(i);
            if (!Character.isAlphabetic(c)) {
                return vat;
            }
        }
        return vat.substring(2).trim();
    }
}
