package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class NoteConverter extends FirstLevelElementsConverter {
    
    @Override
    protected void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Preconditions.checkArgument(root!=null, "This method is only for internal use.");

        if (!invoice.getBG0001InvoiceNote().isEmpty()) {
            for (BG0001InvoiceNote bg0001 : invoice.getBG0001InvoiceNote()) {
                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    Element note = new Element("Note");
                    String bt0022 = bg0001.getBT0022InvoiceNote(0).getValue();

                    if (bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                        note.setText(bt0022);
                    } else {
                        String bt0021 = bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue();
                        note.setText("#" + bt0021 + "#" + bt0022);
                    }

                    root.addContent(note);
                }
            }
        }

    }

}
