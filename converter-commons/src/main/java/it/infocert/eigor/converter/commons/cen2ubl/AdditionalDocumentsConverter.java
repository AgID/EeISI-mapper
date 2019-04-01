package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.codehaus.plexus.util.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdditionalDocumentsConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(AdditionalDocumentsConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
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

                        if (ErrorCode.Location.CII_OUT.equals(callingLocation)) {
                            Element documentTypeCode = new Element("DocumentTypeCode");
                            documentTypeCode.setText("916");
                            additionalDocumentReference.addContent(documentTypeCode);
                        }
                    }

                    if (!elemBg24.getBT0123SupportingDocumentDescription().isEmpty()) {
                        BT0123SupportingDocumentDescription bt0123 = elemBg24.getBT0123SupportingDocumentDescription(0);
                        Element documentDescription = new Element("DocumentDescription");
                        documentDescription.setText(bt0123.getValue());
                        additionalDocumentReference.addContent(documentDescription);
                    }

                    Element attachment = null;
                    if (!elemBg24.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                        FileReference bt0125 =
                                elemBg24.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(0).getValue();


                        attachment = new Element("Attachment");
                        additionalDocumentReference.addContent(attachment);
                        Element embeddedDocumentBinaryObject = new Element("EmbeddedDocumentBinaryObject");
                        embeddedDocumentBinaryObject.setAttribute("mimeCode", bt0125.getMimeType().toString());
                        embeddedDocumentBinaryObject.setAttribute("filename", bt0125.getFileName());
                        try {
                            String content = FileUtils.fileRead(new File(bt0125.getFilePath()));
                            embeddedDocumentBinaryObject.setText(content);
                            attachment.addContent(embeddedDocumentBinaryObject);
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
                    if (!elemBg24.getBT0124ExternalDocumentLocation().isEmpty()) {
                        BT0124ExternalDocumentLocation bt0124 = elemBg24.getBT0124ExternalDocumentLocation(0);
                        Element uri = new Element("URI");
                        uri.setText(bt0124.getValue());
                        if (attachment == null) {
                            attachment = new Element("Attachment");
                            additionalDocumentReference.addContent(attachment);
                        }
                        Element externalReference = new Element("ExternalReference");
                        attachment.addContent(externalReference);
                        externalReference.addContent(uri);
                    }
                }
            }
        }
    }
}
