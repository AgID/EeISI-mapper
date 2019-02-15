package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PurchaseOrderReferenceConverter implements CustomMapping<Document> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
		// /Invoice/cac:OrderReference/cbc:ID
		Element root = document.getRootElement();

		final Element idElm = new Element("ID");
		final Element orderReferenceElm = new Element("OrderReference");
		final Element buyerReferenceElm = new Element("BuyerReference");



		String value;
		if (cenInvoice.getBT0013PurchaseOrderReference().isEmpty()) {
			if(cenInvoice.getBT0010BuyerReference().isEmpty()) {
				value = "NA"; 
				idElm.setText(value);
				orderReferenceElm.addContent(idElm);
				root.addContent(orderReferenceElm);
			}
			else{
				orderReferenceElm.addContent(cenInvoice.getBT0010BuyerReference(0).getValue());
				root.addContent(orderReferenceElm);			
			}
		}
//		else {
//			orderReferenceElm.addContent(cenInvoice.getBT0013PurchaseOrderReference(0).getValue());
//			root.addContent(orderReferenceElm);	
//			if(!cenInvoice.getBT0010BuyerReference().isEmpty()) {
//				buyerReferenceElm.addContent(cenInvoice.getBT0010BuyerReference(0).getValue());
//				root.addContent(buyerReferenceElm);	
//			}
//			
//		}
	}
}