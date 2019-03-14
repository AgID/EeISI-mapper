package it.infocert.eigor.converter.commons.cen2peppolcn;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class NoteConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
                    EigorConfiguration eigorConfiguration) {

        final Element root = document.getRootElement();
        String concatenatedInvoiceNote = "";
        Element btConcat = new Element("Note");

        if (!cenInvoice.getBG0001InvoiceNote().isEmpty()) {
            for (BG0001InvoiceNote bg0001 : cenInvoice.getBG0001InvoiceNote()) {
                String bg0022 = "";
                String bg0021 = "";
                if (!bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                    bg0021 = bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue();
                    concatenatedInvoiceNote = (concatenatedInvoiceNote == "") ? concatenatedInvoiceNote + bg0021 :
                            concatenatedInvoiceNote + "-" + bg0021;

                }

                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    bg0022 = bg0001.getBT0022InvoiceNote(0).getValue();
                    concatenatedInvoiceNote = (concatenatedInvoiceNote == "") ? concatenatedInvoiceNote + bg0022 :
                            concatenatedInvoiceNote + "-" + bg0022;
                }
            }

            btConcat.setText(concatenatedInvoiceNote);
            root.addContent(btConcat);
        }
    }
}
