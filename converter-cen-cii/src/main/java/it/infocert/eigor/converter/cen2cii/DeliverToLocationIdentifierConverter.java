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
 * The Deliver To Location Identifier Converter
 */
public class DeliverToLocationIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0013DeliveryInformation().isEmpty() && !cenInvoice.getBG0013DeliveryInformation(0).getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeDelivery = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                rootElement.addContent(supplyChainTradeTransaction);
            }

            applicableHeaderTradeDelivery = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeDelivery");
            if (applicableHeaderTradeDelivery == null) {
                applicableHeaderTradeDelivery = new Element("ApplicableHeaderTradeDelivery", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeDelivery);
            }

            Element shipToTradeParty = findNamespaceChild(applicableHeaderTradeDelivery, namespacesInScope, "ShipToTradeParty");
            if (shipToTradeParty == null) {
                shipToTradeParty = new Element("ShipToTradeParty", rootElement.getNamespace("ram"));
                applicableHeaderTradeDelivery.addContent(shipToTradeParty);
            }

            Element id = new Element("ID", rootElement.getNamespace("ram")); //maybe GlobalID?
            Identifier identifier = cenInvoice.getBG0013DeliveryInformation(0).getBT0071DeliverToLocationIdentifierAndSchemeIdentifier(0).getValue();
            id.setText(identifier.getIdentifier());
            if (identifier.getIdentificationSchema() != null) {
                id.setAttribute("schemeID", identifier.getIdentificationSchema());
            }
            shipToTradeParty.addContent(id);
        }
    }
}
