package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0017TenderOrLotReference;
import it.infocert.eigor.model.core.model.BT0018InvoicedObjectIdentifierAndSchemeIdentifier;
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

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        Element applicableHeaderTradeAgreement = null;

        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
            applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
            supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            rootElement.addContent(supplyChainTradeTransaction);
        } else {
            applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }
        }

        for (BT0017TenderOrLotReference bt0017 : cenInvoice.getBT0017TenderOrLotReference()) {
            Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", rootElement.getNamespace("ram"));

            Element typeCode = new Element("TypeCode", rootElement.getNamespace("ram"));
            typeCode.setText("50");
            additionalReferencedDocument.addContent(typeCode);

            Element issuerAssignedID = new Element("IssuerAssignedID", rootElement.getNamespace("ram"));
            issuerAssignedID.setText(bt0017.getValue());
            additionalReferencedDocument.addContent(issuerAssignedID);

            applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
        }

        for (BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 : cenInvoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier()) {
            Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", rootElement.getNamespace("ram"));

            Element typeCode = new Element("TypeCode", rootElement.getNamespace("ram"));
            typeCode.setText("130");
            additionalReferencedDocument.addContent(typeCode);

            String identificationSchema = bt0018.getValue().getIdentificationSchema();
            if (identificationSchema != null) {
                Element referenceTypeCode = new Element("ReferenceTypeCode", rootElement.getNamespace("ram"));
                referenceTypeCode.setText(identificationSchema);
                additionalReferencedDocument.addContent(referenceTypeCode);
            }

            String identifier = bt0018.getValue().getIdentifier();
            Element issuerAssignedID = new Element("IssuerAssignedID", rootElement.getNamespace("ram"));
            issuerAssignedID.setText(identifier);
            additionalReferencedDocument.addContent(issuerAssignedID);

            applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
        }
    }
}