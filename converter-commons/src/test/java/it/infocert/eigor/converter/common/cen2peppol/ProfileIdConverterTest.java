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
import it.infocert.eigor.converter.commons.cen2peppol.ProfileIdConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class ProfileIdConverterTest {

	private BG0000Invoice invoice;
	private Document doc;
	private ProfileIdConverter profId;

	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		this.doc = new Document(new Element("Invoice"));
		profId = new ProfileIdConverter();

	}

	@Test
	public void shouldMapCstomizationID() {
		profId.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		String name = doc.getRootElement().getChild("ProfileID").getValue();
		assertEquals(name, "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");


	}
}


