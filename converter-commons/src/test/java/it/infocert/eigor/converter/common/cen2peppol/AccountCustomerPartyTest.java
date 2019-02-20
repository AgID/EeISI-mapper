package it.infocert.eigor.converter.common.cen2peppol;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.AccountCustomerParty;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0007Buyer;


public class AccountCustomerPartyTest {

	private BG0000Invoice invoice;
	private Document doc;
	private AccountCustomerParty sut;
	
	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		BG0007Buyer bg04 = new BG0007Buyer();

		invoice.getBG0007Buyer().add(bg04);
		this.doc = new Document(new Element("Invoice"));
		this.sut =  new AccountCustomerParty();
	}
	
	@Test
	public void shouldMapDefaultMapping() {
		 sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 Element name = doc.getRootElement().getChild("AccountingCustomerParty");
		 Element party = name.getChild("Party");
		 Element endPoint = party.getChild("EndpointID");
		 String schemeId = endPoint.getAttribute("schemeID").getValue();
		 assertEquals("NA", endPoint.getText());
		 assertEquals("0130", schemeId);
		 
	}

}