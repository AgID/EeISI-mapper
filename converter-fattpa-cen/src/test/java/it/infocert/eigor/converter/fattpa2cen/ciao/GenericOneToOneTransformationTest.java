package it.infocert.eigor.converter.fattpa2cen.ciao;

import com.google.common.io.Resources;
import it.infocert.eigor.converter.fattpa2cen.mapping.GenericOneToOneTransformation;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0027SellerName;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class GenericOneToOneTransformationTest {

    @Test
    public void mappingTest() throws Exception {
        URL italianInvoiceUrl = Resources.getResource("fatt-pa-plain-vanilla.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(italianInvoiceUrl.toURI().toString());
        } catch ( IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        assert doc != null;
        doc.getDocumentElement().normalize();

        String xPathExpression = "//FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        String className = BT0027SellerName.class.getName();
        String cenPath = "/BG0004/BT0027";

        GenericOneToOneTransformation transformator = new GenericOneToOneTransformation(xPathExpression,
                className, cenPath);

        BG0000Invoice invoice = new BG0000Invoice();

        transformator.transform(doc, null, invoice);

        assertThat(invoice.getBG0004Seller(), hasSize(1));
        assertThat(invoice.getBG0004Seller().get(0).getBT0027SellerName(), hasSize(1));
        assertEquals("AZIENDA TEST S.p.A.", invoice.getBG0004Seller().get(0).getBT0027SellerName().get(0).toString());
    }
}