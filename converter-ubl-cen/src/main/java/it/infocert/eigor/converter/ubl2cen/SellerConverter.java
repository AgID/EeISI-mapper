package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0029_31_32(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BT0029SellerIdentifierAndSchemeIdentifier bt0029;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        if (invoice.getBG0004Seller().isEmpty()) {
            invoice.getBG0004Seller().add(new BG0004Seller());
        }

        Element accountingSupplierParty = findNamespaceChild(rootElement, namespacesInScope, "AccountingSupplierParty");

        if (accountingSupplierParty != null) {
            Element party = findNamespaceChild(accountingSupplierParty, namespacesInScope, "Party");

            if (party != null) {
            	List<Element> partyIdentifications = findNamespaceChildren(party, namespacesInScope, "PartyIdentification");
            	
            	for(Element elemParty : partyIdentifications) {

                	Element id = findNamespaceChild(elemParty, namespacesInScope, "ID");
                    if (id != null) {
                        Attribute schemeID = id.getAttribute("schemeID");
                        if(schemeID != null) {
                            bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(id.getAttributeValue("schemeID"), id.getText()));
                        } else {
                            bt0029 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(id.getText()));
                        }
                        invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt0029);
                    }
                }

                //BT0031-BT0032
                List<Element> partyTaxScheme = findNamespaceChildren(party, namespacesInScope, "PartyTaxScheme");
            	String idValue = null;
                BT0031SellerVatIdentifier bt0031;
                BT0032SellerTaxRegistrationIdentifier bt0032;
            	for (Element elemPartyTax : partyTaxScheme) {
                    Element taxScheme = findNamespaceChild(elemPartyTax, namespacesInScope, "TaxScheme");
                    if (taxScheme != null) {
                        Element id = findNamespaceChild(taxScheme, namespacesInScope, "ID");
                        if (id != null) {
                            idValue = id.getText();
                        }
                    }
                    Element companyID = findNamespaceChild(elemPartyTax, namespacesInScope, "CompanyID");
                    if (companyID != null && idValue != null) {
                        if (idValue.equals("VAT")) {
                            bt0031 = new BT0031SellerVatIdentifier(companyID.getText());
                            invoice.getBG0004Seller(0).getBT0031SellerVatIdentifier().add(bt0031);
                        } else {
                            bt0032 = new BT0032SellerTaxRegistrationIdentifier(companyID.getText());
                            invoice.getBG0004Seller(0).getBT0032SellerTaxRegistrationIdentifier().add(bt0032);
                        }
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBT0029_31_32(document, cenInvoice, errors);
    }
}