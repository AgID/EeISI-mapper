package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FattPa2CenTest {

    private FattPa2Cen sut;
    private Document doc;
    private String xml;
    private final String num = "00001";

    @Before
    public void setUp() throws Exception {
        sut = new FattPa2Cen(new Reflections("it.infocert"), mock(EigorConfiguration.class));
        doc = setUpDocument();
        xml = convertXml();
    }

    @Test
    public void shouldReturnAnInvoiceNumber() throws Exception {
        ConversionResult<BG0000Invoice> output = sut.convert(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        BG0000Invoice result = output.getResult();
        assertEquals(num, result.getBT0001InvoiceNumber(0).getValue());
    }

    private String convertXml() {
        return new XMLOutputter().outputString(doc);
    }

    private Document setUpDocument() {
        Element fatturaElettronica = new Element("FatturaElettronica");
        Element body = new Element("FatturaElettronicaBody");
        Element datiGenerali = new Element("DatiGenerali");
        Element datiGeneraliDocumento = new Element("DatiGeneraliDocumento");
        Element numero = new Element("Numero");

        numero.setText(num);

        datiGeneraliDocumento.setContent(numero);
        datiGenerali.setContent(datiGeneraliDocumento);
        body.setContent(datiGenerali);
        fatturaElettronica.setContent(body);

        return new Document(fatturaElettronica);
    }

}