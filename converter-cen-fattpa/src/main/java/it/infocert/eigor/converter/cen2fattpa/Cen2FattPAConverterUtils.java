package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;

class Cen2FattPAConverterUtils {

    static XMLGregorianCalendar fromLocalDateToXMLGregorianCalendarIgnoringTimeZone(LocalDate dateTime) {
        XMLGregorianCalendar invoiceDate;
        try {
            invoiceDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            invoiceDate.setDay(dateTime.getDayOfMonth());
            invoiceDate.setMonth(dateTime.getMonthOfYear());
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

    /**
     * @param xml    Byte array containing raw XML
     * @param errors List of exceptions, usually from BinaryConversionResult
     * @return true if XML is valid compared to XSD
     */
    static Boolean validateXmlAgainstSchemaDefinition(byte[] xml, List<ConversionIssue> errors) {
        URL schemaFile = Cen2FattPAConverterUtils.class.getClassLoader().getResource("converterdata/converter-cen-fattpa/xsd/Schema_del_file_xml_FatturaPA_versione_1.2.xsd");
        Source xmlFile = new StreamSource(new ByteArrayInputStream(xml));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            schema.newValidator().validate(xmlFile);
        } catch (SAXException | IOException e) {
            errors.add(ConversionIssue.newWarning(new RuntimeException(IConstants.ERROR_XML_VALIDATION_FAILED, e)));
            return false;
        }
        return true;
    }
}
