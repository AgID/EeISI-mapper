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

        String customizationId = eigorConfiguration.getMandatoryString("eigor.converter.cen-ubl.customization-id");

        String profileID = bt24ToProfileID(eigorConfiguration, bt24(cenInvoice));

        root.addContent(0, new Element("CustomizationID").setText(customizationId));
        root.addContent(1, new Element("ProfileID").setText(profileID));

    }

    private String bt24ToProfileID(EigorConfiguration eigorConfiguration, String bt24) {
        String profileID = null;
        if(StringUtils.isEmpty(bt24)) {
            profileID = eigorConfiguration.getMandatoryString("eigor.converter.cen-ubl.profile-id");
        }else{
            profileID = bt24;
        }
        return profileID;
    }

    private String bt24(BG0000Invoice cenInvoice) {
        try {
            return cenInvoice.getBG0002ProcessControl(0).getBT0024SpecificationIdentifier(0).getValue();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return null;
        }
    }

}
