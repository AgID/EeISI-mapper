package it.infocert.eigor.converter.common.cen2peppol;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.AccountSupplierPartyConverter;
import it.infocert.eigor.converter.commons.cen2peppol.DateFormatConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import it.infocert.eigor.model.core.model.BT0007ValueAddedTaxPointDate;
import it.infocert.eigor.model.core.model.BT0009PaymentDueDate;

public class DateFormatConverterTest {

	
	private BG0000Invoice invoice;
	private Document doc;
	private DateFormatConverter sut;

	@Before
	public void setUp() {
		
		invoice = new BG0000Invoice();
		BT0002InvoiceIssueDate bt002 = new BT0002InvoiceIssueDate(new LocalDate(2004, 12, 25));
		BT0007ValueAddedTaxPointDate bt007 = new BT0007ValueAddedTaxPointDate(new LocalDate(2004, 12, 25));
		BT0009PaymentDueDate bt009 = new BT0009PaymentDueDate(new LocalDate(2004, 12, 25));
		
		invoice.getBT0002InvoiceIssueDate().add(bt002);
		invoice.getBT0007ValueAddedTaxPointDate().add(bt007);
		invoice.getBT0009PaymentDueDate().add(bt009);
		this.doc = new Document(new Element("Invoice"));
		this.sut =  new DateFormatConverter();
		
	}
	
	@Test
	public void ConvertDatetoString() { 
		 sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 String issueDate = doc.getRootElement().getChild("IssueDate").getValue();
		 String taxDate = doc.getRootElement().getChild("TaxPointDate").getValue();
		 
		 assertEquals("2004-12-25", issueDate);
		 assertEquals("2004-12-25", taxDate);


		
	}
	
	
}
