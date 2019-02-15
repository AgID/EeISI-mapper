package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.*;
import org.codehaus.plexus.util.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdditionalSupportingDocumentsConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        if (!invoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace rsmNs = rootElement.getNamespace("rsm");
            Namespace ramNs = rootElement.getNamespace("ram");


            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                rootElement.addContent(supplyChainTradeTransaction);
            }

            Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }

            for (BG0024AdditionalSupportingDocuments bg0024 : invoice.getBG0024AdditionalSupportingDocuments()) {
                Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", rootElement.getNamespace("ram"));

                if (!bg0024.getBT0122SupportingDocumentReference().isEmpty()) {
                    BT0122SupportingDocumentReference bt0122 = bg0024.getBT0122SupportingDocumentReference(0);
                    Element issuerAssignedID = new Element("IssuerAssignedID", rootElement.getNamespace("ram"));
                    issuerAssignedID.setText(bt0122.getValue());
                    additionalReferencedDocument.addContent(issuerAssignedID);
                    Element typeCode = new Element("TypeCode", ramNs);
                    typeCode.setText("916");
                    additionalReferencedDocument.addContent(typeCode);
                }

                if (!invoice.getBT0017TenderOrLotReference().isEmpty()) {

                    final BT0017TenderOrLotReference bt0017 = invoice.getBT0017TenderOrLotReference(0);

                    Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                    issuerAssignedID.setText(bt0017.getValue());
                    additionalReferencedDocument.addContent(issuerAssignedID);
                }

                if (!invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                    final BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier(0);

                    String identificationSchema = bt0018.getValue().getIdentificationSchema();
                    if (identificationSchema != null) {
                        Element referenceTypeCode = new Element("ReferenceTypeCode", ramNs);
                        referenceTypeCode.setText(identificationSchema);
                        additionalReferencedDocument.addContent(referenceTypeCode);
                    }
                }

                if (!bg0024.getBT0124ExternalDocumentLocation().isEmpty()) {
                    BT0124ExternalDocumentLocation bt0124 = bg0024.getBT0124ExternalDocumentLocation(0);
                    Element uriid = new Element("URIID", rootElement.getNamespace("ram"));
                    uriid.setText(bt0124.getValue());
                    additionalReferencedDocument.addContent(uriid);
                }

                if (!invoice.getBT0017TenderOrLotReference().isEmpty()) {

                    final BT0017TenderOrLotReference bt0017 = invoice.getBT0017TenderOrLotReference(0);

                    Element typeCode = new Element("TypeCode", ramNs);
                    typeCode.setText(bt0017.getValue());
                    additionalReferencedDocument.addContent(typeCode);
                }

                if (!invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                    final BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier(0);

                    Element typeCode = new Element("TypeCode", ramNs);
                    typeCode.setText(bt0018.getValue().getIdentifier());
                    additionalReferencedDocument.addContent(typeCode);
                }

                for (BT0123SupportingDocumentDescription bt0123 : bg0024.getBT0123SupportingDocumentDescription()) {
                    Element name = new Element("Name", rootElement.getNamespace("ram"));
                    name.setText(bt0123.getValue());
                    additionalReferencedDocument.addContent(name);
                }

                for (BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename filename : bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename()) {
                    final FileReference bt0125 = filename.getValue();
                    Element attachmentBinaryObject = new Element("AttachmentBinaryObject", rootElement.getNamespace("ram"));
                    attachmentBinaryObject.setAttribute("mimeCode", bt0125.getMimeType().toString());
                    attachmentBinaryObject.setAttribute("filename", bt0125.getFileName());
                    try {
                        String content = FileUtils.fileRead(new File(bt0125.getFilePath()));
                        attachmentBinaryObject.setText(content);
                        additionalReferencedDocument.addContent(attachmentBinaryObject);
                    } catch (IOException e) {
                        errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                String.format("Cannot read attachment file %s!", bt0125.getFileName()),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.INVALID,
                                e
                        )));
                    }
                }

                if (!invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                    final BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier(0);
                    String identificationSchema = bt0018.getValue().getIdentificationSchema();
                    if (identificationSchema != null) {
                        Element referenceTypeCode = new Element("ReferenceTypeCode", ramNs);
                        referenceTypeCode.setText(identificationSchema);
                        additionalReferencedDocument.addContent(referenceTypeCode);
                    }
                }

                if (!invoice.getBT0011ProjectReference().isEmpty()) {
                    Element specifiedProcuringProject = new Element("SpecifiedProcuringProject", ramNs);
                    Element id = new Element("ID", ramNs);
                    id.setText(invoice.getBT0011ProjectReference(0).getValue());
                    specifiedProcuringProject.addContent(id);
                    applicableHeaderTradeAgreement.addContent(specifiedProcuringProject);
                }

                applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
            }
        }
    }
}
