package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0029(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BT0029SellerIdentifierAndSchemeIdentifier bt0029 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> ids = null;
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
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBT0029(document, cenInvoice, errors);
    }
}