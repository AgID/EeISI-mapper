package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.EigorRuntimeException;
import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class LocalDateToXMLGregorianCalendarConverter implements TypeConverter<LocalDate, XMLGregorianCalendar> {
    @Override
    public XMLGregorianCalendar convert(LocalDate localDate) {
        GregorianCalendar gcal = new GregorianCalendar(
                localDate.getYear(),
                localDate.getMonthOfYear() -1 ,
                localDate.getDayOfMonth()
        );
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd").format(localDate.toDate()));
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
