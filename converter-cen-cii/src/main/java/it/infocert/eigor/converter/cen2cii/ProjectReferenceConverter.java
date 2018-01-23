package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
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
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {

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

        if (!cenInvoice.getBT0011ProjectReference().isEmpty()) {
            Element specifiedProcuringProject = new Element("SpecifiedProcuringProject", ramNs);
            Element id = new Element("ID", ramNs);
            id.setText(cenInvoice.getBT0011ProjectReference(0).getValue());
            specifiedProcuringProject.addContent(id);
            applicableHeaderTradeAgreement.addContent(specifiedProcuringProject);
        }

        if (!cenInvoice.getBT0012ContractReference().isEmpty()) {
            BT0012ContractReference bt0012 = cenInvoice.getBT0012ContractReference(0);
            Element contractReferencedDocument = new Element("ContractReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0012.getValue());
            contractReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(contractReferencedDocument);
        }

        if (!cenInvoice.getBT0013PurchaseOrderReference().isEmpty()) {
            BT0013PurchaseOrderReference bt0013 = cenInvoice.getBT0013PurchaseOrderReference(0);
            Element buyerOrderReferencedDocument = new Element("BuyerOrderReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0013.getValue());
            buyerOrderReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(buyerOrderReferencedDocument);
        }

        if (!cenInvoice.getBT0014SalesOrderReference().isEmpty()) {
            BT0014SalesOrderReference bt0014 = cenInvoice.getBT0014SalesOrderReference(0);
            Element sellerOrderReferencedDocument = new Element("SellerOrderReferencedDocument", ramNs);
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0014.getValue());
            sellerOrderReferencedDocument.addContent(issuerAssignedID);
            applicableHeaderTradeAgreement.addContent(sellerOrderReferencedDocument);
        }
    }
}
