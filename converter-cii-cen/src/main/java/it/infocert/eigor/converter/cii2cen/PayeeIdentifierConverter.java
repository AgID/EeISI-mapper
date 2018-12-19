package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0060PayeeIdentifierAndSchemeIdentifier;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Payee Identifier Custom Converter
 */
public class PayeeIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

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
                        Attribute schemeID = globalID.getAttribute("schemeID");
                        BT0060PayeeIdentifierAndSchemeIdentifier bt0060 = null;
                        if (schemeID != null) {
                            bt0060 = new BT0060PayeeIdentifierAndSchemeIdentifier(new Identifier(globalID.getAttributeValue("schemeID"), globalID.getText()));
                        } else {
                            bt0060 = new BT0060PayeeIdentifierAndSchemeIdentifier(new Identifier(globalID.getText()));
                        }
                        invoice.getBG0010Payee(0).getBT0060PayeeIdentifierAndSchemeIdentifier().add(bt0060);
                    } else if (id != null) {
                        BT0060PayeeIdentifierAndSchemeIdentifier bt0060 = new BT0060PayeeIdentifierAndSchemeIdentifier(new Identifier(id.getAttributeValue("schemeID"), id.getText()));
                        invoice.getBG0010Payee(0).getBT0060PayeeIdentifierAndSchemeIdentifier().add(bt0060);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBT0060(document, cenInvoice, errors);
    }
}