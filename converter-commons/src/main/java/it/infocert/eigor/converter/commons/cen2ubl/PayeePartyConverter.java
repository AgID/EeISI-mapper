package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0010Payee;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class PayeePartyConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document target, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {


        // Generates element at /*[name()='Invoice']/cac:PayeeParty
        final Element root = target.getRootElement();

        BG0010Payee bg10 = evalExpression(() -> invoice.getBG0010Payee(0));

        if(bg10!=null) {
            String bt59 = evalExpression(() -> bg10.getBT0059PayeeName(0).getValue());
            Identifier bt60 = evalExpression(() -> bg10.getBT0060PayeeIdentifierAndSchemeIdentifier(0).getValue());
            Identifier bt61 = evalExpression(() -> bg10.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue());

            Element payeeParty = new Element("PayeeParty");

            if(bt60!=null) {

                Element id = new Element("ID")
                        .addContent(bt60.getIdentifier());
                if(bt60.getIdentificationSchema()!=null) {
                    id.setAttribute("schemeID", bt60.getIdentificationSchema());
                }

                payeeParty.addContent(
                        new Element("PartyIdentification")
                                .addContent(id)
                );
            }

            if(bt59!=null) {
                payeeParty.addContent(
                    new Element("PartyName")
                        .addContent(
                                new Element("Name")
                                    .addContent(bt59)
                        )
                );
            }

            if(bt61!=null) {

                Element companyId = new Element("CompanyID")
                        .addContent(bt61.getIdentifier());

                if(bt61.getIdentificationSchema()!=null) {
                    companyId.setAttribute("schemeID", bt61.getIdentificationSchema());
                }

                payeeParty.addContent(
                        new Element("PartyLegalEntity")
                            .addContent(companyId)
                );
            }

            if(!payeeParty.getChildren().isEmpty()){
                root.addContent(payeeParty);
            }
        }

    }
}
