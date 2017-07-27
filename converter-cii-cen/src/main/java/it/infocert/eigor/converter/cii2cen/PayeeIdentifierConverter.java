package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0060PayeeIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0071DeliverToLocationIdentifierAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Payee Identifier Custom Converter
 */
public class PayeeIdentifierConverter extends CustomConverter {

    public PayeeIdentifierConverter() {
        super(new Reflections("it.infocert"), new ConversionRegistry());
    }

    public ConversionResult<BG0000Invoice> toBT0060(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (child1 != null) {
                Element payeeTradeParty = findNamespaceChild(child1, namespacesInScope, "PayeeTradeParty");
                if (payeeTradeParty != null) {
                    Element id = findNamespaceChild(payeeTradeParty, namespacesInScope, "ID");
                    Element globalID = findNamespaceChild(payeeTradeParty, namespacesInScope, "GlobalID");
                    if (globalID != null) {
                        BT0060PayeeIdentifierAndSchemeIdentifier bt0060 = new BT0060PayeeIdentifierAndSchemeIdentifier(globalID.getText());
                        invoice.getBG0010Payee(0).getBT0060PayeeIdentifierAndSchemeIdentifier().add(bt0060);
                    } else if (id != null) {
                        BT0060PayeeIdentifierAndSchemeIdentifier bt0060 = new BT0060PayeeIdentifierAndSchemeIdentifier(id.getText());
                        invoice.getBG0010Payee(0).getBT0060PayeeIdentifierAndSchemeIdentifier().add(bt0060);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
}