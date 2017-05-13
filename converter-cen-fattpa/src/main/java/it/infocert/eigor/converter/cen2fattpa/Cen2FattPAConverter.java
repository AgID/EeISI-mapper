package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Cen2FattPAConverter implements FromCenConversion {

    private ObjectFactory factory = new ObjectFactory();

    /**
     * Create XML based on Cen2FattPAConverter
     * Apply XSD validation on resulting XML
     * @param invoice
     * @return BinaryConversionResult object wrapping xml data and resulting errors from converting and XSD validation
     */
    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) {

            List<Exception> errors = new ArrayList<Exception>();
            byte[] xml = makeXML(invoice, errors);
            Cen2FattPAConverterUtils.validateXmlAgainstSchemaDefinition(xml, errors);

            return new BinaryConversionResult(xml, errors);
    }

    @Override
    public boolean support(String format) {
        return IConstants.CONVERTER_SUPPORT.equals(format.toLowerCase().trim());
    }

    @Override
    public String getSupportedFormats() {
        return IConstants.SUPPORTED_FORMATS;
    }

    @Override
    public String extension() {
        return "xml";
    }

    private byte[] makeXML(BG0000Invoice invoice, List<Exception> errors) {

        StringWriter xmlOutput = new StringWriter();

        // INVOICE CREATION
        HeaderFatturaConverter hfc = new HeaderFatturaConverter(factory, invoice, errors);
        hfc.copyRequiredOne2OneFields();

        BodyFatturaConverter bfc = new BodyFatturaConverter(factory, invoice, errors);
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
            errors.add(new RuntimeException(IConstants.ERROR_XML_GENERATION));
        }
        return xmlOutput.toString().getBytes();
    }

}

