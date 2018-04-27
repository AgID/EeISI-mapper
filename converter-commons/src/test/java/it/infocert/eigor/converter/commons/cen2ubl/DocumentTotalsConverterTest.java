package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Marco Basilico on 28/09/2017.
 */
public class DocumentTotalsConverterTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        BG0022DocumentTotals totals = new BG0022DocumentTotals();
        totals.getBT0106SumOfInvoiceLineNetAmount().add(new BT0106SumOfInvoiceLineNetAmount(BigDecimal.valueOf(100)));
        totals.getBT0109InvoiceTotalAmountWithoutVat().add(new BT0109InvoiceTotalAmountWithoutVat(BigDecimal.valueOf(100)));
        invoice.getBG0022DocumentTotals().add(totals);
        invoice.getBT0005InvoiceCurrencyCode().add(new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));
    }

    @Test
    public void shouldAddAttributeToAllChildren() throws Exception {
        DocumentTotalsConverter sut = new DocumentTotalsConverter();
        Document doc = new Document(new Element("Invoice"));
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element amount = doc.getRootElement().getChild("LegalMonetaryTotal");

        for (Element child : amount.getChildren()) {
            assertTrue(child.hasAttributes());
            assertEquals("EUR", child.getAttributeValue("currencyID"));
        }

    }

    @Test
    public void shouldMapBG0021DocumentLevelCharges() throws Exception {
        DocumentTotalsConverter sut = new DocumentTotalsConverter();
        Document doc = new Document(new Element("Invoice"));
        enrichInvoiceWithBG0021();
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element allowanceCharge = doc.getRootElement().getChild("AllowanceCharge");

        assertFalse(allowanceCharge.getContent().isEmpty());

        Element amount = allowanceCharge.getChild("Amount");
        assertThat(amount.getText(), is("12.34"));

        Element baseAmount = allowanceCharge.getChild("BaseAmount");
        assertThat(baseAmount.getText(), is("34.56"));

        Element multiplierFactorNumeric = allowanceCharge.getChild("MultiplierFactorNumeric");
        assertThat(multiplierFactorNumeric.getText(), is("56.78"));

        Element allowanceChargeReason = allowanceCharge.getChild("AllowanceChargeReason");
        assertThat(allowanceChargeReason.getText(), is("BT-104"));

    }


    private void enrichInvoiceWithBG0021() {
        BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();

        BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(BigDecimal.valueOf(12.34));
        bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);

        BT0100DocumentLevelChargeBaseAmount bt0100 = new BT0100DocumentLevelChargeBaseAmount(BigDecimal.valueOf(34.56));
        bg0021.getBT0100DocumentLevelChargeBaseAmount().add(bt0100);

        BT0101DocumentLevelChargePercentage bt0101 = new BT0101DocumentLevelChargePercentage(BigDecimal.valueOf(56.78));
        bg0021.getBT0101DocumentLevelChargePercentage().add(bt0101);

        BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.E);
        bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);

        BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason("BT-104");
        bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);

        invoice.getBG0021DocumentLevelCharges().add(bg0021);
    }
}