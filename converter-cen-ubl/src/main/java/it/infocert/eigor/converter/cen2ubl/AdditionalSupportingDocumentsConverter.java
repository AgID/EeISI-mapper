package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdditionalSupportingDocumentsConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(AdditionalSupportingDocumentsConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {

                List<BG0024AdditionalSupportingDocuments> bg0024 = cenInvoice.getBG0024AdditionalSupportingDocuments();
                for (BG0024AdditionalSupportingDocuments elemBg24 : bg0024) {
                    Element additionalDocumentReference = new Element("AdditionalDocumentReference");
                    root.addContent(additionalDocumentReference);
                    if (!elemBg24.getBT0122SupportingDocumentReference().isEmpty()) {
                        BT0122SupportingDocumentReference bt0122 = elemBg24.getBT0122SupportingDocumentReference(0);
                        Element id = new Element("ID");
                        id.setText(bt0122.getValue());
                        additionalDocumentReference.addContent(id);
                    }
                    if (!elemBg24.getBT0123SupportingDocumentDescription().isEmpty()) {
                        BT0123SupportingDocumentDescription bt0123 = elemBg24.getBT0123SupportingDocumentDescription(0);
                        Element documentDescription = new Element("DocumentDescription");
                        documentDescription.setText(bt0123.getValue());
                        additionalDocumentReference.addContent(documentDescription);
                    }
                    if (!elemBg24.getBT0124ExternalDocumentLocation().isEmpty()) {
                        BT0124ExternalDocumentLocation bt0124 = elemBg24.getBT0124ExternalDocumentLocation(0);
                        Element uri = new Element("URI");
                        uri.setText(bt0124.getValue());
                        Element attachment = new Element("Attachment");
                        additionalDocumentReference.addContent(attachment);
                        Element externalReference = new Element("ExternalReference");
                        attachment.addContent(externalReference);
                        externalReference.addContent(uri);
                    }
                }
            }
        }
    }
}