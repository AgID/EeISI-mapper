package it.infocert.eigor.api.conversion;

import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

public class LocalDateToXMLGregorianCalendarConverter implements TypeConverter<LocalDate, XMLGregorianCalendar> {

    public static TypeConverter<LocalDate, XMLGregorianCalendar> newConverter(){
        return new LocalDateToXMLGregorianCalendarConverter();
    }

    LocalDateToXMLGregorianCalendarConverter() {
    }

    @Override
    public XMLGregorianCalendar convert(LocalDate localDate) {
        GregorianCalendar gcal = new GregorianCalendar(
                localDate.getYear(),
                localDate.getMonthOfYear() -1 ,
                localDate.getDayOfMonth()
        );
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Class<XMLGregorianCalendar> getTargetClass() {
        return XMLGregorianCalendar.class;
    }

    @Override
    public Class<LocalDate> getSourceClass() {
        return LocalDate.class;
    }
}
