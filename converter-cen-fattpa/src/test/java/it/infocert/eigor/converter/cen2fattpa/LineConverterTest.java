package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2fattpa.models.AltriDatiGestionaliType;
import it.infocert.eigor.converter.cen2fattpa.models.CodiceArticoloType;
import it.infocert.eigor.converter.cen2fattpa.models.DatiBeniServiziType;
import it.infocert.eigor.converter.cen2fattpa.models.DatiDocumentiCorrelatiType;
import it.infocert.eigor.converter.cen2fattpa.models.DettaglioLineeType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.converter.cen2fattpa.models.NaturaType;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0013DeliveryInformation;
import it.infocert.eigor.model.core.model.BG0014InvoicingPeriod;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0026InvoiceLinePeriod;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;
import it.infocert.eigor.model.core.model.BG0028InvoiceLineCharges;
import it.infocert.eigor.model.core.model.BG0029PriceDetails;
import it.infocert.eigor.model.core.model.BG0030LineVatInformation;
import it.infocert.eigor.model.core.model.BG0031ItemInformation;
import it.infocert.eigor.model.core.model.BT0073InvoicingPeriodStartDate;
import it.infocert.eigor.model.core.model.BT0074InvoicingPeriodEndDate;
import it.infocert.eigor.model.core.model.BT0092DocumentLevelAllowanceAmount;
import it.infocert.eigor.model.core.model.BT0093DocumentLevelAllowanceBaseAmount;
import it.infocert.eigor.model.core.model.BT0094DocumentLevelAllowancePercentage;
import it.infocert.eigor.model.core.model.BT0095DocumentLevelAllowanceVatCategoryCode;
import it.infocert.eigor.model.core.model.BT0096DocumentLevelAllowanceVatRate;
import it.infocert.eigor.model.core.model.BT0097DocumentLevelAllowanceReason;
import it.infocert.eigor.model.core.model.BT0098DocumentLevelAllowanceReasonCode;
import it.infocert.eigor.model.core.model.BT0100DocumentLevelChargeBaseAmount;
import it.infocert.eigor.model.core.model.BT0102DocumentLevelChargeVatCategoryCode;
import it.infocert.eigor.model.core.model.BT0103DocumentLevelChargeVatRate;
import it.infocert.eigor.model.core.model.BT0104DocumentLevelChargeReason;
import it.infocert.eigor.model.core.model.BT0105DocumentLevelChargeReasonCode;
import it.infocert.eigor.model.core.model.BT0127InvoiceLineNote;
import it.infocert.eigor.model.core.model.BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0129InvoicedQuantity;
import it.infocert.eigor.model.core.model.BT0132ReferencedPurchaseOrderLineReference;
import it.infocert.eigor.model.core.model.BT0133InvoiceLineBuyerAccountingReference;
import it.infocert.eigor.model.core.model.BT0134InvoiceLinePeriodStartDate;
import it.infocert.eigor.model.core.model.BT0135InvoiceLinePeriodEndDate;
import it.infocert.eigor.model.core.model.BT0136InvoiceLineAllowanceAmount;
import it.infocert.eigor.model.core.model.BT0137InvoiceLineAllowanceBaseAmount;
import it.infocert.eigor.model.core.model.BT0138InvoiceLineAllowancePercentage;
import it.infocert.eigor.model.core.model.BT0139InvoiceLineAllowanceReason;
import it.infocert.eigor.model.core.model.BT0140InvoiceLineAllowanceReasonCode;
import it.infocert.eigor.model.core.model.BT0141InvoiceLineChargeAmount;
import it.infocert.eigor.model.core.model.BT0142InvoiceLineChargeBaseAmount;
import it.infocert.eigor.model.core.model.BT0143InvoiceLineChargePercentage;
import it.infocert.eigor.model.core.model.BT0144InvoiceLineChargeReason;
import it.infocert.eigor.model.core.model.BT0145InvoiceLineChargeReasonCode;
import it.infocert.eigor.model.core.model.BT0146ItemNetPrice;
import it.infocert.eigor.model.core.model.BT0147ItemPriceDiscount;
import it.infocert.eigor.model.core.model.BT0148ItemGrossPrice;
import it.infocert.eigor.model.core.model.BT0149ItemPriceBaseQuantity;
import it.infocert.eigor.model.core.model.BT0150ItemPriceBaseQuantityUnitOfMeasureCode;
import it.infocert.eigor.model.core.model.BT0151InvoicedItemVatCategoryCode;
import it.infocert.eigor.model.core.model.BT0152InvoicedItemVatRate;
import it.infocert.eigor.model.core.model.BT0153ItemName;
import it.infocert.eigor.model.core.model.BT0154ItemDescription;
import it.infocert.eigor.model.core.model.BT0157ItemStandardIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class LineConverterTest {

    private BG0000Invoice invoice;
    private FatturaElettronicaType fatturaElettronica;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        fatturaElettronica = new FatturaElettronicaType();
        FatturaElettronicaBodyType b = new FatturaElettronicaBodyType();
        b.setDatiBeniServizi(new DatiBeniServiziType());
        fatturaElettronica.getFatturaElettronicaBody().add(b);
    }

    @Test
    public void shouldConvertWithError() throws Exception {
        new LineConverter().map(
                invoice,
                fatturaElettronica,
                Lists.<IConversionIssue>newArrayList(),
                ErrorCode.Location.FATTPA_OUT,
                null);

        if (Math.abs(Math.PI - 1) < Math.random()) {

        }
    }

    @Test
    public void shouldMapBt73And74() throws Exception {
        final long now = System.currentTimeMillis();
        final long after = System.currentTimeMillis() + 1000;

        populateWithDates(now, after);
        populateWithBG20();
        populateWithBG25();
        XMLGregorianCalendar nowXml = setupCalendar(now);
        XMLGregorianCalendar afterXml = setupCalendar(after);
        convert();

        List<DettaglioLineeType> dettaglioLinee = fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDettaglioLinee();

        assertFalse(dettaglioLinee.isEmpty());

        for (DettaglioLineeType linea : dettaglioLinee) {
            assertEquals(nowXml, linea.getDataInizioPeriodo());
            assertEquals(afterXml, linea.getDataFinePeriodo());
        }
    }

    @Test
    public void shouldMapBt134AndBt135EvenIfBt73And74ArePresent() throws Exception {
        final long now = System.currentTimeMillis();
        final long after = System.currentTimeMillis() + 1000;

        populateWithDates(now, after);
        populateWithBG20();
        populateWithBG25();

        final long bg26start = 1514764800000l;
        final long bg26end = 1515974400000l;
        setupBG26(bg26start, bg26end);

        convert();

        List<DettaglioLineeType> dettaglioLinee = fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType linea = dettaglioLinee.get(i);

            assertThat(linea.getDataInizioPeriodo().getYear(), is(2018));
            assertThat(linea.getDataInizioPeriodo().getMonth(), is(1));
            assertThat(linea.getDataInizioPeriodo().getDay(), is(1));

            assertThat(linea.getDataFinePeriodo().getYear(), is(2018));
            assertThat(linea.getDataFinePeriodo().getMonth(), is(1));
            assertThat(linea.getDataFinePeriodo().getDay(), is(15));
        }
    }

    private void setupBG26(final long start, final long end) {
        List<BG0025InvoiceLine> invoiceLines = invoice.getBG0025InvoiceLine();
        for (BG0025InvoiceLine invoiceLine : invoiceLines) {
            BG0026InvoiceLinePeriod period = new BG0026InvoiceLinePeriod();
            period.getBT0134InvoiceLinePeriodStartDate().add(new BT0134InvoiceLinePeriodStartDate(new LocalDate(start)));
            period.getBT0135InvoiceLinePeriodEndDate().add(new BT0135InvoiceLinePeriodEndDate(new LocalDate(end)));
            invoiceLine.getBG0026InvoiceLinePeriod().add(period);
        }
    }

    private XMLGregorianCalendar setupCalendar(final long time) throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(time);

        XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        xmlCalendar.setHour(0);
        xmlCalendar.setMinute(0);
        xmlCalendar.setSecond(0);
        xmlCalendar.setMillisecond(0);

        return xmlCalendar;
    }


    private void populateWithDates(final long now, final long after) {
        BG0014InvoicingPeriod invoicingPeriod = new BG0014InvoicingPeriod();
        invoicingPeriod.getBT0073InvoicingPeriodStartDate().add(new BT0073InvoicingPeriodStartDate(new LocalDate(now)));
        invoicingPeriod.getBT0074InvoicingPeriodEndDate().add(new BT0074InvoicingPeriodEndDate(new LocalDate(after)));

        BG0013DeliveryInformation deliveryInformation = new BG0013DeliveryInformation();
        deliveryInformation.getBG0014InvoicingPeriod().add(invoicingPeriod);

        invoice.getBG0013DeliveryInformation().add(deliveryInformation);
    }

    @Test
    public void shouldMapBG20() throws Exception {
        populateWithBG20();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        assertThat(dettaglioLineeList.size(), is(2));

        DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(0);

        List<AltriDatiGestionaliType> altriDatiGestionali = dettaglioLinee.getAltriDatiGestionali();
        assertThat(altriDatiGestionali.size(), is(0));

        assertEquals(NaturaType.N_3, dettaglioLinee.getNatura());

        assertEquals(new BigDecimal(23.00).setScale(2, RoundingMode.HALF_UP), dettaglioLinee.getAliquotaIVA());

        assertEquals("Reason BT-98=PR", dettaglioLinee.getDescrizione());
    }

    @Test
    public void shouldMapBG21() {
        populateWithBG21();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);


        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        assertThat(dettaglioLineeList.size(), is(2));

        DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(0);

        List<AltriDatiGestionaliType> altriDatiGestionali = dettaglioLinee.getAltriDatiGestionali();
        assertThat(altriDatiGestionali.size(), is(0));

        assertEquals(NaturaType.N_3, dettaglioLinee.getNatura());

        assertEquals(new BigDecimal(23.00).setScale(2, RoundingMode.HALF_UP), dettaglioLinee.getAliquotaIVA());

        assertEquals("Reason SC", dettaglioLinee.getDescrizione());
    }

    @Test
    public void shouldMapBG25() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        assertThat(dettaglioLineeList.size(), is(15));
    }

    @Test
    public void shouldMapBT153AndBT154MergedInDescrizione() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            assertThat(dettaglioLinee.getDescrizione(), is("Name Description"));
        }
    }

    @Test
    public void shouldMapBT132InDatiOrdineAcquisto() {
        populateWithBG25();
        convert();
        final List<DatiDocumentiCorrelatiType> datiOrdineAcquisto = fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiOrdineAcquisto();
        assertFalse(datiOrdineAcquisto.isEmpty());
        for (int i = 0; i < 5; i++) {
            final DatiDocumentiCorrelatiType dati = datiOrdineAcquisto.get(i);
            final String numItem = dati.getNumItem();
            final List<Integer> riferimentoNumeroLinea = dati.getRiferimentoNumeroLinea();
            assertEquals("Test Reference", numItem);
            assertFalse(riferimentoNumeroLinea.isEmpty());
        }
    }

    @Test
    public void shouldMapBT133InLineeRiferimentoAmministrazione() {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            String sut = dettaglioLinee.getRiferimentoAmministrazione();
            assertThat(sut, is("BT-133"));
        }
    }

    @Test
    public void shouldMapBT127InvoiceNote() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            AltriDatiGestionaliType altriDatiGestionali = dettaglioLinee.getAltriDatiGestionali().get(0);

            assertThat(altriDatiGestionali.getTipoDato(), is("BT-127"));
            assertThat(altriDatiGestionali.getRiferimentoTesto(), is("TestNote"));
        }
    }

    @Test
    public void shouldMapBT128InvoiceLineIdentifier() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            CodiceArticoloType codiceArticolo = dettaglioLinee.getCodiceArticolo().get(0);

            assertThat(codiceArticolo.getCodiceValore(), is("BT-128"));
            assertThat(codiceArticolo.getCodiceTipo(), is("BT-128-1"));
        }
    }

    @Test
    public void shouldMapBG30() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            assertThat(dettaglioLinee.getAliquotaIVA().toString(), is("0.00"));
            assertEquals(NaturaType.N_4, dettaglioLinee.getNatura());
        }
    }

    @Test
    public void shouldMapBG25Quantity() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            BigDecimal quantita = dettaglioLinee.getQuantita();
            BigDecimal expected = new BigDecimal(2).setScale(8, RoundingMode.HALF_UP);
            assertThat(quantita, is(expected));
        }
    }

    @Test
    public void shouldMapBT157WithSchemeIdentifier() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            CodiceArticoloType codiceArticolo = dettaglioLinee.getCodiceArticolo().get(1);
            assertThat(codiceArticolo.getCodiceValore(), is("BT-157"));
            assertThat(codiceArticolo.getCodiceTipo(), is("BT-157-1"));
        }
    }

    @Test
    public void shouldMapBT158SchemeIdentifierAndVersionMergedInCodiceTipo() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();

        for (int i = 0; i < 5; i++) {
            DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
            String codiceTipo = dettaglioLinee.getCodiceArticolo().get(2).getCodiceTipo();
            assertThat(codiceTipo, is("BT-158-1 BT-158-2"));
        }
    }

    @Test
    public void shouldMapBT128WithDefault() {
        Predicate<CodiceArticoloType> defaultCodiceArticolo = ca -> "ZZZ".equals(ca.getCodiceTipo()) || "ZZZ".equals(ca.getCodiceValore());
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);

        boolean match = body.getDatiBeniServizi().getDettaglioLinee()
                .stream()
                .map(DettaglioLineeType::getCodiceArticolo)
                .flatMap(List::stream)
                .anyMatch(defaultCodiceArticolo.negate());
        assertTrue(match);

        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier("", ""));
        invoice.getBG0025InvoiceLine().get(0).getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().clear();
        invoice.getBG0025InvoiceLine().get(0).getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt128);
        convert();
        body = fatturaElettronica.getFatturaElettronicaBody().get(0);

        match = body.getDatiBeniServizi().getDettaglioLinee()
                .stream()
                .map(DettaglioLineeType::getCodiceArticolo)
                .flatMap(List::stream)
                .anyMatch(defaultCodiceArticolo);
        assertTrue(match);
    }

    private void convert() {
        new LineConverter().map(invoice, fatturaElettronica, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT, null);
    }

    private void populateWithBG20() {
        BG0020DocumentLevelAllowances allowances = new BG0020DocumentLevelAllowances();
        allowances.getBT0092DocumentLevelAllowanceAmount().add(new BT0092DocumentLevelAllowanceAmount(new BigDecimal(20.0)));
        allowances.getBT0093DocumentLevelAllowanceBaseAmount().add(new BT0093DocumentLevelAllowanceBaseAmount(new BigDecimal(21.0)));
        allowances.getBT0094DocumentLevelAllowancePercentage().add(new BT0094DocumentLevelAllowancePercentage(new BigDecimal(22.0)));
        allowances.getBT0095DocumentLevelAllowanceVatCategoryCode().add(new BT0095DocumentLevelAllowanceVatCategoryCode(Untdid5305DutyTaxFeeCategories.Z));
        allowances.getBT0096DocumentLevelAllowanceVatRate().add(new BT0096DocumentLevelAllowanceVatRate(new BigDecimal(23.0)));
        allowances.getBT0097DocumentLevelAllowanceReason().add(new BT0097DocumentLevelAllowanceReason("Reason"));
        allowances.getBT0098DocumentLevelAllowanceReasonCode().add(new BT0098DocumentLevelAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code42));

        invoice.getBG0020DocumentLevelAllowances().add(allowances);
    }

    private void populateWithBG21() {
        BG0021DocumentLevelCharges charges = new BG0021DocumentLevelCharges();
        charges.getBT0100DocumentLevelChargeBaseAmount().add(new BT0100DocumentLevelChargeBaseAmount(new BigDecimal(20.0)));
        charges.getBT0102DocumentLevelChargeVatCategoryCode().add(new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.Z));
        charges.getBT0103DocumentLevelChargeVatRate().add(new BT0103DocumentLevelChargeVatRate(new BigDecimal(23.0)));
        charges.getBT0104DocumentLevelChargeReason().add(new BT0104DocumentLevelChargeReason("Reason"));
        charges.getBT0105DocumentLevelChargeReasonCode().add(new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.ABK));

        invoice.getBG0021DocumentLevelCharges().add(charges);
    }

    private void populateWithBG25() {
        for (int i = 0; i < 5; i++) {

            BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();

            populateBG25WithBG27(invoiceLine);
            populateBG25WithBG28(invoiceLine);
            populateBG25WithBG29(invoiceLine);
            populateBG25WithBG30(invoiceLine);
            populateBG25WithBG31(invoiceLine);
            populateBG25WithBT127(invoiceLine);
            populateBG25WithBT128(invoiceLine);
            populateBG25WithBT129(invoiceLine);
            populateBG25WithBT132(invoiceLine);
            populateBG25WithBT133(invoiceLine);

            invoice.getBG0025InvoiceLine().add(invoiceLine);
        }
    }

    private void populateBG25WithBT132(BG0025InvoiceLine invoiceLine) {
        final BT0132ReferencedPurchaseOrderLineReference bt132 = new BT0132ReferencedPurchaseOrderLineReference("Test Reference");
        invoiceLine.getBT0132ReferencedPurchaseOrderLineReference().add(bt132);
    }

    private void populateBG25WithBT133(BG0025InvoiceLine invoiceLine) {
        final BT0133InvoiceLineBuyerAccountingReference bt0133 = new BT0133InvoiceLineBuyerAccountingReference("BT-133");
        invoiceLine.getBT0133InvoiceLineBuyerAccountingReference().add(bt0133);
    }

    private void populateBG25WithBG27(BG0025InvoiceLine invoiceLine) {
        BG0027InvoiceLineAllowances invoiceLineAllowances = new BG0027InvoiceLineAllowances();
        invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().add(new BT0136InvoiceLineAllowanceAmount(new BigDecimal(20.0)));
        invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount().add(new BT0137InvoiceLineAllowanceBaseAmount(new Identifier("EUR", "EUR:21.0")));
        invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage().add(new BT0138InvoiceLineAllowancePercentage(new Identifier("EUR", "22.0")));
        invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason().add(new BT0139InvoiceLineAllowanceReason("Reason"));
        invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode().add(new BT0140InvoiceLineAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code42));

        invoiceLine.getBG0027InvoiceLineAllowances().add(invoiceLineAllowances);

    }

    private void populateBG25WithBG28(BG0025InvoiceLine invoiceLine) {
        BG0028InvoiceLineCharges invoiceLineCharges = new BG0028InvoiceLineCharges();
        invoiceLineCharges.getBT0141InvoiceLineChargeAmount().add(new BT0141InvoiceLineChargeAmount(new BigDecimal(20.0)));
        invoiceLineCharges.getBT0142InvoiceLineChargeBaseAmount().add(new BT0142InvoiceLineChargeBaseAmount(new BigDecimal(21.0)));
        invoiceLineCharges.getBT0143InvoiceLineChargePercentage().add(new BT0143InvoiceLineChargePercentage(new BigDecimal(22.0)));
        invoiceLineCharges.getBT0144InvoiceLineChargeReason().add(new BT0144InvoiceLineChargeReason("Reason"));
        invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().add(new BT0145InvoiceLineChargeReasonCode(Untdid7161SpecialServicesCodes.AA));

        invoiceLine.getBG0028InvoiceLineCharges().add(invoiceLineCharges);
    }

    private void populateBG25WithBG29(BG0025InvoiceLine invoiceLine) {
        BG0029PriceDetails priceDetails = new BG0029PriceDetails();
        priceDetails.getBT0146ItemNetPrice().add(new BT0146ItemNetPrice(new BigDecimal(20.0)));
        priceDetails.getBT0147ItemPriceDiscount().add(new BT0147ItemPriceDiscount(new BigDecimal(21.0)));
        priceDetails.getBT0148ItemGrossPrice().add(new BT0148ItemGrossPrice(new BigDecimal(22.0)));
        priceDetails.getBT0149ItemPriceBaseQuantity().add(new BT0149ItemPriceBaseQuantity(BigDecimal.ONE));
        priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(UnitOfMeasureCodes.EACH_EA));

        invoiceLine.getBG0029PriceDetails().add(priceDetails);
    }

    private void populateBG25WithBG30(BG0025InvoiceLine invoiceLine) {
        BG0030LineVatInformation lineVatInformation = new BG0030LineVatInformation();
        lineVatInformation.getBT0151InvoicedItemVatCategoryCode().add(new BT0151InvoicedItemVatCategoryCode(Untdid5305DutyTaxFeeCategories.E));
        lineVatInformation.getBT0152InvoicedItemVatRate().add(new BT0152InvoicedItemVatRate(BigDecimal.ZERO));

        invoiceLine.getBG0030LineVatInformation().add(lineVatInformation);
    }

    private void populateBG25WithBG31(BG0025InvoiceLine invoiceLine) {
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBT0153ItemName().add(new BT0153ItemName("Name"));
        itemInformation.getBT0154ItemDescription().add(new BT0154ItemDescription("Description"));
        BT0157ItemStandardIdentifierAndSchemeIdentifier bt157 = new BT0157ItemStandardIdentifierAndSchemeIdentifier(new Identifier("BT-157-1", "BT-157"));
        itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier().add(bt157);
        BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier bt158 =
                new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(
                        new Identifier("BT-158-1", "BT-158-2", "BT158"));
        itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().add(bt158);
        invoiceLine.getBG0031ItemInformation().add(itemInformation);
    }

    private void populateBG25WithBT127(BG0025InvoiceLine invoiceLine) {
        BT0127InvoiceLineNote bt0127 = new BT0127InvoiceLineNote("TestNote");
        invoiceLine.getBT0127InvoiceLineNote().add(bt0127);
    }

    private void populateBG25WithBT128(BG0025InvoiceLine invoiceLine) {
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 =
                new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(
                        new Identifier("BT-128-1", "BT-128"));
        invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);
    }

    private void populateBG25WithBT129(BG0025InvoiceLine invoiceLine) {
        BT0129InvoicedQuantity bt0129 = new BT0129InvoicedQuantity(new BigDecimal(2));
        invoiceLine.getBT0129InvoicedQuantity().add(bt0129);
    }

}
