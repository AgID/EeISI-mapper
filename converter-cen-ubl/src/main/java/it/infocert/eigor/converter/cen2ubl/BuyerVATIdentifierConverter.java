package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuyerVATIdentifierConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(BuyerVATIdentifierConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        Element root = document.getRootElement();
        if (root != null) {
            BG0007Buyer bg0007 = cenInvoice.getBG0007Buyer(0);
            if (bg0007 != null) {
                BT0048BuyerVatIdentifier bt0048 = bg0007.getBT0048BuyerVatIdentifier(0);
                if (bt0048 != null) {
                    Element companyID = new Element("CompanyID");
                    Identifier identifier = bt0048.getValue();
                    if (identifier.getIdentificationSchema() != null) {
                        companyID.setAttribute("schemeID", identifier.getIdentificationSchema());
                    }
                    if (identifier.getIdentifier() != null) {
                        companyID.addContent(identifier.getIdentifier());
                    }
                    Element accountingCustomerParty = root.getChild("AccountingCustomerParty");
                    if (accountingCustomerParty == null) {
                        accountingCustomerParty = new Element("AccountingCustomerParty");
                        root.addContent(accountingCustomerParty);
                    }
                    Element party = accountingCustomerParty.getChild("Party");
                    if (party == null) {
                        party = new Element("Party");
                        accountingCustomerParty.addContent(party);
                    }
                    Element partyTaxScheme = party.getChild("PartyTaxScheme");
                    if (partyTaxScheme == null) {
                        partyTaxScheme = new Element("PartyTaxScheme");
                        party.addContent(partyTaxScheme);
                    }
                    partyTaxScheme.addContent(companyID);
                }
            }
        }
    }
}