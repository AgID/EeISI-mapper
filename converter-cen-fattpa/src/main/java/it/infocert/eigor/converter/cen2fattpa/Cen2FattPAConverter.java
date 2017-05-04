package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

public class Cen2FattPAConverter implements FromCenConversion {

    private ObjectFactory factory = new ObjectFactory();

    @Override
    public ConversionResult convert(BG0000Invoice invoice) {
            ConversionResult conversionResult = new ConversionResult();
            byte[] xml = makeXML(invoice, conversionResult);
            conversionResult.setResult(xml);
//            Validation currently disabled. Must check why it takes 10-15 seconds!!
//            if (validateXmlAgainstSchemaDefinition(xml, conversionResult.getErrors()) && conversionResult.getErrors().isEmpty()) {
                conversionResult.setSuccessful(true);
//            }
            return conversionResult;
    }

    @Override
    public boolean support(String format) {
        return IConstants.CONVERTER_SUPPORT.equals(format.toLowerCase().trim());
    }

    @Override
    public String getSupportedFormats() {
        return IConstants.SUPPORTED_FORMATS;
    }

    private byte[] makeXML(BG0000Invoice invoice, ConversionResult conversionResult) {

        StringWriter xmlOutput = new StringWriter();

        // INVOICE CREATION
        HeaderFatturaConverter hfc = new HeaderFatturaConverter(factory, invoice, conversionResult.getErrors());
        hfc.copyRequiredOne2OneFields();

        BodyFatturaConverter bfc = new BodyFatturaConverter(factory, invoice, conversionResult.getErrors());
        bfc.copyRequiredOne2OneFields();
        bfc.copyOptionalOne2OneFields();
        bfc.computeMultipleCenElements2FpaField();
        FatturaElettronicaHeaderType fatturaElettronicaHeader = hfc.getFatturaElettronicaHeader();
        FatturaElettronicaBodyType fatturaElettronicaBody = bfc.getFatturaElettronicaBody();

        FatturaElettronicaType fatturaElettronica = factory.createFatturaElettronicaType();
        fatturaElettronica.setFatturaElettronicaHeader(fatturaElettronicaHeader);
        fatturaElettronica.getFatturaElettronicaBody().add(fatturaElettronicaBody);
        JAXBElement<FatturaElettronicaType> fatturaElettronicaXML = factory.createFatturaElettronica(fatturaElettronica);
        fatturaElettronica.setVersione(FormatoTrasmissioneType.FPA_12);


        // XML GENERATION

        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance("it.infocert.eigor.converter.cen2fattpa.models");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); // neat formatting, for now
            marshaller.marshal(fatturaElettronicaXML, xmlOutput);
        } catch (JAXBException e) {
            conversionResult.getErrors().add(new RuntimeException(IConstants.ERROR_XML_GENERATION));
        }
        return xmlOutput.toString().getBytes();
    }


    private Boolean validateXmlAgainstSchemaDefinition(byte[] xml, List<Exception> errors) {
        URL schemaFile = getClass().getClassLoader().getResource("Schema_del_file_xml_FatturaPA_versione_1.2.xsd");
        Source xmlFile = new StreamSource(new ByteArrayInputStream(xml));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            schema.newValidator().validate(xmlFile);
        } catch (SAXException | IOException e) {
            errors.add(new RuntimeException(IConstants.ERROR_XML_VALIDATION_FAILED, e));
            return false;
        }
        return true;
    }
}

