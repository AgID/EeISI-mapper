package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0010Payee;
import it.infocert.eigor.model.core.model.BT0059PayeeName;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Payee Identifier Custom Converter
 */
public class PayeeIdentifierConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!cenInvoice.getBG0010Payee().isEmpty()) {
            BG0010Payee bg0010 = cenInvoice.getBG0010Payee(0);
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace rsmNs = rootElement.getNamespace("rsm");
            Namespace ramNs = rootElement.getNamespace("ram");

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
                rootElement.addContent(supplyChainTradeTransaction);
            }

            Element applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (applicableHeaderTradeSettlement == null) {
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            }

            Element payeeTradeParty = findNamespaceChild(applicableHeaderTradeSettlement, namespacesInScope, "PayeeTradeParty");
            if (payeeTradeParty == null) {
                payeeTradeParty = new Element("PayeeTradeParty", ramNs);
                applicableHeaderTradeSettlement.addContent(payeeTradeParty);
            }

            if (!bg0010.getBT0060PayeeIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0060 = bg0010.getBT0060PayeeIdentifierAndSchemeIdentifier(0).getValue();
                Element id = new Element("ID", ramNs); //maybe GlobalID?
                String schema = bt0060.getIdentificationSchema();
                if (schema != null) {
                    id.setAttribute("schemeID", schema);
                }
                id.setText(bt0060.getIdentifier());
                payeeTradeParty.addContent(id);
            }

            if (!bg0010.getBT0059PayeeName().isEmpty()) {
                BT0059PayeeName bt0059 = bg0010.getBT0059PayeeName(0);
                Element name = new Element("Name", ramNs);
                name.setText(bt0059.getValue());
                payeeTradeParty.addContent(name);
            }

            if (!bg0010.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0061 = bg0010.getBT0061PayeeLegalRegistrationIdentifierAndSchemeIdentifier(0).getValue();
                Element specifiedLegalOrganization = new Element("SpecifiedLegalOrganization", ramNs);
                Element id = new Element("ID", ramNs);
                id.setText(bt0061.getIdentifier());
                String schema = bt0061.getIdentificationSchema();
                if (schema != null) {
                    id.setAttribute("schemeID", schema);
                }
                specifiedLegalOrganization.addContent(id);
                payeeTradeParty.addContent(specifiedLegalOrganization);
            }
        }
    }
}