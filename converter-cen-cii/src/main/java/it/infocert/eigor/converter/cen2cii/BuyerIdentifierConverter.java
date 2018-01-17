package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Buyer Identifier Custom Converter
 */
public class BuyerIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0007Buyer().isEmpty() && !cenInvoice.getBG0007Buyer(0).getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeAgreement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                rootElement.addContent(supplyChainTradeTransaction);
            }

            applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }

            Element buyerTradeParty = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "BuyerTradeParty");
            if (buyerTradeParty == null) {
                buyerTradeParty = new Element("BuyerTradeParty", rootElement.getNamespace("ram"));
                applicableHeaderTradeAgreement.addContent(buyerTradeParty);
            }

            Element id = new Element("ID", rootElement.getNamespace("ram")); // maybe GlobalID ?
            Identifier identifier = cenInvoice.getBG0007Buyer(0).getBT0046BuyerIdentifierAndSchemeIdentifier(0).getValue();
            id.setText(identifier.getIdentifier());
            if (identifier.getIdentificationSchema() != null) {
                id.setAttribute("schemeID", identifier.getIdentificationSchema());
            }
            buyerTradeParty.addContent(id);
        }
    }
}

