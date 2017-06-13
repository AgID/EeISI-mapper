package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import it.infocert.eigor.converter.fattpa2cen.mapping.FattPaXpathMap;
import it.infocert.eigor.converter.fattpa2cen.mapping.GenericOneToOneTransformation;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;

public class ITFattPA2CenConverterTest {

    private static Logger log = LoggerFactory.getLogger(FattPA2CenConverter.class);

    @Test
    public void test() throws Exception {
        URL italianInvoiceUrl = Resources.getResource("examples/fattpa/fatt-pa-plain-vanilla.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(italianInvoiceUrl.toURI().toString());
        } catch ( IOException | ParserConfigurationException e) {
            log.error(e.getMessage(), e);
        }
        assert doc != null;
        doc.getDocumentElement().normalize();

        FattPaXpathMap mapper = new FattPaXpathMap();

        Multimap<String, String> mapping = mapper.getMapping();

        BG0000Invoice invoice = new BG0000Invoice();

        for (Map.Entry<String, String> entry : mapping.entries()) {
        GenericOneToOneTransformation transformator = new GenericOneToOneTransformation(entry.getValue(), entry.getKey());
        transformator.transform(doc, null, invoice);
        }

        assertFalse(invoice.getBT0001InvoiceNumber().isEmpty());
    }
}