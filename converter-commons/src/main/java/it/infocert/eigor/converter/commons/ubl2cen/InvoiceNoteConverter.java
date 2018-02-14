package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BT0021InvoiceNoteSubjectCode;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.Iterator;
import java.util.List;

/**
 * The Invoice Note Custom Converter
 */
public class InvoiceNoteConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0001(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0001InvoiceNote bg0001;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> notes = findNamespaceChildren(rootElement, namespacesInScope, "Note");

        final Iterator<Element> itr = notes.iterator();
        while(itr.hasNext()) {
            final Element elem = itr.next();
            bg0001 = new BG0001InvoiceNote();

            String text = elem.getText();
            if (text != null) {
                if (text.matches("^#.*#.*$")) {
                    BT0021InvoiceNoteSubjectCode bt0021 = new BT0021InvoiceNoteSubjectCode(text.substring(1, text.indexOf("#", 1)));
                    bg0001.getBT0021InvoiceNoteSubjectCode().add(bt0021);
                    if (!text.matches("^#.*#$")) {
                        BT0022InvoiceNote bt0022 = new BT0022InvoiceNote(text.substring(text.indexOf("#", 1) + 1));
                        bg0001.getBT0022InvoiceNote().add(bt0022);
                    } else if(itr.hasNext()) {
                        final Element note = itr.next();
                        if (!note.getText().matches("^#.*#.*$")) {
                            BT0022InvoiceNote bt0022 = new BT0022InvoiceNote(note.getText());
                            bg0001.getBT0022InvoiceNote().add(bt0022);
                        }
                    }

                } else {
                    BT0022InvoiceNote bt0022 = new BT0022InvoiceNote(text);
                    bg0001.getBT0022InvoiceNote().add(bt0022);
                }

                invoice.getBG0001InvoiceNote().add(bg0001);
            }
        }


        /*for (Element elem : notes) {
            bg0001 = new BG0001InvoiceNote();

            String text = elem.getText();
            if (text != null) {
                if (text.matches("^#.*#.*$")) {
                    BT0021InvoiceNoteSubjectCode bt0021 = new BT0021InvoiceNoteSubjectCode(text.substring(1, text.indexOf("#", 1)));
                    bg0001.getBT0021InvoiceNoteSubjectCode().add(bt0021);
                    BT0022InvoiceNote bt0022 = new BT0022InvoiceNote(text.substring(text.indexOf("#", 1) + 1));
                    bg0001.getBT0022InvoiceNote().add(bt0022);
                } else {
                    BT0022InvoiceNote bt0022 = new BT0022InvoiceNote(text);
                    bg0001.getBT0022InvoiceNote().add(bt0022);
                }

                invoice.getBG0001InvoiceNote().add(bg0001);
            }
        }*/
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0001(document, cenInvoice, errors);
    }
}