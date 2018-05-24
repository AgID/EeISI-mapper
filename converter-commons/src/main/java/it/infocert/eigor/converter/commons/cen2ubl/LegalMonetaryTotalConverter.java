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

                final BigDecimal bt106SumOfInvoiceLineNetAmounts = getBT106SumOfInvoiceLineNetAmounts(cenInvoice);
                final BigDecimal bt107SumOfAllowances = getBT107SumOfAllowances(cenInvoice);
                final BigDecimal bt108SumOfCharges = getBT108SumOfCharges(cenInvoice);
                final BigDecimal bt109TaxExclusiveAmount = bt106SumOfInvoiceLineNetAmounts.subtract(bt107SumOfAllowances).add(bt108SumOfCharges);
                final BigDecimal bt110TotalTaxAmount = getBT110TotalTaxAmount(cenInvoice);
                final BigDecimal bt112TaxInclusiveAmount = bt109TaxExclusiveAmount.add(bt110TotalTaxAmount);
                final BigDecimal bt0113PrepaidAmount = bg0022.getBT0113PaidAmount().isEmpty() ? BigDecimal.ZERO : bg0022.getBT0113PaidAmount(0).getValue();
                final BigDecimal bt0114RoundingAmount = bg0022.getBT0114RoundingAmount().isEmpty() ? BigDecimal.ZERO : bg0022.getBT0114RoundingAmount(0).getValue();
                final BigDecimal bt0115PayableAmount = bt112TaxInclusiveAmount.subtract(bt0113PrepaidAmount).add(bt0114RoundingAmount);

                Element lineExtensionAmount = new Element("LineExtensionAmount");
                lineExtensionAmount.setText(bt106SumOfInvoiceLineNetAmounts
                        .setScale(2, RoundingMode.HALF_UP).toString());
                legalMonetaryTotal.addContent(lineExtensionAmount);

                Element taxExclusiveAmount = new Element("TaxExclusiveAmount");
                taxExclusiveAmount.setText(bt109TaxExclusiveAmount.setScale(2, RoundingMode.HALF_UP).toString());
                legalMonetaryTotal.addContent(taxExclusiveAmount);

                Element taxInclusiveAmount = new Element("TaxInclusiveAmount");
                taxInclusiveAmount.setText(bt112TaxInclusiveAmount.setScale(2, RoundingMode.HALF_UP).toString());
                legalMonetaryTotal.addContent(taxInclusiveAmount);

                if (BigDecimal.ZERO.compareTo(bt107SumOfAllowances) != 0) {
                    Element allowanceTotalAmount = new Element("AllowanceTotalAmount");
                    allowanceTotalAmount.setText(bt107SumOfAllowances.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(allowanceTotalAmount);
                }

                if (BigDecimal.ZERO.compareTo(bt108SumOfCharges) != 0) {
                    Element chargeTotalAmount = new Element("ChargeTotalAmount");
                    chargeTotalAmount.setText(bt108SumOfCharges.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(chargeTotalAmount);
                }

                Element prepaidAmount = new Element("PrepaidAmount");
                prepaidAmount.setText(bt0113PrepaidAmount.setScale(2, RoundingMode.HALF_UP).toString());
                legalMonetaryTotal.addContent(prepaidAmount);

                Element payableRoundingAmount = new Element("PayableRoundingAmount");
                payableRoundingAmount.setText(bt0114RoundingAmount.setScale(2, RoundingMode.HALF_UP).toString());
                legalMonetaryTotal.addContent(payableRoundingAmount);

                Element payableAmount = new Element("PayableAmount");
                payableAmount.setText(bt0115PayableAmount.setScale(2, RoundingMode.HALF_UP).toString());
                legalMonetaryTotal.addContent(payableAmount);

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

    private BigDecimal getBT106SumOfInvoiceLineNetAmounts(BG0000Invoice invoice) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BG0025InvoiceLine bg0025 : invoice.getBG0025InvoiceLine()) {
            if (!bg0025.getBT0131InvoiceLineNetAmount().isEmpty()) {
                BT0131InvoiceLineNetAmount bt0131 = bg0025.getBT0131InvoiceLineNetAmount(0);
                sum = sum.add(bt0131.getValue());
            }
        }
        return sum;
    }

    private BigDecimal getBT107SumOfAllowances(BG0000Invoice invoice) {
        BigDecimal sumOfAllowances = BigDecimal.ZERO;
        for (BG0020DocumentLevelAllowances bg0020 : invoice.getBG0020DocumentLevelAllowances()) {
            if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                BT0092DocumentLevelAllowanceAmount bt0092 = bg0020.getBT0092DocumentLevelAllowanceAmount(0);
                sumOfAllowances = sumOfAllowances.add(bt0092.getValue());
            }
        }
        return sumOfAllowances;
    }

    private BigDecimal getBT108SumOfCharges(BG0000Invoice invoice) {
        BigDecimal sumOfCharges = BigDecimal.ZERO;
        for (BG0021DocumentLevelCharges bg0021 : invoice.getBG0021DocumentLevelCharges()) {
            if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
                sumOfCharges = sumOfCharges.add(bt0099.getValue());
            }
        }
        return sumOfCharges;
    }

    private BigDecimal getBT110TotalTaxAmount(BG0000Invoice cenInvoice) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BG0023VatBreakdown bg0023 : cenInvoice.getBG0023VatBreakdown()) {
            if (!bg0023.getBT0117VatCategoryTaxAmount().isEmpty()) {
                BT0117VatCategoryTaxAmount bt0117 = bg0023.getBT0117VatCategoryTaxAmount(0);
                sum = sum.add(bt0117.getValue());
            }
        }
        return sum;
    }
}