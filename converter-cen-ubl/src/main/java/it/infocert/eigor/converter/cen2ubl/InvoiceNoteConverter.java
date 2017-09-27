package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InvoiceNoteConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(InvoiceNoteConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        Element root = document.getRootElement();
        if (root != null) {
            List<BG0001InvoiceNote> bg0001 = cenInvoice.getBG0001InvoiceNote();
            for (BG0001InvoiceNote elemBG1 : bg0001) {
                if (!elemBG1.getBT0022InvoiceNote().isEmpty()) {
                    BT0022InvoiceNote bt0022 = elemBG1.getBT0022InvoiceNote(0);
                    Element note = new Element("Note");
                    note.addContent(bt0022.getValue());
                    root.addContent(note);
                }
            }
        }
    }
}