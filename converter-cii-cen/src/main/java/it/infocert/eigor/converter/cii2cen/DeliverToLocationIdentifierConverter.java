package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0071DeliverToLocationIdentifierAndSchemeIdentifier;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Deliver To Location Identifier Converter
 */
public class DeliverToLocationIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0071(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeDelivery");
            if (child1 != null) {
                Element shipToTradeParty = findNamespaceChild(child1, namespacesInScope, "ShipToTradeParty");
                if (shipToTradeParty != null) {
                    Element id = findNamespaceChild(shipToTradeParty, namespacesInScope, "ID");
                    Element globalID = findNamespaceChild(shipToTradeParty, namespacesInScope, "GlobalID");
                    if (globalID != null) {
                        Attribute schemeID = globalID.getAttribute("schemeID");
                        BT0071DeliverToLocationIdentifierAndSchemeIdentifier bt0071;
                        if (schemeID != null) {
                            bt0071 = new BT0071DeliverToLocationIdentifierAndSchemeIdentifier(new Identifier(globalID.getAttributeValue("schemeID"), globalID.getText()));
                        } else {
                            bt0071 = new BT0071DeliverToLocationIdentifierAndSchemeIdentifier(new Identifier(globalID.getText()));
                        }
                        invoice.getBG0013DeliveryInformation(0).getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().add(bt0071);
                    }
                    else if (id != null) {
                        BT0071DeliverToLocationIdentifierAndSchemeIdentifier bt0071 = new BT0071DeliverToLocationIdentifierAndSchemeIdentifier(new Identifier(id.getAttributeValue("schemeID"), id.getText()));
                        invoice.getBG0013DeliveryInformation(0).getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().add(bt0071);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBT0071(document, cenInvoice, errors);
    }
}
