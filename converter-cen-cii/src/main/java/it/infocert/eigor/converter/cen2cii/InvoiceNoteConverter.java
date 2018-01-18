package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Invoice Note Custom Converter
 */
public class InvoiceNoteConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0001InvoiceNote().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

            Element exchangedDocument = findNamespaceChild(rootElement, namespacesInScope, "ExchangedDocument");

            if (exchangedDocument == null) {
                exchangedDocument = new Element("ExchangedDocument", rootElement.getNamespace("rsm"));
                rootElement.addContent(exchangedDocument);
            }

            List<BG0001InvoiceNote> bg0001InvoiceNote = cenInvoice.getBG0001InvoiceNote();
            for (int i = 0; i < bg0001InvoiceNote.size(); i++) {
                BG0001InvoiceNote bg0001 = bg0001InvoiceNote.get(i);
                Element includedNote = new Element("IncludedNote", rootElement.getNamespace("ram"));

                // FIXME according to CII XSD, ID is mandatory, but we have no mapping, thus index is used as mock value
                Element id = new Element("ID", rootElement.getNamespace("ram"));
                id.setText(String.valueOf(i));
                includedNote.addContent(id);

                if (!bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                    Element subjectCode = new Element("SubjectCode", rootElement.getNamespace("ram"));
                    subjectCode.setText(bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue());
                    includedNote.addContent(subjectCode);
                }

                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    Element content = new Element("Content", rootElement.getNamespace("ram"));
                    content.setText(bg0001.getBT0022InvoiceNote(0).getValue());
                    includedNote.addContent(content);
                }

                exchangedDocument.addContent(includedNote);
            }
        }
    }
}