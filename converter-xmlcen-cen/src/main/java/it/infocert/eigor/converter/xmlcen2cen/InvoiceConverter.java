package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import it.infocert.eigor.model.core.model.BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;

public class InvoiceConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0000(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg24 = rootElement.getChild("BG-24");

        if (Objects.nonNull(bg24)) {
            if (invoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {
                invoice.getBG0024AdditionalSupportingDocuments().add(new BG0024AdditionalSupportingDocuments());
            }

            final List<Element> bt125s = bg24.getChildren("BT-125");
            TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.XMLCEN_IN, "mime");
            bt125s.forEach(bt125 -> {
                final BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename attachedDocument;

                final Element attachment = bg24.getChild("BT-125");
                try {
                    BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(attachment));
                    invoice.getBG0024AdditionalSupportingDocuments().get(0).getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);
                } catch (ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(
                            e,
                            ErrorMessage.builder()
                                    .message(e.getMessage())
                                    .location(ErrorCode.Location.XMLCEN_IN)
                                    .action(ErrorCode.Action.HARDCODED_MAP)
                                    .error(ErrorCode.Error.ILLEGAL_VALUE)
                                    .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                    .addParam(ErrorMessage.OFFENDINGITEM_PARAM, attachment.toString())
                                    .build());
                    errors.add(ConversionIssue.newError(ere));
                }
            });
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0000(document, cenInvoice, errors);
    }
}
