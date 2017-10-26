package it.infocert.eigor.converter.cen2fattpa.jaxbindings;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateAdapter {

    public static String marshal(XMLGregorianCalendar v) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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