package it.infocert.eigor.converter.common.cen2peppol;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.AllowanceChargeConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0099DocumentLevelChargeAmount;
import it.infocert.eigor.model.core.model.BT0100DocumentLevelChargeBaseAmount;
import it.infocert.eigor.model.core.model.BT0101DocumentLevelChargePercentage;
import it.infocert.eigor.model.core.model.BT0102DocumentLevelChargeVatCategoryCode;
import it.infocert.eigor.model.core.model.BT0104DocumentLevelChargeReason;
import it.infocert.eigor.model.core.model.BT0106SumOfInvoiceLineNetAmount;
import it.infocert.eigor.model.core.model.BT0109InvoiceTotalAmountWithoutVat;

public class AllowanceChargerConverterTest {

	
	  private BG0000Invoice invoice;

	    @Before
	    public void setUp() {
	        invoice = invoiceWithAmounts();
	    }

	    @Test
	    public void shouldMapBG0021DocumentLevelCharges() throws Exception {
	        AllowanceChargeConverter sut = new AllowanceChargeConverter();

	        Document doc = new Document(new Element("Invoice"));
	        enrichInvoiceWithBG0021();
	        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT, null);

	        Element allowanceCharge = doc.getRootElement().getChild("AllowanceCharge");
	        Element baseAmount = allowanceCharge.getChild("BaseAmount");
	        Element amount = allowanceCharge.getChild("Amount");
	        Element multiplierFactorNumeric = allowanceCharge.getChild("MultiplierFactorNumeric");

	        
	        System.out.println("amount:" + amount.getText() + "base:" + baseAmount.getText() + 
	        		"percent:" + multiplierFactorNumeric.getText());

	        assertFalse(allowanceCharge.getContent().isEmpty());

	        assertThat(amount.getText(), is("40.00"));

//	        assertThat(baseAmount.getText(), is("34.56"));

//	        assertThat(multiplierFactorNumeric.getText(), is("0.57"));

	        
	        
	     
	    }

	    private void enrichInvoiceWithBG0021() {
	        BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();

	        BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(BigDecimal.valueOf(40.00));
	        bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);

	        BT0100DocumentLevelChargeBaseAmount bt0100 = new BT0100DocumentLevelChargeBaseAmount(BigDecimal.valueOf(1000.00));
	        bg0021.getBT0100DocumentLevelChargeBaseAmount().add(bt0100);

	        BT0101DocumentLevelChargePercentage bt0101 = new BT0101DocumentLevelChargePercentage(BigDecimal.valueOf(0.04));
	        bg0021.getBT0101DocumentLevelChargePercentage().add(bt0101);

	        BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.E);
	        bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);

	        BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason("BT-104");
	        bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);

	        invoice.getBG0021DocumentLevelCharges().add(bg0021);
	    }
	    static BG0000Invoice invoiceWithAmounts() {
	        BG0000Invoice theInvoice;
	        theInvoice = new BG0000Invoice();
	        BG0022DocumentTotals totals = new BG0022DocumentTotals();
	        totals.getBT0106SumOfInvoiceLineNetAmount().add(new BT0106SumOfInvoiceLineNetAmount(new BigDecimal(100)));
	        totals.getBT0109InvoiceTotalAmountWithoutVat().add(new BT0109InvoiceTotalAmountWithoutVat(new BigDecimal(100)));
	        theInvoice.getBG0022DocumentTotals().add(totals);
	        theInvoice.getBT0005InvoiceCurrencyCode().add(new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));
	        return theInvoice;
	    }

}
