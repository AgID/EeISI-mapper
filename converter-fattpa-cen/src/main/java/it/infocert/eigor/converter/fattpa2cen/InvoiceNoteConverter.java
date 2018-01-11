package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Invoice Note Custom Converter
 */
public class InvoiceNoteConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0001(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0001InvoiceNote bg0001 = null;
        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            if (datiGenerali != null) {
                List<Element> datiGeneraliDocumenti = datiGenerali.getChildren();
                for (Element datiGeneraliDocumento : datiGeneraliDocumenti) {
                    bg0001 = new BG0001InvoiceNote();
                    if (datiGeneraliDocumento.getName().equals("DatiGeneraliDocumento")) {
                        Element causale = datiGeneraliDocumento.getChild("Causale");
                        if (causale != null) {
                            BT0022InvoiceNote invoiceNote = new BT0022InvoiceNote(causale.getText());
                            bg0001.getBT0022InvoiceNote().add(invoiceNote);
                        }
                        invoice.getBG0001InvoiceNote().add(bg0001);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0001(document, cenInvoice, errors);
    }
}