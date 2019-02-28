package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BT0104DocumentLevelChargeReason;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DocumentTotalsConverterTest {

    private BG0000Invoice invoice;
    private Document document;
    private DocumentTotalsConverter sut;
    private static EigorConfiguration configuration;

    @BeforeClass
    public static void setUpConf() {
        configuration = DefaultEigorConfigurationLoader.configuration();
    }

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        sut = new DocumentTotalsConverter();
    }

    @Test
    public void testTotals() throws Exception {
        document = createXmlInvoiceWithDatiBollo(new Document());
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN, configuration);
        final BigDecimal value = invoice.getBG0022DocumentTotals(0).getBT0112InvoiceTotalAmountWithVat(0).getValue();
        assertEquals(new BigDecimal(3.0d), value);
    }

    @Test
    public void shouldMapDocumentLevelChargesBT104IfBolloVirtualeIsSI() {
        document = createXmlInvoiceWithDatiBollo(new Document());
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN, configuration);
        assertFalse(invoice.getBG0021DocumentLevelCharges().isEmpty());
        BG0021DocumentLevelCharges bg0021 = invoice.getBG0021DocumentLevelCharges().get(0);

        assertEquals(BigDecimal.ZERO, bg0021.getBT0099DocumentLevelChargeAmount().get(0).getValue());
        assertEquals(new BigDecimal("2.00"), bg0021.getBT0100DocumentLevelChargeBaseAmount().get(0).getValue());
        assertEquals(BigDecimal.ZERO, bg0021.getBT0101DocumentLevelChargePercentage().get(0).getValue());
        assertEquals(BigDecimal.ZERO, bg0021.getBT0103DocumentLevelChargeVatRate(0).getValue());

        BT0104DocumentLevelChargeReason bt0104 = bg0021.getBT0104DocumentLevelChargeReason(0);
        assertThat(bt0104.getValue(), is("SI"));
    }

    @Test
    public void shouldMapDocumentLevelChargesForDatiRitenuta() {
        document = createXmlInvoiceWithDatiRitenuta(new Document());
        sut.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_IN, configuration);
        assertFalse(invoice.getBG0021DocumentLevelCharges().isEmpty());
        BG0021DocumentLevelCharges bg0021 = invoice.getBG0021DocumentLevelCharges().get(0);

        assertThat(bg0021.getBT0099DocumentLevelChargeAmount(0).getValue(), is(BigDecimal.ZERO));
        assertThat(bg0021.getBT0100DocumentLevelChargeBaseAmount(0).getValue().toString(), is("200.00"));
        assertThat(bg0021.getBT0101DocumentLevelChargePercentage(0).getValue().toString(), is("20.00"));
        assertThat(bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0).getValue(), is(Untdid5305DutyTaxFeeCategories.E));
        assertThat(bg0021.getBT0104DocumentLevelChargeReason(0).getValue(), is("RT01 A"));

        assertThat(invoice.getBG0022DocumentTotals(0).getBT0113PaidAmount(0).getValue().toString(), is("200.00"));
        assertThat(invoice.getBT0020PaymentTerms(0).getValue(), is("BT-113 represents Withholding tax amount"));
    }

    private Document createXmlInvoiceWithDatiBollo(Document document) {
        final Element fatturaElettronicaBody = new Element("FatturaElettronicaBody");
        final Element fatturaElettronica = new Element("FatturaElettronica").addContent(fatturaElettronicaBody);
        final Element datiBeniServizi = new Element("DatiBeniServizi");

        final Element datiRiepilogo = new Element("DatiRiepilogo")
                .addContent(new Element("ImponibileImporto").setText("2"))
                .addContent(new Element("Imposta").setText("1"));

        fatturaElettronicaBody.addContent(datiBeniServizi.addContent(datiRiepilogo));
        fatturaElettronicaBody.addContent(new Element("DatiGenerali")
                .addContent(new Element("DatiGeneraliDocumento")
                        .addContent(new Element("DatiBollo")
                                .addContent(new Element("BolloVirtuale").setText("SI"))
                                .addContent(new Element("ImportoBollo").setText("2.00")))));
        document.setRootElement(fatturaElettronica);
        return document;
    }

    private Document createXmlInvoiceWithDatiRitenuta(Document document) {
        final Element fatturaElettronicaBody = new Element("FatturaElettronicaBody");
        final Element fatturaElettronica = new Element("FatturaElettronica").addContent(fatturaElettronicaBody);
        final Element datiBeniServizi = new Element("DatiBeniServizi");

        final Element datiRiepilogo = new Element("DatiRiepilogo")
                .addContent(new Element("ImponibileImporto").setText("2"))
                .addContent(new Element("Imposta").setText("1"));

        fatturaElettronicaBody.addContent(datiBeniServizi.addContent(datiRiepilogo));
        fatturaElettronicaBody.addContent(new Element("DatiGenerali")
                .addContent(new Element("DatiGeneraliDocumento")
                        .addContent(new Element("DatiRitenuta")
                                .addContent(new Element("TipoRitenuta").setText("RT01"))
                                .addContent(new Element("ImportoRitenuta").setText("200.00"))
                                .addContent(new Element("AliquotaRitenuta").setText("20.00"))
                                .addContent(new Element("CausalePagamento").setText("A")))));
        document.setRootElement(fatturaElettronica);
        return document;
    }
}

