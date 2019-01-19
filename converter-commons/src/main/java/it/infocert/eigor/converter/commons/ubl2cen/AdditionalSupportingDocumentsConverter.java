package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Additional Supporting Documents Custom Converter
 */
public class AdditionalSupportingDocumentsConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0024(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        BG0024AdditionalSupportingDocuments bg0024;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> additionalDocumentReferences = findNamespaceChildren(rootElement, namespacesInScope, "AdditionalDocumentReference");

        for (Element elemAdd : additionalDocumentReferences) {
            bg0024 = new BG0024AdditionalSupportingDocuments();

            Element documentTypeCode = findNamespaceChild(elemAdd, namespacesInScope, "DocumentTypeCode");
            Element id = findNamespaceChild(elemAdd, namespacesInScope, "ID");
            if (id != null) {
                if (documentTypeCode != null) {
                    if ("916".equals(documentTypeCode.getValue())) {
                        BT0122SupportingDocumentReference bt0122 = new BT0122SupportingDocumentReference(id.getText());
                        bg0024.getBT0122SupportingDocumentReference().add(bt0122);
                    }
                    if ("130".equals(documentTypeCode.getValue())) {
                        String schemeID = id.getAttributeValue("schemeID");
                        BT0018InvoicedObjectIdentifierAndSchemeIdentifier bt0018 = new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(new Identifier(schemeID, id.getText()));
                        invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add(bt0018);
                    }
                } else {
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
                    TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), callingLocation);
                    try {
                        final FileReference fileReference = strToBinConverter.convert(embeddedDocumentBinaryObject);
                        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(fileReference);
                        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                    } catch (IllegalArgumentException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, attachment.toString())
                                        .build());
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
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0024(document, cenInvoice, errors, callingLocation);
    }
}
