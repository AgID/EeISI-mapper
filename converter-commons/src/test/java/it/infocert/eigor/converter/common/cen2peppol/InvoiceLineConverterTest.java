package it.infocert.eigor.converter.common.cen2peppol;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.CustomizationIdConverter;
import it.infocert.eigor.converter.commons.cen2peppol.InvoiceLineBaseQuantityConverter;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0029PriceDetails;
import it.infocert.eigor.model.core.model.BT0130InvoicedQuantityUnitOfMeasureCode;
import it.infocert.eigor.model.core.model.BT0150ItemPriceBaseQuantityUnitOfMeasureCode;

public class InvoiceLineConverterTest {
	
	private BG0000Invoice invoice;
	private Document doc;
	private InvoiceLineBaseQuantityConverter sut;
	
	@Before
	public void setUp() {
		invoice = new BG0000Invoice();
		this.doc = new Document(new Element("Invoice"));
		sut = new InvoiceLineBaseQuantityConverter();
		BG0029PriceDetails bg29 = new BG0029PriceDetails();
		bg29.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(UnitOfMeasureCodes.DOLLAR_PER_HOUR_D67));		
		
		BG0025InvoiceLine bg25 = new BG0025InvoiceLine();
		bg25.getBT0130InvoicedQuantityUnitOfMeasureCode().add(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.DOLLAR_PER_HOUR_D67));
		bg25.getBG0029PriceDetails().add(bg29);
		
		invoice.getBG0025InvoiceLine().add(bg25);
	}
	
	@Test
	public void shouldMapBaseQuantityUnitCode() { 
		 sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
		 Element incoiceLine = doc.getRootElement().getChild("InvoiceLine");
		 Element price = incoiceLine.getChild("Price");
		 Element baseQuantity = price.getChild("BaseQuantity");		 
		 String unit = baseQuantity.getAttribute("unitCode").getValue();
		 System.out.println(unit); 
	}

}