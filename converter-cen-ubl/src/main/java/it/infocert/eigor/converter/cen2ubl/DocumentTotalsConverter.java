package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DocumentTotalsConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(DocumentTotalsConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        DoubleToStringConverter dblStrConverter = new DoubleToStringConverter("#.00");

        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0022DocumentTotals().isEmpty()) {
                BG0022DocumentTotals bg0022 = cenInvoice.getBG0022DocumentTotals(0);
                if (bg0022 != null) {
                    Element legalMonetaryTotal = new Element("LegalMonetaryTotal");
                    if (!bg0022.getBT0106SumOfInvoiceLineNetAmount().isEmpty()) {
                        BT0106SumOfInvoiceLineNetAmount bt0106 = bg0022.getBT0106SumOfInvoiceLineNetAmount(0);
                        if (bt0106 != null) {
                            Element lineExtensionAmount = new Element("LineExtensionAmount");
                            lineExtensionAmount.addContent(dblStrConverter.convert(bt0106.getValue()));
                            legalMonetaryTotal.addContent(lineExtensionAmount);
                        }
                    }
                    if (!bg0022.getBT0109InvoiceTotalAmountWithoutVat().isEmpty()) {
                        BT0109InvoiceTotalAmountWithoutVat bt0109 = bg0022.getBT0109InvoiceTotalAmountWithoutVat(0);
                        if (bt0109 != null) {
                            Element taxExclusiveAmount = new Element("TaxExclusiveAmount");
                            taxExclusiveAmount.addContent(dblStrConverter.convert(bt0109.getValue()));
                            legalMonetaryTotal.addContent(taxExclusiveAmount);
                        }
                    }

                    if (!bg0022.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                        BT0112InvoiceTotalAmountWithVat bt0112 = bg0022.getBT0112InvoiceTotalAmountWithVat(0);
                        if (bt0112 != null) {
                            Element taxInclusiveAmount = new Element("TaxInclusiveAmount");
                            taxInclusiveAmount.addContent(dblStrConverter.convert(bt0112.getValue()));
                            legalMonetaryTotal.addContent(taxInclusiveAmount);
                        }
                    }
                    if (!bg0022.getBT0115AmountDueForPayment().isEmpty()) {
                        BT0115AmountDueForPayment bt0115 = bg0022.getBT0115AmountDueForPayment(0);
                        if (bt0115 != null) {
                            Element payableAmount = new Element("PayableAmount");
                            payableAmount.addContent(dblStrConverter.convert(bt0115.getValue()));
                            legalMonetaryTotal.addContent(payableAmount);
                        }
                    }
                    if (!bg0022.getBT0114RoundingAmount().isEmpty()) {
                        BT0114RoundingAmount bt0114 = bg0022.getBT0114RoundingAmount(0);
                        if (bt0114 != null) {
                            Element payableRoundingAmount = new Element("PayableRoundingAmount");
                            payableRoundingAmount.addContent(dblStrConverter.convert(bt0114.getValue()));
                            legalMonetaryTotal.addContent(payableRoundingAmount);
                        }
                    }

                    if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
                        BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
                        Iso4217CurrenciesFundsCodes currencyCode = bt0005.getValue();

                        for (Element element : legalMonetaryTotal.getChildren()) {
                            element.setAttribute(new Attribute("currencyID", currencyCode.name()));
                        }
                    }

                    root.addContent(legalMonetaryTotal);
                }

            }
        }
    }
}