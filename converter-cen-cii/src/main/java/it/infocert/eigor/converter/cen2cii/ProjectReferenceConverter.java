package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0012ContractReference;
import it.infocert.eigor.model.core.model.BT0013PurchaseOrderReference;
import it.infocert.eigor.model.core.model.BT0014SalesOrderReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Project Reference Custom Converter
 */
public class ProjectReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
            rootElement.addContent(supplyChainTradeTransaction);
        }

        Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
        if (applicableHeaderTradeAgreement == null) {
            applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
        }

        if (!invoice.getBT0014SalesOrderReference().isEmpty()) {
            BT0014SalesOrderReference bt0014 = invoice.getBT0014SalesOrderReference(0);
            Element sellerOrderReferencedDocument = new Element("SellerOrderReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0014.getValue());
            sellerOrderReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(sellerOrderReferencedDocument);
        }

        if (!invoice.getBT0013PurchaseOrderReference().isEmpty()) {
            BT0013PurchaseOrderReference bt0013 = invoice.getBT0013PurchaseOrderReference(0);
            Element buyerOrderReferencedDocument = new Element("BuyerOrderReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0013.getValue());
            buyerOrderReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(buyerOrderReferencedDocument);
        }

        if (!invoice.getBT0012ContractReference().isEmpty()) {
            BT0012ContractReference bt0012 = invoice.getBT0012ContractReference(0);
            Element contractReferencedDocument = new Element("ContractReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0012.getValue());
            contractReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(contractReferencedDocument);
        }
    }
}
