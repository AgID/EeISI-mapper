package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DocumentTotalsConverter implements CustomMapping<Document> {
    private final static Logger log = LoggerFactory.getLogger(DocumentTotalsConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        addInvoiceTotalAmountWithVatDefault(invoice, document, errors);
    }

    private void addInvoiceTotalAmountWithVatDefault(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {
        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            BG0022DocumentTotals totals;
            if (invoice.getBG0022DocumentTotals().isEmpty()) {
                totals = new BG0022DocumentTotals();
                invoice.getBG0022DocumentTotals().add(totals);
            } else {
                totals = invoice.getBG0022DocumentTotals(0);
            }
            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
            final List<BT0112InvoiceTotalAmountWithVat> amountsWithVat = totals.getBT0112InvoiceTotalAmountWithVat();
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
                                    final Optional<Element> imponibileImporto = Optional.fromNullable(datiRiepilogo.getChild("ImponibileImporto"));
                                    final Optional<Element> imposta = Optional.fromNullable(datiRiepilogo.getChild("Imposta"));
                                    final Function<Element, Double> function = new Function<Element, Double>() {
                                        @Override
                                        public Double apply(Element input) {
                                            return Double.parseDouble(input.getText());
                                        }
                                    };

                                    final Double imponibileD = imponibileImporto.transform(function).or(0d);
                                    final Double impostaD = imposta.transform(function).or(0d);
                                    amountsWithVat.add(new BT0112InvoiceTotalAmountWithVat(imponibileD + impostaD));
                                }
                            }
                        } else {
                            log.error("ImportoTotaleDocumento [BT-112] isn't present but ImponibileImporto [BT-109] and Imposta [BT-110] (used to calculate the default value)" +
                                    "are missing too.");
                        }
                    }
                }
            }

            final Element datiPagamento = fatturaElettronicaBody.getChild("DatiPagamento");
            if (datiPagamento != null) {
                final Element dettaglioPagamento = datiPagamento.getChild("DettaglioPagamento");
                if (dettaglioPagamento != null) {
                    final Element importoPagamento = dettaglioPagamento.getChild("ImportoPagamento");
                    if (!totals.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                        final Double amountWithVat = totals.getBT0112InvoiceTotalAmountWithVat(0).getValue();
                        if (importoPagamento != null) {
                            final Double importoD = Double.valueOf(importoPagamento.getText());

                            totals.getBT0113PaidAmount().add(new BT0113PaidAmount(amountWithVat - importoD));
                            totals.getBT0115AmountDueForPayment().add(new BT0115AmountDueForPayment(importoD));
                        } else {
                            final List<BT0113PaidAmount> paidAmounts = totals.getBT0113PaidAmount();
                            final List<BT0114RoundingAmount> roundingAmounts = totals.getBT0114RoundingAmount();
                            if (!amountsWithVat.isEmpty() && !paidAmounts.isEmpty() && !roundingAmounts.isEmpty()) {
                                final Double bt113 = totals.getBT0113PaidAmount(0).getValue();
                                final Double bt114 = totals.getBT0114RoundingAmount(0).getValue();
                                totals.getBT0115AmountDueForPayment().add(new BT0115AmountDueForPayment(amountWithVat + bt113 - bt114));
                            } else {
                                log.debug("One of [BT-112], [BT-113] or [BT-114] is missing. BT-112: {}, BT-113: {}, BT-114: {}", amountsWithVat.size(), paidAmounts.size(), roundingAmounts.size());
                            }
                        }
                    }

                }
            }
        }
    }
}
