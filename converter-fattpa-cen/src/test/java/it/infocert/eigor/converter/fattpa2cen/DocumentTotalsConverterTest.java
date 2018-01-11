package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentTotalsConverterTest {

    private BG0000Invoice invoice;
    private Document document;
    private DocumentTotalsConverter sut;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        document = createXmlInvoice(new Document());
        sut = new DocumentTotalsConverter();
    }

    @Test
    public void name() throws Exception {
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList());
        final Double value = invoice.getBG0022DocumentTotals(0).getBT0112InvoiceTotalAmountWithVat(0).getValue();
        assertEquals(new Double(3.0d), value);
    }

    private Document createXmlInvoice(Document document) {
        final Element fatturaElettronicaBody = new Element("FatturaElettronicaBody");
        final Element fatturaElettronica = new Element("FatturaElettronica").addContent(fatturaElettronicaBody);
        final Element datiBeniServizi = new Element("DatiBeniServizi");

        final Element datiRiepilogo = new Element("DatiRiepilogo")
                .addContent(new Element("ImponibileImporto").setText("2"))
                .addContent(new Element("Imposta").setText("1"));

        fatturaElettronicaBody.addContent(datiBeniServizi.addContent(datiRiepilogo));
        fatturaElettronicaBody.addContent(new Element("DatiGenerali").addContent(new Element("DatiGeneraliDocumento")));
        document.setRootElement(fatturaElettronica);
        return document;
    }
}