package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.org.springframework.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class ProfileIdConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        final Element root = document.getRootElement();

        String profileID = bt23ToProfileID(bt23(cenInvoice));
        if(profileID!=null)
            root.addContent(new Element("ProfileID").setText(profileID));

    }

    private String bt23ToProfileID(String bt23) {
        String profileID = null;
        if (!StringUtils.isEmpty(bt23)) {
            profileID = bt23;
        }
        return profileID;
    }

    private String bt23(BG0000Invoice cenInvoice) {
        try {
            return cenInvoice.getBG0002ProcessControl(0).getBT0023BusinessProcessType(0).getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return "";
        }
    }

}
