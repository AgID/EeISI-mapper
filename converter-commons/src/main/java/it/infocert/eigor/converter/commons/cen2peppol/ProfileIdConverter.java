package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.model.BG0000Invoice;

public class ProfileIdConverter implements CustomMapping<Document>{

	@Override
	public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
			EigorConfiguration eigorConfiguration) {
		// TODO Auto-generated method stub
		
			final Element root = document.getRootElement();
	        Element profileId = root.getChild("ProfileID");
	        String ProfileID = "ProfileID";


	        if (profileId == null) {

	        	profileId = new Element(ProfileID);
	        	profileId.setText("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
	        	root.addContent(profileId);
	           
	        } else {
	        	profileId.setText("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
	        	root.addContent(profileId);
	        }

		
	}

}
