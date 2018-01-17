package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0031SellerVatIdentifier;
import it.infocert.eigor.model.core.model.BT0032SellerTaxRegistrationIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Seller Custom Converter
 */
public class SellerConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0004Seller().isEmpty()) {
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

            Element sellerTradeParty = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "SellerTradeParty");
            if (sellerTradeParty == null) {
                sellerTradeParty = new Element("SellerTradeParty", rootElement.getNamespace("ram"));
                applicableHeaderTradeAgreement.addContent(sellerTradeParty);
            }

            if (!cenInvoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().isEmpty()) {
                for (BT0029SellerIdentifierAndSchemeIdentifier bt0029 : cenInvoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier()) {
                    Identifier identifier = bt0029.getValue();
                    Element id = new Element("ID", rootElement.getNamespace("ram"));
                    id.setText(identifier.getIdentifier());
                    if (identifier.getIdentificationSchema() != null) {
                        id.setAttribute("schemeID", identifier.getIdentificationSchema());
                    }
                    sellerTradeParty.addContent(id);
                }

            }

            for (BT0031SellerVatIdentifier bt0031 : cenInvoice.getBG0004Seller(0).getBT0031SellerVatIdentifier()) {
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", rootElement.getNamespace("ram"));
                Element id = new Element("ID", rootElement.getNamespace("ram"));
                id.setText(bt0031.getValue());
                id.setAttribute("schemeID", "VA");
                specifiedTaxRegistration.addContent(id);
                sellerTradeParty.addContent(specifiedTaxRegistration);
            }

            for (BT0032SellerTaxRegistrationIdentifier bt0032 : cenInvoice.getBG0004Seller(0).getBT0032SellerTaxRegistrationIdentifier()) {
                Element specifiedTaxRegistration = new Element("SpecifiedTaxRegistration", rootElement.getNamespace("ram"));
                Element id = new Element("ID", rootElement.getNamespace("ram"));
                id.setText(bt0032.getValue());
                id.setAttribute("schemeID", "FC");
                specifiedTaxRegistration.addContent(id);
                sellerTradeParty.addContent(specifiedTaxRegistration);
            }
        }
    }
}