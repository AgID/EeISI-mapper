package it.infocert.eigor.converter.commons.cen2ubl;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;

@Ignore
public class AccountSupplierPartyConverterTest {

	private BG0000Invoice invoice;
	private Document doc;
	private AccountSupplierPartyConverter sut;
	
	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		BG0004Seller bg04 = new BG0004Seller();

		invoice.getBG0004Seller().add(bg04);
		this.doc = new Document(new Element("Invoice"));
		this.sut =  new AccountSupplierPartyConverter();
	}
	
	@Test
	public void shouldMapDefaultMapping() {
		 sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 Element name = doc.getRootElement().getChild("AccountingSupplierParty").getChild("Party").getChild("EndpointID");
		 String schemeId = name.getAttribute("schemeID").getValue();
		 assertEquals("NA", name.getText());
		 assertEquals("0201", schemeId);
		 
	}

}
