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
 * The Payee Identifier Custom Converter
 */
public class PayeeIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0010Payee(0).getBT0060PayeeIdentifierAndSchemeIdentifier().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeSettlement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
                rootElement.addContent(supplyChainTradeTransaction);
            } else {
                applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
                if (applicableHeaderTradeSettlement == null) {
                    applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
                    supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
                }
            }

            Element payeeTradeParty = findNamespaceChild(applicableHeaderTradeSettlement, namespacesInScope, "PayeeTradeParty");
            if (payeeTradeParty == null) {
                payeeTradeParty = new Element("PayeeTradeParty", rootElement.getNamespace("ram"));
                applicableHeaderTradeSettlement.addContent(payeeTradeParty);
            }

            Element id = new Element("ID", rootElement.getNamespace("ram")); //maybe GlobalID?
            Identifier bt0060 = cenInvoice.getBG0010Payee(0).getBT0060PayeeIdentifierAndSchemeIdentifier(0).getValue();

            String schema = bt0060.getIdentificationSchema();
            if (schema != null) {
                id.setAttribute("schemeID", schema);
            }

            id.setText(bt0060.getIdentifier());

            payeeTradeParty.addContent(id);
        }
    }
}