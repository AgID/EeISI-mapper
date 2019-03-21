package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class NoteConverter extends FirstLevelElementsConverter {

    @Override
    protected void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Preconditions.checkArgument(root != null, "This method is only for internal use.");

        if (!invoice.getBG0001InvoiceNote().isEmpty()) {
            for (BG0001InvoiceNote bg0001 : invoice.getBG0001InvoiceNote()) {
                for (BT0022InvoiceNote bt0022 : bg0001.getBT0022InvoiceNote()) {
                    Element note = new Element("Note");
                    String resultNote = "";
                    if (bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                        resultNote = bt0022.getValue();
                    } else {
                        String bt0021 = bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue();
                        resultNote = "#" + bt0021 + "#" + bt0022.getValue();
                    }
                    if (!resultNote.isEmpty()) {
                        note.setText(resultNote);
                        root.addContent(note);
                    }
                }
            }
        }

    }

}
