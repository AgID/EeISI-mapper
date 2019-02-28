package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0130InvoicedQuantityUnitOfMeasureCode;
import it.infocert.eigor.model.core.model.BT0150ItemPriceBaseQuantityUnitOfMeasureCode;

public class InvoiceLineBaseQuantityConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		//TODO Auto-generated method stub

		Element root = document.getRootElement();
		UnitOfMeasureCodes bt130;
		UnitOfMeasureCodes bt150;
		final Element priceElm;
		Element baseQuantitiy;
		Element invoiceLine = root.getChild("InvoiceLine");
		Element unitCode;
		if(invoiceLine == null) { 
			invoiceLine = new Element("InvoiceLine");
			priceElm = new Element("Price");
		}else { 
		
			priceElm = invoiceLine.getChild("Price");

		}


		String baseQuantityunitCode = "";
		if(!cenInvoice.getBG0025InvoiceLine().isEmpty()) {
			bt130 = cenInvoice.getBG0025InvoiceLine(0).getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue();
			if(!cenInvoice.getBG0025InvoiceLine(0).getBG0029PriceDetails().isEmpty())
				if(!cenInvoice.getBG0025InvoiceLine(0).getBG0029PriceDetails(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
					bt150 = cenInvoice.getBG0025InvoiceLine(0).getBG0029PriceDetails(0).getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue();

					if(bt130 != bt150){
						baseQuantityunitCode = bt130.toString();
					}
					else
						baseQuantityunitCode = bt150.toString();

				}
			baseQuantitiy = priceElm.getChild("BaseQuantity");

			if(baseQuantitiy != null) { 
				baseQuantitiy.setAttribute("unitCode", baseQuantityunitCode);
			}else { 
				baseQuantitiy = new Element("BaseQuantity");
				baseQuantitiy.setAttribute("unitCode", baseQuantityunitCode);
			} 
			priceElm.addContent(baseQuantitiy);
			invoiceLine.addContent(priceElm);
			root.addContent(invoiceLine);

		}
		else return;
	}
}