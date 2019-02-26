package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2ubl.FirstLevelElementsConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PurchaseOrderReferenceConverter extends FirstLevelElementsConverter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
    public void customMap(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {


		if (cenInvoice.getBT0013PurchaseOrderReference().isEmpty() && 
				cenInvoice.getBT0010BuyerReference().isEmpty()) {
			    
				final Element orderReference = new Element("OrderReference");
	            final Element id = new Element("ID").setText("NA");
	            root.addContent(orderReference.setContent(id));

			
		}
		else if (!cenInvoice.getBT0013PurchaseOrderReference().isEmpty()) {
            
			final String value = cenInvoice.getBT0013PurchaseOrderReference(0).getValue();
            final Element orderReference = new Element("OrderReference");
            final Element id = new Element("ID").setText(value);
            root.addContent(orderReference.setContent(id));
        
		}
		else if(!cenInvoice.getBT0010BuyerReference().isEmpty()) {
            
			final String value = cenInvoice.getBT0010BuyerReference(0).getValue();
            final Element buyerReference = new Element("BuyerReference");
            final Element id = new Element("ID").setText(value);
            root.addContent(buyerReference.setContent(id));
        
		}
		
	}
}