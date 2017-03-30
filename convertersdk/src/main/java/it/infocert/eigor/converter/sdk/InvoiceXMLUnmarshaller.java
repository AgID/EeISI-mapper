package it.infocert.eigor.converter.sdk;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class InvoiceXMLUnmarshaller<T> {

    private String packageName;

    public InvoiceXMLUnmarshaller(String packageName) {
        this.packageName = packageName;
    }

    public JAXBElement<T> unmarshalInvoiceFile(File xmlFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(this.packageName);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object unmarshal = unmarshaller.unmarshal(xmlFile);
        return (JAXBElement<T>) unmarshal;
    }


}
