package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class LegalMonetaryTotalConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Iso4217CurrenciesFundsCodes currencyCode = null;
        if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
            currencyCode = bt0005.getValue();
        }

        Element root = document.getRootElement();
        if (root != null) {

            if (!cenInvoice.getBG0022DocumentTotals().isEmpty()) {
                BG0022DocumentTotals bg0022 = cenInvoice.getBG0022DocumentTotals(0);
                Element legalMonetaryTotal = new Element("LegalMonetaryTotal");

                // <xsd:element ref="cbc:LineExtensionAmount" minOccurs="0" maxOccurs="1">
                if (!bg0022.getBT0106SumOfInvoiceLineNetAmount().isEmpty()) {
                    BT0106SumOfInvoiceLineNetAmount bt0106 = bg0022.getBT0106SumOfInvoiceLineNetAmount(0);
                    Element lineExtensionAmount = new Element("LineExtensionAmount");
                    final BigDecimal value = bt0106.getValue();
                    lineExtensionAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(lineExtensionAmount);
                }

                // <xsd:element ref="cbc:TaxExclusiveAmount" minOccurs="0" maxOccurs="1">
                if (!bg0022.getBT0109InvoiceTotalAmountWithoutVat().isEmpty()) {
                    BT0109InvoiceTotalAmountWithoutVat bt0109 = bg0022.getBT0109InvoiceTotalAmountWithoutVat(0);
                    Element taxExclusiveAmount = new Element("TaxExclusiveAmount");
                    final BigDecimal value = bt0109.getValue();
                    taxExclusiveAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(taxExclusiveAmount);
                }

                // <xsd:element ref="cbc:TaxInclusiveAmount" minOccurs="0" maxOccurs="1">
                if (!bg0022.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                    BT0112InvoiceTotalAmountWithVat bt0112 = bg0022.getBT0112InvoiceTotalAmountWithVat(0);
                    Element taxInclusiveAmount = new Element("TaxInclusiveAmount");
                    final BigDecimal value = bt0112.getValue();
                    taxInclusiveAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(taxInclusiveAmount);
                }

                // <xsd:element ref="cbc:AllowanceTotalAmount" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:ChargeTotalAmount" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:PrepaidAmount" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:PayableRoundingAmount" minOccurs="0" maxOccurs="1">
                if (!bg0022.getBT0114RoundingAmount().isEmpty()) {
                    BT0114RoundingAmount bt0114 = bg0022.getBT0114RoundingAmount(0);
                    Element payableRoundingAmount = new Element("PayableRoundingAmount");
                    final BigDecimal value = bt0114.getValue();
                    payableRoundingAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(payableRoundingAmount);
                }

                // <xsd:element ref="cbc:PayableAmount" minOccurs="1" maxOccurs="1">
                if (!bg0022.getBT0115AmountDueForPayment().isEmpty()) {
                    BT0115AmountDueForPayment bt0115 = bg0022.getBT0115AmountDueForPayment(0);
                    Element payableAmount = new Element("PayableAmount");
                    final BigDecimal value = bt0115.getValue();
                    payableAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(payableAmount);
                }

                // <xsd:element ref="cbc:PayableAlternativeAmount" minOccurs="0" maxOccurs="1">
                // not used

                if (currencyCode != null) {
                    for (Element element : legalMonetaryTotal.getChildren()) {
                        element.setAttribute(new Attribute("currencyID", currencyCode.name()));
                    }
                }

                root.addContent(legalMonetaryTotal);
            }
        }
    }
}