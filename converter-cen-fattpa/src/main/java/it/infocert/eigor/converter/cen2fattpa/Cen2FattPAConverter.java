package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class Cen2FattPAConverter implements FromCenConversion {

    private ObjectFactory factory = new ObjectFactory();

    @Override
    public ConversionResult convert(BG0000Invoice invoice) {
            ConversionResult conversionResult = new ConversionResult();
            byte[] xml = makeXML(invoice, conversionResult);
            conversionResult.setResult(xml);
            if (Cen2FattPAConverterUtils.validateXmlAgainstSchemaDefinition(xml, conversionResult.getErrors()) && conversionResult.getErrors().isEmpty()) {
                conversionResult.setSuccessful(true);
            }
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

}

