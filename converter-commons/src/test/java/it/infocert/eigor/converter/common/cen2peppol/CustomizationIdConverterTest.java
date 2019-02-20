package it.infocert.eigor.converter.common.cen2peppol;

import static org.junit.Assert.assertEquals;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.CustomizationIdConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class CustomizationIdConverterTest {

	private BG0000Invoice invoice;
	private Document doc;
	private CustomizationIdConverter cusId;

	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		this.doc = new Document(new Element("Invoice"));
		cusId = new CustomizationIdConverter();

	}

	@Test
	public void shouldMapCstomizationID() {
		 cusId.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 String name = doc.getRootElement().getChild("CustomizationID").getValue();
		 assertEquals(name, "urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
		 

	}
}
