package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0017TenderOrLotReference;
import it.infocert.eigor.model.core.model.BT0018InvoicedObjectIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0019BuyerAccountingReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The TenderRefInvoicedObjectID Converter
 */
public class TenderRefInvoicedObjectID extends CustomConverterUtils implements CustomMapping<Document> {

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

        for (BT0017TenderOrLotReference bt0017 : cenInvoice.getBT0017TenderOrLotReference()) {
            Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", ramNs);

            Element typeCode = new Element("TypeCode", ramNs);
            typeCode.setText("50");
            additionalReferencedDocument.addContent(typeCode);

            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(bt0017.getValue());
            additionalReferencedDocument.addContent(issuerAssignedID);

            applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
        }

        for (BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 : cenInvoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier()) {
            Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", ramNs);

            Element typeCode = new Element("TypeCode", ramNs);
            typeCode.setText("130");
            additionalReferencedDocument.addContent(typeCode);

            String identificationSchema = bt0018.getValue().getIdentificationSchema();
            if (identificationSchema != null) {
                Element referenceTypeCode = new Element("ReferenceTypeCode", ramNs);
                referenceTypeCode.setText(identificationSchema);
                additionalReferencedDocument.addContent(referenceTypeCode);
            }

            String identifier = bt0018.getValue().getIdentifier();
            Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
            issuerAssignedID.setText(identifier);
            additionalReferencedDocument.addContent(issuerAssignedID);

            applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
        }

        if (!cenInvoice.getBT0019BuyerAccountingReference().isEmpty()) {
            BT0019BuyerAccountingReference bt0019 = cenInvoice.getBT0019BuyerAccountingReference(0);

            Element applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (applicableHeaderTradeSettlement == null) {
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            }

            Element receivableSpecifiedTradeAccountingAccount = new Element("ReceivableSpecifiedTradeAccountingAccount", ramNs);
            Element id = new Element("ID", ramNs);
            id.setText(bt0019.getValue());
            receivableSpecifiedTradeAccountingAccount.addContent(id);
            applicableHeaderTradeSettlement.addContent(receivableSpecifiedTradeAccountingAccount);
        }
    }
}