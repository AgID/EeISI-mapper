package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Optional;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0112InvoiceTotalAmountWithVat;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DocumentTotalsConverter implements CustomMapping<Document> {
    private final static Logger log = LoggerFactory.getLogger(DocumentTotalsConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        addInvoiceTotalAmountWithVatDefault(invoice, document, errors);
    }

    private void addInvoiceTotalAmountWithVatDefault(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            if (datiGenerali != null) {
                List<Element> datiGeneraliDocumenti = datiGenerali.getChildren();
                for (Element datiGeneraliDocumento : datiGeneraliDocumenti) {
                    if (datiGeneraliDocumento.getName().equals("DatiGeneraliDocumento")) {
                        Element importoTotaleDocumento = datiGeneraliDocumento.getChild("ImportoTotaleDocumento");
                        if (importoTotaleDocumento == null) {
                            final Element datiBeniServizi = fatturaElettronicaBody.getChild("DatiBeniServizi");
                            if (datiBeniServizi != null) {
                                final Element datiRiepilogo = datiBeniServizi.getChild("DatiRiepilogo");
                                if (datiRiepilogo != null) {
                                    final Element imponibileImporto = Optional.fromNullable(datiRiepilogo.getChild("ImponibileImporto")).or(new Element("ImponibileImport").setText(""));
                                    final Element imposta = Optional.fromNullable(datiRiepilogo.getChild("Imposta")).or(new Element("Imposta").setText(""));
                                    BG0022DocumentTotals totals;
                                    if (invoice.getBG0022DocumentTotals().isEmpty()) {
                                        totals = new BG0022DocumentTotals();
                                        invoice.getBG0022DocumentTotals().add(totals);
                                    } else {
                                        totals = invoice.getBG0022DocumentTotals(0);
                                    }
                                    final double imponibileD = Double.parseDouble(!"".equals(imponibileImporto.getText()) ? imponibileImporto.getText() : "0");
                                    final double impostaD = Double.parseDouble(!"".equals(imposta.getText()) ? imposta.getText() : "0");
                                    totals.getBT0112InvoiceTotalAmountWithVat().add(new BT0112InvoiceTotalAmountWithVat(imponibileD + impostaD));
                                }
                            }
                        } else {
                            log.error("ImportoTotaleDocumento [BT-112] isn't present but ImponibileImporto [BT-109] and Imposta [BT-110] (used to calculate the default value)" +
                                    "are missing too.");
                        }
                    }
                }
            }
        }
    }
}
