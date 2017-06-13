package it.infocert.eigor.converter.fattpa2cen.mapping;

import com.google.common.io.Resources;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GenericOneToOneTransformationTest {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);

    @Test
    public void mappingTest() throws Exception {
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

        String xPathExpression = "//FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        String cenPath = "/BG0004/BT0027";

        GenericOneToOneTransformation transformator = new GenericOneToOneTransformation(xPathExpression, cenPath);

        BG0000Invoice invoice = new BG0000Invoice();

        transformator.transform(doc, null, invoice);

        assertThat(invoice.getBG0004Seller(), hasSize(1));
        assertThat(invoice.getBG0004Seller().get(0).getBT0027SellerName(), hasSize(1));
        assertEquals("AZIENDA TEST S.p.A.", invoice.getBG0004Seller().get(0).getBT0027SellerName().get(0).toString());
    }
}