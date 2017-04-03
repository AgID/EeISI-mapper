package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.converter.fattpa2cen.mapping.FattPA2CenMapper;
import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.InputStream;

public class FattPA2CenConverter implements ToCenConversion {

    public BG0000Invoice convert(InputStream input) {
        InvoiceXMLUnmarshaller<FatturaElettronicaType> unmarshaller = new InvoiceXMLUnmarshaller<>("it.infocert.eigor.converter.fattpa2cen.models");
        JAXBElement<FatturaElettronicaType> element = null;
        try {
            element = unmarshaller.unmarshalInvoiceFile(input);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        Preconditions.checkNotNull(element);
        FatturaElettronicaType fattura = element.getValue();

        return FattPA2CenMapper.mapToCoreInvoice(fattura);
    }

    @Override
    public boolean support(String format) {
        return false;
    }
}
