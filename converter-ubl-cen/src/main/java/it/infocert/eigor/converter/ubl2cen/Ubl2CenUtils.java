package it.infocert.eigor.converter.ubl2cen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.infocert.eigor.api.ConversionIssue;

class Ubl2CenUtils {
	
	
	/**
     * @param xml    Byte array containing raw XML
     * @param errors List of exceptions, usually from BinaryConversionResult
     * @return true if XML is valid compared to XSD
     */
    static Boolean validateXmlAgainstSchemaDefinition(byte[] xml, List<ConversionIssue> errors) {
        URL schemaFile = Ubl2CenUtils.class.getClassLoader().getResource("xsd/Schema_del_file_xml_Ubl_versione_2.1.xsd");
        Source xmlFile = new StreamSource(new ByteArrayInputStream(xml));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            schema.newValidator().validate(xmlFile);
        } catch(SAXException e) {
        	errors.add(ConversionIssue.newWarning(new RuntimeException("XSD validation failed! Cause: " + e.getMessage(), e)));
            return false;
        } catch (IOException e) { 
        	errors.add(ConversionIssue.newError(new RuntimeException("XSD validation failed!", e)));
            return false;
        }
        return true;
    }
    
}
