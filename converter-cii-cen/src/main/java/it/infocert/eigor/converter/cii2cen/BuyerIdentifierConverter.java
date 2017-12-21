package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0046BuyerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0048BuyerVatIdentifier;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Buyer Identifier Custom Converter
 */
public class BuyerIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0046(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        BT0046BuyerIdentifierAndSchemeIdentifier bt0046 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeAgreement");

            if (child1 != null) {
               Element buyerTradeParty = findNamespaceChild(child1, namespacesInScope, "BuyerTradeParty");

                if (buyerTradeParty != null) {

                    Element id = findNamespaceChild(buyerTradeParty, namespacesInScope, "ID");
                    Element globalID = findNamespaceChild(buyerTradeParty, namespacesInScope, "GlobalID");

                    if (globalID != null) {
                        Attribute schemeID = globalID.getAttribute("schemeID");
                        if (schemeID != null) {
                            bt0046 = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(globalID.getAttributeValue("schemeID"), globalID.getText()));
                        } else {
                            bt0046 = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(globalID.getText()));
                        }
                        invoice.getBG0007Buyer(0).getBT0046BuyerIdentifierAndSchemeIdentifier().add(bt0046);
                    }
                    else if (id != null){
                        bt0046 = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(id.getText()));
                        invoice.getBG0007Buyer(0).getBT0046BuyerIdentifierAndSchemeIdentifier().add(bt0046);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBT0046(document, cenInvoice, errors);
    }
}
