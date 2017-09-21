package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.EigorRuntimeException;
import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

public class LocalDateToXMLGregorianCalendarConverter implements TypeConverter<LocalDate, XMLGregorianCalendar> {
    @Override
    public XMLGregorianCalendar convert(LocalDate localDate) {
        GregorianCalendar gcal = new GregorianCalendar(
                localDate.getYear(),
                localDate.getMonthOfYear(),
                localDate.getDayOfMonth()
        );
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException e) {
            throw new EigorRuntimeException(e.getMessage(), e);
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
