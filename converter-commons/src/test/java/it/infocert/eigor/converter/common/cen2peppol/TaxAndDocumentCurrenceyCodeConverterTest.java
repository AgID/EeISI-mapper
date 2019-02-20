package it.infocert.eigor.converter.common.cen2peppol;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.AccountSupplierPartyConverter;
import it.infocert.eigor.converter.commons.cen2peppol.TaxAndDocumentCurrenceyCodeConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0005InvoiceCurrencyCode;
import it.infocert.eigor.model.core.model.BT0006VatAccountingCurrencyCode;

public class TaxAndDocumentCurrenceyCodeConverterTest {


	private BG0000Invoice invoice;
	private Document doc;
	TaxAndDocumentCurrenceyCodeConverter sut;

	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		BT0005InvoiceCurrencyCode bt05 = new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.INR);
		BT0006VatAccountingCurrencyCode bt06 = new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.AED);
		invoice.getBT0005InvoiceCurrencyCode().add(bt05);
		invoice.getBT0006VatAccountingCurrencyCode().add(bt06);
		this.doc = new Document(new Element("Invoice"));
		this.sut =  new TaxAndDocumentCurrenceyCodeConverter();

	}
	@Test
	public void shouldMapTaxAndDocumentCurrency() { 
		sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		Element documentCurrency = doc.getRootElement().getChild("DocumentCurrencyCode");
		Element taxCurrency = doc.getRootElement().getChild("TaxCurrencyCode");
		
		assertEquals("AED", documentCurrency.getText());
		assertEquals("INR", taxCurrency.getText());
		
	}
	
////	@Test
//	public void shouldMapDocumentCurrency() { 
//		sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
//		Element documentCurrency = doc.getRootElement().getChild("DocumentCurrencyCode");
//		Element taxCurrency = doc.getRootElement().getChild("TaxCurrencyCode");
//		
//		System.out.print(documentCurrency.getText());
//		System.out.print(taxCurrency.getText());
//
//	}


}
