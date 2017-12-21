package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.conversion.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Additional Supporting Documents Custom Converter
 */
public class AdditionalSupportingDocumentsConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0024(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0024AdditionalSupportingDocuments bg0024 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> additionalDocumentReferences = findNamespaceChildren(rootElement, namespacesInScope, "AdditionalDocumentReference");

        for (Element elemAdd : additionalDocumentReferences) {
            bg0024 = new BG0024AdditionalSupportingDocuments();

            Element documentTypeCode = findNamespaceChild(elemAdd, namespacesInScope, "DocumentTypeCode");
            if(documentTypeCode != null && "916".equals(documentTypeCode.getValue())) {
                Element id = findNamespaceChild(elemAdd, namespacesInScope, "ID");
                if (id != null) {
                    BT0122SupportingDocumentReference bt0122 = new BT0122SupportingDocumentReference(id.getText());
                    bg0024.getBT0122SupportingDocumentReference().add(bt0122);
                }
            }

            Element documentDescription = findNamespaceChild(elemAdd, namespacesInScope, "DocumentDescription");
            if (documentDescription != null) {
                BT0123SupportingDocumentDescription bt0123 = new BT0123SupportingDocumentDescription(documentDescription.getText());
                bg0024.getBT0123SupportingDocumentDescription().add(bt0123);
            }

            Element attachment = findNamespaceChild(elemAdd, namespacesInScope, "Attachment");
            if (attachment != null) {

                Element externalReference = findNamespaceChild(attachment, namespacesInScope, "ExternalReference");
                if (externalReference != null) {
                    Element uri = findNamespaceChild(externalReference, namespacesInScope, "URI");
                    if (uri != null) {
                        BT0124ExternalDocumentLocation bt0124 = new BT0124ExternalDocumentLocation(uri.getText());
                        bg0024.getBT0124ExternalDocumentLocation().add(bt0124);
                    }
                }

                Element embeddedDocumentBinaryObject = findNamespaceChild(attachment, namespacesInScope, "EmbeddedDocumentBinaryObject");
                if (embeddedDocumentBinaryObject != null) {
                    AttachmentToFileReferenceConverter strToBinConverter = new AttachmentToFileReferenceConverter(DefaultEigorConfigurationLoader.configuration());
                    try {
                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(embeddedDocumentBinaryObject));
                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                    } catch (IllegalArgumentException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("AdditionalSupportingDocumentsConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    } catch (EigorRuntimeException e) {
                        errors.add(ConversionIssue.newError(e));
                    }
                }
            }
            invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0024(document, cenInvoice, errors);
    }
}
