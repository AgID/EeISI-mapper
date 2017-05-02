package it.infocert.eigor.converter.ubl2cen;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.converter.ubl2cen.mapping.UblXpathMap;
import it.infocert.eigor.converter.ubl2cen.mapping.GenericOneToOneTransformation;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Ubl2Cen implements ToCenConversion {

    private static final Logger log = LoggerFactory.getLogger(Ubl2Cen.class);

    private static final String FORMAT = "ubl";

    @Override
    public BG0000Invoice convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        BG0000Invoice invoice = null;

        // read document (from xml to Document obj)
        Document document = getDocument(sourceInvoiceStream);

        // maps each path into BG/BT obj
        invoice = applyTransformations(document);

        return invoice;
    }

    protected Document getDocument(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(sourceInvoiceStream);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }

    protected BG0000Invoice applyTransformations(Document document) {
        BG0000Invoice invoice = new BG0000Invoice();

        UblXpathMap mapper = new UblXpathMap();
        Multimap<String, String> mapping = mapper.getMapping();
        for (Map.Entry<String, String> entry : mapping.entries()) {
            GenericOneToOneTransformation transformer = new GenericOneToOneTransformation(entry.getValue(), entry.getKey());
            transformer.transform(document, invoice);
        }

        log.info("transformed invoice: " + invoice);
        return invoice;
    }

    @Override
    public boolean support(String format) {
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList(FORMAT));
    }

}
