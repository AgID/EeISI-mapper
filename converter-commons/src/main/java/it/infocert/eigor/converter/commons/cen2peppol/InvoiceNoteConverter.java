package it.infocert.eigor.converter.commons.cen2peppol;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode.Location;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;

public class InvoiceNoteConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, Location callingLocation,
                    EigorConfiguration eigorConfiguration) {

        final Element root = document.getRootElement();
        String concatenatedInvoiceNote = "";
        Element btConcat = new Element("Note");

        if (!cenInvoice.getBG0001InvoiceNote().isEmpty()) {
            for (BG0001InvoiceNote bg0001 : cenInvoice.getBG0001InvoiceNote()) {
                String bg0021, bg0022;
                if (!bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                    bg0021 = bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue();
                    if (bg0021 != null && !bg0021.isEmpty()) {
                        concatenatedInvoiceNote = ("".equals(concatenatedInvoiceNote)) ? concatenatedInvoiceNote + bg0021 :
                                concatenatedInvoiceNote + "-" + bg0021;
                    }

                }

                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    bg0022 = bg0001.getBT0022InvoiceNote(0).getValue();
                    if (bg0022 != null && !bg0022.isEmpty()) {
                        concatenatedInvoiceNote = ("".equals(concatenatedInvoiceNote)) ? concatenatedInvoiceNote + bg0022 :
                                concatenatedInvoiceNote + "-" + bg0022;
                    }
                }
            }

            if (!concatenatedInvoiceNote.isEmpty()) {
                btConcat.setText(concatenatedInvoiceNote);
                root.addContent(btConcat);
            }
        }
    }
}
