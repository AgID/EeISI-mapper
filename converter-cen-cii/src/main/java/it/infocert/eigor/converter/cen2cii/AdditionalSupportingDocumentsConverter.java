package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.*;
import org.codehaus.plexus.util.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The Additional Supporting Documents Custom Converter
 */
public class AdditionalSupportingDocumentsConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

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

            for (BG0024AdditionalSupportingDocuments bg0024 : cenInvoice.getBG0024AdditionalSupportingDocuments()) {
                Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", rootElement.getNamespace("ram"));

                if (!bg0024.getBT0122SupportingDocumentReference().isEmpty()) {
                    BT0122SupportingDocumentReference bt0122 = bg0024.getBT0122SupportingDocumentReference(0);
                    Element issuerAssignedID = new Element("IssuerAssignedID", rootElement.getNamespace("ram"));
                    issuerAssignedID.setText(bt0122.getValue());
                    additionalReferencedDocument.addContent(issuerAssignedID);
                }

                if (!bg0024.getBT0123SupportingDocumentDescription().isEmpty()) {
                    BT0123SupportingDocumentDescription bt0123 = bg0024.getBT0123SupportingDocumentDescription(0);
                    Element name = new Element("Name", rootElement.getNamespace("ram"));
                    name.setText(bt0123.getValue());
                    additionalReferencedDocument.addContent(name);
                }

                if (!bg0024.getBT0124ExternalDocumentLocation().isEmpty()) {
                    BT0124ExternalDocumentLocation bt0124 = bg0024.getBT0124ExternalDocumentLocation(0);
                    Element uriid = new Element("URIID", rootElement.getNamespace("ram"));
                    uriid.setText(bt0124.getValue());
                    additionalReferencedDocument.addContent(uriid);
                }

                if (!bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                    FileReference bt0125 = bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(0).getValue();
                    Element attachmentBinaryObject = new Element("AttachmentBinaryObject", rootElement.getNamespace("ram"));
                    attachmentBinaryObject.setAttribute("mimeCode", bt0125.getMimeType().toString());
                    attachmentBinaryObject.setAttribute("filename", bt0125.getFileName());
                    try {
                        String content = FileUtils.fileRead(new File(bt0125.getFilePath()));
                        attachmentBinaryObject.setText(content);
                        additionalReferencedDocument.addContent(attachmentBinaryObject);
                    } catch (IOException e) {
                        errors.add(ConversionIssue.newError(e, e.getMessage(), "AttachmentConverter"));
                    }
                }

                applicableHeaderTradeAgreement.addContent(additionalReferencedDocument);
            }
        }
    }
}
