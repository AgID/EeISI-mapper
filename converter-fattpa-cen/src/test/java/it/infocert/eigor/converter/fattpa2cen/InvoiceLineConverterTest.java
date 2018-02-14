package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0129InvoicedQuantity;
import it.infocert.eigor.model.core.model.BT0130InvoicedQuantityUnitOfMeasureCode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvoiceLineConverterTest {

    private BG0000Invoice invoice;
    private Document document;
    private InvoiceLineConverter sut;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        document = createXmlInvoice(new Document());
        sut = new InvoiceLineConverter();

    }

    @Test
    public void shouldApplyBT129Default() throws Exception {
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN);
        final BT0129InvoicedQuantity bt129 = invoice.getBG0025InvoiceLine(0).getBT0129InvoicedQuantity(0);
        assertEquals(new Double(1d), bt129.getValue());
    }

    @Test
    public void shouldConvertBT129() throws Exception {
        document.getRootElement()
                .getChild("FatturaElettronicaBody")
                .getChild("DatiBeniServizi")
                .getChild("DettaglioLinee")
                .addContent(new Element("Quantita").setText("15"));

        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN);
        final BT0129InvoicedQuantity bt129 = invoice.getBG0025InvoiceLine(0).getBT0129InvoicedQuantity(0);
        assertEquals(new Double(15d), bt129.getValue());
    }

    @Test
    public void shouldConvertBT130() throws Exception {
        document.getRootElement()
                .getChild("FatturaElettronicaBody")
                .getChild("DatiBeniServizi")
                .getChild("DettaglioLinee")
                .addContent(new Element("UnitaMisura").setText(UnitOfMeasureCodes.C62_ONE.getCommonCode()));
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN);
        final BT0130InvoicedQuantityUnitOfMeasureCode unitOfMeasureCode = invoice.getBG0025InvoiceLine(0).getBT0130InvoicedQuantityUnitOfMeasureCode(0);
        assertEquals(UnitOfMeasureCodes.C62_ONE, unitOfMeasureCode.getValue());
    }

    @Test
    public void shouldApplyBT130Default() throws Exception {
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN);
        final BT0130InvoicedQuantityUnitOfMeasureCode unitOfMeasureCode = invoice.getBG0025InvoiceLine(0).getBT0130InvoicedQuantityUnitOfMeasureCode(0);
        assertEquals(UnitOfMeasureCodes.EACH_EA, unitOfMeasureCode.getValue());
    }

    private Document createXmlInvoice(Document document) {
        final Element fatturaElettronicaBody = new Element("FatturaElettronicaBody");
        final Element fatturaElettronica = new Element("FatturaElettronica").addContent(fatturaElettronicaBody);
        final Element datiBeniServizi = new Element("DatiBeniServizi");
        final Element dettaglioLinee = new Element("DettaglioLinee");

        fatturaElettronicaBody.addContent(datiBeniServizi.addContent(dettaglioLinee));
        fatturaElettronicaBody.addContent(new Element("DatiGenerali").addContent(new Element("DatiGeneraliDocumento")));
        document.setRootElement(fatturaElettronica);
        return document;
    }
}