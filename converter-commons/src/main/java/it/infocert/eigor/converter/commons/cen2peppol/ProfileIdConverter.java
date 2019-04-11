package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class ProfileIdConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
                    EigorConfiguration eigorConfiguration) {

        final Element root = document.getRootElement();
        Element profileId = root.getChild("ProfileID");

        if (profileId == null) {
            profileId = new Element("ProfileID");
            root.addContent(profileId);
        }

        profileId.setText("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0");
    }
}
