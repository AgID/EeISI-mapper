package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.*;

public class Cen2FattPAConverter implements FromCenConversion {

    private ObjectFactory factory = new ObjectFactory();
    private static final String FORMAT = "fatturapa";


    /**
     * Create XML based on Cen2FattPAConverter
     * Apply XSD validation on resulting XML
     *
     * @param invoice
     * @return BinaryConversionResult object wrapping xml data and resulting issues from converting and XSD validation
     */
    @Override
    public BinaryConversionResult convert(BG0000Invoice invoice) {

        List<ConversionIssue> errors = new ArrayList<>();
        byte[] xml = makeXML(invoice, errors);
        Cen2FattPAConverterUtils.validateXmlAgainstSchemaDefinition(xml, errors);

        return new BinaryConversionResult(xml, errors);
    }

    @Override
    public boolean support(String format) {
        return IConstants.CONVERTER_SUPPORT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Collections.singletonList(FORMAT));
    }


    @Override
    public String extension() {
        return "xml";
    }

    @Override
    public String getMappingRegex() {
        return null;
    }

    private byte[] makeXML(BG0000Invoice invoice, List<ConversionIssue> errors) {

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

        JAXBContext context;
        try {
            context = JAXBContext.newInstance("it.infocert.eigor.converter.cen2fattpa.models");

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE); // neat formatting, for now
            marshaller.marshal(fatturaElettronicaXML, xmlOutput);
        } catch (JAXBException e) {
            errors.add(ConversionIssue.newError(new RuntimeException(IConstants.ERROR_XML_GENERATION)));
        }
        return xmlOutput.toString().getBytes();
    }

    @Override
    public String getName() {
        return "cen-fatturapa";
    }
}

