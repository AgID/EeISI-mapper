package it.infocert.eigor.api.mapping.toCen;

import com.google.common.io.Resources;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BT0099DocumentLevelChargeAmount;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GenericOneToOneTransformationTest {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);

    URL italianInvoiceUrl;

    @Before
    public void setUp() throws Exception {
        italianInvoiceUrl = Resources.getResource("examples/fattpa/fatt-pa-plain-vanilla.xml");
    }

    @Test
    public void shouldMapNullDoubles() throws Exception {

        Document doc = docFromString("<doc>" +
                "<string>hello</string>" +
                "<double></double>" +
                "</doc>");

        GenericOneToOneTransformation transformator = new GenericOneToOneTransformation(
                "//doc/double",
                "/BG0021/BT0099",
                new Reflections("it.infocert"));

        BG0000Invoice invoice = new BG0000Invoice();
        transformator.transform(doc, invoice, new ArrayList<ConversionIssue>());


        assertThat(
                invoice.getBG0021DocumentLevelCharges(0)
                        .getBT0099DocumentLevelChargeAmount(0).getValue(),
                equalTo(123.40)
        );

    }

    @Test
    public void shouldMapNotNullDoubles() throws Exception {

        Document doc = docFromString("<doc>" +
                "<string>hello</string>" +
                "<double>123.4</double>" +
                "</doc>");

        GenericOneToOneTransformation transformator = new GenericOneToOneTransformation(
                "//doc/double",
                "/BG0021/BT0099",
                new Reflections("it.infocert"));

        BG0000Invoice invoice = new BG0000Invoice();
        transformator.transform(doc, invoice, new ArrayList<ConversionIssue>());


        assertThat(
                invoice.getBG0021DocumentLevelCharges(0)
                        .getBT0099DocumentLevelChargeAmount(0).getValue(),
                equalTo(123.40)
        );

    }

    @Test
    public void mappingTest() throws Exception {

        Document doc = docFromUri(italianInvoiceUrl.toURI().toString());

        // given
        String xPathExpression = "//FatturaElettronicaHeader/CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
        String cenPath = "/BG0004/BT0027";

        // when
        GenericOneToOneTransformation transformator = new GenericOneToOneTransformation(xPathExpression, cenPath, new Reflections("it.infocert"));

        // then
        BG0000Invoice invoice = new BG0000Invoice();
        List<ConversionIssue> errors = new ArrayList<>();
        transformator.transform(doc, invoice, errors);

        assertThat(invoice.getBG0004Seller(), hasSize(1));
        assertThat(invoice.getBG0004Seller().get(0).getBT0027SellerName(), hasSize(1));
        assertEquals("AZIENDA TEST S.p.A.", invoice.getBG0004Seller().get(0).getBT0027SellerName().get(0).toString());
    }

    private Document docFromUri(String uri) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(uri);
        assert doc != null;
        doc.getDocumentElement().normalize();
        return doc;
    }

    private Document docFromString(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        assert doc != null;
        doc.getDocumentElement().normalize();
        return doc;
    }

}