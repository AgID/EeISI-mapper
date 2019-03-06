package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0020PaymentTerms;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FattPa2CenTest {

    private FattPa2Cen sut;
    private Document doc;
    private String xml;
    private final String num = "00001";

    @Before
    public void setUp() throws Exception {
        sut = new FattPa2Cen(new JavaReflections(), DefaultEigorConfigurationLoader.configuration());
        doc = setUpDocument();
        xml = convertXml();
    }

    @Test
    public void shouldMapInBT20AddingAReferenceOfTheSourceTag() throws Exception {

        // given
        InputStream sourceInvoiceStream = getResourceAsStream("issues/issue-eeisi214-fattpa.xml");
        assertNotNull( sourceInvoiceStream );

        // when
        sut.configure();
        ConversionResult<BG0000Invoice> convert = sut.convert(sourceInvoiceStream);

        // then
        assertFalse( convert.hasIssues() );

        BG0000Invoice result = convert.getResult();
        BT0020PaymentTerms bt20 = result.getBT0020PaymentTerms(0);

        assertEquals( "DettaglioPagamento=TP02, GiorniTerminiPagamento=99, ScontoPagamentoAnticipato=10.00, DataLimitePagamentoAnticipato=100", bt20.getValue() );

    }

    @Test
    public void shouldReturnAnInvoiceNumber() throws Exception {

        // given
        Document document = getXmlInvoiceResourceAsDocument(getResourceAsStream("examples/fattpa/fatt-pa-plain-vanilla.xml"));

        BG0000Invoice invoice = new BG0000Invoice();
        List<IConversionIssue> errors = new ArrayList<>();
        InvoiceNoteConverter bg0001 = new InvoiceNoteConverter();
        ConversionResult<BG0000Invoice> result = bg0001.toBG0001(document, invoice, errors);

        assertEquals("STORNO NOTA DEBITO INTERESSI DI MORA N. XXXX DEL 06.07.2011 COME DA NS. TRANSAZIONE", result.getResult().getBG0001InvoiceNote(0).getBT0022InvoiceNote().get(0).getValue().trim());
    }

    private InputStream getResourceAsStream(String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
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

    private Document getXmlInvoiceResourceAsDocument(InputStream sourceInvoiceStream) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setIgnoringBoundaryWhitespace(true);
        return saxBuilder.build(sourceInvoiceStream);
    }

}
