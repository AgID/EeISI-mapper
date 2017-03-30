package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.converter.sdk.Converter;
import it.infocert.eigor.converter.sdk.ConverterType;
import it.infocert.eigor.converter.sdk.InvoiceXMLUnmarshaller;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;

public class FattPA2CenConverter implements Converter{

    public BG0000Invoice convert(File xmlFile) {
        InvoiceXMLUnmarshaller<FatturaElettronicaType> unmarshaller = new InvoiceXMLUnmarshaller<>("it.infocert.eigor.converter.fattpa2cen.models");
        JAXBElement<FatturaElettronicaType> element = null;
        try {
            element = unmarshaller.unmarshalInvoiceFile(xmlFile);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        FatturaElettronicaType fattura = element.getValue();

        return FattPA2CenMapper.mapToCoreInvoice(fattura);
    }

    @Override
    public ConverterType getConverterType() {
        return ConverterType.INBOUND;
    }

    @Override
    public String getInputFormat() {
        return "FattPA";
    }
}
