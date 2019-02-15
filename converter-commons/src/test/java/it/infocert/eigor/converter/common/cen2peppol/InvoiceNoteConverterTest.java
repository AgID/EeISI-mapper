package it.infocert.eigor.converter.common.cen2peppol;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import it.infocert.eigor.converter.commons.cen2peppol.InvoiceTypeCodeConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;

public class InvoiceNoteConverterTest {

	
	private BG0000Invoice invoice;
	private Document doc;
	
	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
//		BG0001InvoiceNote bg01 = new BG0001InvoiceNote();
//		BG0022DocumentTotals bg022 = new BG0022DocumentTotals();
		
//		invoice.getBT0003InvoiceTypeCode().add(bt03);
		this.doc = new Document(new Element("Invoice"));
	//	this.sut =  new InvoiceTypeCodeConverter();

	}
	
	@Test
	public void shouldMapDefault() { 
		
		
	}
}
