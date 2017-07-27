package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
                Lists.<IConversionIssue>newArrayList());
    }

    @Test
    public void shouldMapBG20() throws Exception {
        populateWithBG20();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        assertThat(dettaglioLineeList.size(), is(1));

        DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(0);

        List<AltriDatiGestionaliType> altriDatiGestionali = dettaglioLinee.getAltriDatiGestionali();
        assertThat(altriDatiGestionali.size(), is(1));
        assertEquals("21.0", altriDatiGestionali.get(0).getRiferimentoTesto());

        assertEquals(NaturaType.N_3, dettaglioLinee.getNatura());

        assertEquals(new BigDecimal(23.00).setScale(2, RoundingMode.HALF_UP), dettaglioLinee.getAliquotaIVA());

        assertEquals("Reason PR", dettaglioLinee.getDescrizione());
    }

    @Test
    public void shouldMapBG21() throws Exception {
        populateWithBG21();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);


        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        assertThat(dettaglioLineeList.size(), is(1));

        DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(0);

        List<AltriDatiGestionaliType> altriDatiGestionali = dettaglioLinee.getAltriDatiGestionali();
        assertThat(altriDatiGestionali.size(), is(1));
        assertEquals("20.00", altriDatiGestionali.get(0).getRiferimentoNumero().toString());

        assertEquals(NaturaType.N_3, dettaglioLinee.getNatura());

        assertEquals(new BigDecimal(23.00).setScale(2, RoundingMode.HALF_UP), dettaglioLinee.getAliquotaIVA());

        assertEquals("Reason TC01", dettaglioLinee.getDescrizione());
    }

    @Test
    public void shouldMapBG25() throws Exception {
        populateWithBG25();
        convert();
        FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
        List<DettaglioLineeType> dettaglioLineeList = body.getDatiBeniServizi().getDettaglioLinee();
        assertThat(dettaglioLineeList.size(), is(5));


    }

    private void convert() {
        new LineConverter().map(invoice, fatturaElettronica, Lists.<IConversionIssue>newArrayList());
    }

    private void populateWithBG20() {
        BG0020DocumentLevelAllowances allowances = new BG0020DocumentLevelAllowances();
        allowances.getBT0092DocumentLevelAllowanceAmount().add(new BT0092DocumentLevelAllowanceAmount(20.0));
        allowances.getBT0093DocumentLevelAllowanceBaseAmount().add(new BT0093DocumentLevelAllowanceBaseAmount(21.0));
        allowances.getBT0094DocumentLevelAllowancePercentage().add(new BT0094DocumentLevelAllowancePercentage(22.0));
        allowances.getBT0095DocumentLevelAllowanceVatCategoryCode().add(new BT0095DocumentLevelAllowanceVatCategoryCode(Untdid5305DutyTaxFeeCategories.Z));
        allowances.getBT0096DocumentLevelAllowanceVatRate().add(new BT0096DocumentLevelAllowanceVatRate(23.0));
        allowances.getBT0097DocumentLevelAllowanceReason().add(new BT0097DocumentLevelAllowanceReason("Reason"));
        allowances.getBT0098DocumentLevelAllowanceReasonCode().add(new BT0098DocumentLevelAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code42));

        invoice.getBG0020DocumentLevelAllowances().add(allowances);
    }

    private void populateWithBG21() {
        BG0021DocumentLevelCharges charges = new BG0021DocumentLevelCharges();
        charges.getBT0100DocumentLevelChargeBaseAmount().add(new BT0100DocumentLevelChargeBaseAmount(20.0));
        charges.getBT0102DocumentLevelChargeVatCategoryCode().add(new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.Z));
        charges.getBT0103DocumentLevelChargeVatRate().add(new BT0103DocumentLevelChargeVatRate(23.0));
        charges.getBT0104DocumentLevelChargeReason().add(new BT0104DocumentLevelChargeReason("Reason"));
        charges.getBT0105DocumentLevelChargeReasonCode().add(new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.ABK));

        invoice.getBG0021DocumentLevelCharges().add(charges);
    }

    private void populateWithBG25() {
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();

        populateBG25WithBG27(invoiceLine);
        populateBG25WithBG28(invoiceLine);
        populateBG25WithBG29(invoiceLine);
        populateBG25WithBG31(invoiceLine);
        invoice.getBG0025InvoiceLine().add(invoiceLine);
    }

    private void populateBG25WithBG27(BG0025InvoiceLine invoiceLine) {
        BG0027InvoiceLineAllowances invoiceLineAllowances = new BG0027InvoiceLineAllowances();
        invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().add(new BT0136InvoiceLineAllowanceAmount(20.0));
        invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount().add(new BT0137InvoiceLineAllowanceBaseAmount(21.0));
        invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage().add(new BT0138InvoiceLineAllowancePercentage(22.0));
        invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason().add(new BT0139InvoiceLineAllowanceReason("Reason"));
        invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode().add(new BT0140InvoiceLineAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code42));

        invoiceLine.getBG0027InvoiceLineAllowances().add(invoiceLineAllowances);

    }

    private void populateBG25WithBG28(BG0025InvoiceLine invoiceLine) {
        BG0028InvoiceLineCharges invoiceLineCharges = new BG0028InvoiceLineCharges();
        invoiceLineCharges.getBT0141InvoiceLineChargeAmount().add(new BT0141InvoiceLineChargeAmount(20.0));
        invoiceLineCharges.getBT0142InvoiceLineChargeBaseAmount().add(new BT0142InvoiceLineChargeBaseAmount(21.0));
        invoiceLineCharges.getBT0143InvoiceLineChargePercentage().add(new BT0143InvoiceLineChargePercentage(22.0));
        invoiceLineCharges.getBT0144InvoiceLineChargeReason().add(new BT0144InvoiceLineChargeReason("Reason"));
        invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().add(new BT0145InvoiceLineChargeReasonCode(Untdid7161SpecialServicesCodes.AA));

        invoiceLine.getBG0028InvoiceLineCharges().add(invoiceLineCharges);
    }

    private void populateBG25WithBG29(BG0025InvoiceLine invoiceLine) {
        BG0029PriceDetails priceDetails = new BG0029PriceDetails();
        priceDetails.getBT0146ItemNetPrice().add(new BT0146ItemNetPrice(20.0));
        priceDetails.getBT0147ItemPriceDiscount().add(new BT0147ItemPriceDiscount(21.0));
        priceDetails.getBT0148ItemGrossPrice().add(new BT0148ItemGrossPrice(22.0));
        priceDetails.getBT0149ItemPriceBaseQuantity().add(new BT0149ItemPriceBaseQuantity(1.0));
        priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(UnitOfMeasureCodes.EACH_EA));

        invoiceLine.getBG0029PriceDetails().add(priceDetails);
    }

    private void populateBG25WithBG31(BG0025InvoiceLine invoiceLine) {
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBT0153ItemName().add(new BT0153ItemName("Name"));

        invoiceLine.getBG0031ItemInformation().add(itemInformation);
    }

}