package it.infocert.eigor.converter.cen2fattpa.jaxbindings;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;

public class DateTimeAdapter {

    public static String marshal(XMLGregorianCalendar v) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat.format(v.toGregorianCalendar().getTime());
    }

    public static XMLGregorianCalendar unmarshal(String v) {
        DatatypeFactory datatypeFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(String.format("Error parsing %s", v), e);
        }
        return datatypeFactory.newXMLGregorianCalendar(v);
    }

}
