package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Additional Supporting Documents Custom Converter
 */
public class AdditionalSupportingDocumentsConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0024(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0024AdditionalSupportingDocuments bg0024 = null;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            List<Element> allegati = fatturaElettronicaBody.getChildren();
            for (Element allegato : allegati) {
                if (allegato.getName().equals("Allegati")) {
                    bg0024 = new BG0024AdditionalSupportingDocuments();
                    Element nomeAttachment = allegato.getChild("NomeAttachment");
                    if (nomeAttachment != null) {
                        BT0122SupportingDocumentReference supportingDocumentReference = new BT0122SupportingDocumentReference(nomeAttachment.getText());
                        bg0024.getBT0122SupportingDocumentReference().add(supportingDocumentReference);
                    }
                    Element descrizioneAttachment = allegato.getChild("DescrizioneAttachment");
                    if (descrizioneAttachment != null) {
                        BT0123SupportingDocumentDescription supportingDocumentDescription = new BT0123SupportingDocumentDescription(descrizioneAttachment.getText());
                        bg0024.getBT0123SupportingDocumentDescription().add(supportingDocumentDescription);
                    }
                    Element attachment = allegato.getChild("Attachment");
                    if (attachment != null) {
                        BT0124ExternalDocumentLocation externalDocumentLocation = new BT0124ExternalDocumentLocation(attachment.getText());
                        bg0024.getBT0124ExternalDocumentLocation().add(externalDocumentLocation);
                    }
                    invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0024(document, cenInvoice, errors);
    }
}
