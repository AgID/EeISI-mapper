package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class CustomizationIdConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub
		
		final Element root = document.getRootElement();
        Element customizationId = root.getChild("CustomizationID");
        String customizationID = "CustomizationID";
       
    
        if (customizationId == null) {
        	customizationId = new Element(customizationID);
        	customizationId.setText("urn:fdc:cen.eu:en16931-1:2017");
        	root.addContent(customizationId);
        } else {
        	customizationId.setText("urn:fdc:cen.eu:en16931-1:2017");
        	root.addContent(customizationId);
        }

		
	}

}
