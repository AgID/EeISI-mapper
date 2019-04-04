package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.FileReference;
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

        List<Element> additionalReferencedDocuments;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (child1 != null) {
                additionalReferencedDocuments = findNamespaceChildren(child1, namespacesInScope, "AdditionalReferencedDocument");

                for(Element elem : additionalReferencedDocuments) {
                    bg0024 = new BG0024AdditionalSupportingDocuments();
                    Element issuerAssignedID = findNamespaceChild(elem, namespacesInScope, "IssuerAssignedID");
                    Element typeCode = findNamespaceChild(elem, namespacesInScope, "TypeCode");
                    Element name = findNamespaceChild(elem, namespacesInScope, "Name");
                    Element uriid = findNamespaceChild(elem, namespacesInScope, "URIID");
                    Element attachmentBinaryObject = findNamespaceChild(elem, namespacesInScope, "AttachmentBinaryObject");

                    // type code 130 has to be mapped into BT18, not BT122
                    if(typeCode!=null && typeCode.getTextNormalize().equals("130")) continue;

                    if (issuerAssignedID != null && typeCode != null) {
                        BT0122SupportingDocumentReference bt0122 = new BT0122SupportingDocumentReference(issuerAssignedID.getText());
                        bg0024.getBT0122SupportingDocumentReference().add(bt0122);
                    }
                    if (name != null) {
                        BT0123SupportingDocumentDescription bt0123 = new BT0123SupportingDocumentDescription(name.getText());
                        bg0024.getBT0123SupportingDocumentDescription().add(bt0123);
                    }
                    if (uriid != null) {
                        BT0124ExternalDocumentLocation bt0124 = new BT0124ExternalDocumentLocation(uriid.getText());
                        bg0024.getBT0124ExternalDocumentLocation().add(bt0124);
                    }
                    if (attachmentBinaryObject != null) {
                        TypeConverter<Element, FileReference> attToFileConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.CII_IN);
                        try {
                            BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(attToFileConverter.convert(attachmentBinaryObject));
                            bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                        }catch (IllegalArgumentException | ConversionFailedException e) {
                            errors.add(ConversionIssue.newError(new EigorRuntimeException(e, ErrorMessage.builder()
                            .message(e.getMessage())
                            .location(callingLocation)
                            .action(ErrorCode.Action.HARDCODED_MAP)
                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                            .addParam("offendingItem", attachmentBinaryObject.toString())
                            .build())));
                        }
                    }
                    invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0024(document, cenInvoice, errors, callingLocation);
    }
}
