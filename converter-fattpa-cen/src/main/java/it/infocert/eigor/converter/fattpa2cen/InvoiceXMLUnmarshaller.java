package it.infocert.eigor.converter.fattpa2cen;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class InvoiceXMLUnmarshaller<T> {
    private String packageName;

    public InvoiceXMLUnmarshaller(String packageName) {
        this.packageName = packageName;
    }

    @SuppressWarnings("unchecked")
    public JAXBElement<T> unmarshalInvoiceFile(InputStream xmlFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(this.packageName);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object unmarshal = unmarshaller.unmarshal(xmlFile);
        return (JAXBElement<T>) unmarshal;
    }
}