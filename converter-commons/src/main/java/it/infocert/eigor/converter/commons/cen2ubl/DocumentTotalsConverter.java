package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DocumentTotalsConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(DocumentTotalsConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Iso4217CurrenciesFundsCodes currencyCode = null;
        if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
            currencyCode = bt0005.getValue();
        }

        Element root = document.getRootElement();
        if (root != null) {
            for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {

                Element allowanceCharge = new Element("AllowanceCharge");

                if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
                    Element amount = new Element("Amount");
                    final BigDecimal value = bt0099.getValue();
                    amount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    amount.setAttribute("currencyID", currencyCode.getCode());
                    allowanceCharge.addContent(amount);
                }

                if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    BT0100DocumentLevelChargeBaseAmount bt0100 = bg0021.getBT0100DocumentLevelChargeBaseAmount(0);
                    Element baseAmount = new Element("BaseAmount");
                    final BigDecimal value = bt0100.getValue();
                    baseAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    baseAmount.setAttribute("currencyID", currencyCode.getCode());
                    allowanceCharge.addContent(baseAmount);
                }

                if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                    BT0101DocumentLevelChargePercentage bt0101 = bg0021.getBT0101DocumentLevelChargePercentage(0);
                    Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                    final BigDecimal value = bt0101.getValue();
                    multiplierFactorNumeric.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    allowanceCharge.addContent(multiplierFactorNumeric);
                }

                if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
                    Element taxCategory = new Element("TaxCategory");
                    Element id = new Element("ID");
                    taxCategory.addContent(id);
                    id.setText(bt0102.getValue().name());
                    allowanceCharge.addContent(taxCategory);
                }

                if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
                    BT0104DocumentLevelChargeReason bt0104 = bg0021.getBT0104DocumentLevelChargeReason(0);
                    Element allowanceChargeReason = new Element("AllowanceChargeReason");
                    allowanceChargeReason.setText(bt0104.getValue());
                    allowanceCharge.addContent(allowanceChargeReason);
                }

                root.addContent(allowanceCharge);
            }

            if (!cenInvoice.getBG0022DocumentTotals().isEmpty()) {
                BG0022DocumentTotals bg0022 = cenInvoice.getBG0022DocumentTotals(0);
                Element legalMonetaryTotal = new Element("LegalMonetaryTotal");
                if (!bg0022.getBT0106SumOfInvoiceLineNetAmount().isEmpty()) {
                    BT0106SumOfInvoiceLineNetAmount bt0106 = bg0022.getBT0106SumOfInvoiceLineNetAmount(0);
                    Element lineExtensionAmount = new Element("LineExtensionAmount");
                    final BigDecimal value = bt0106.getValue();
                    lineExtensionAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(lineExtensionAmount);
                }
                if (!bg0022.getBT0109InvoiceTotalAmountWithoutVat().isEmpty()) {
                    BT0109InvoiceTotalAmountWithoutVat bt0109 = bg0022.getBT0109InvoiceTotalAmountWithoutVat(0);
                    Element taxExclusiveAmount = new Element("TaxExclusiveAmount");
                    final BigDecimal value = bt0109.getValue();
                    taxExclusiveAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(taxExclusiveAmount);
                }

                if (!bg0022.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                    BT0112InvoiceTotalAmountWithVat bt0112 = bg0022.getBT0112InvoiceTotalAmountWithVat(0);
                    Element taxInclusiveAmount = new Element("TaxInclusiveAmount");
                    final BigDecimal value = bt0112.getValue();
                    taxInclusiveAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(taxInclusiveAmount);
                }

                if (!bg0022.getBT0115AmountDueForPayment().isEmpty()) {
                    BT0115AmountDueForPayment bt0115 = bg0022.getBT0115AmountDueForPayment(0);
                    Element payableAmount = new Element("PayableAmount");
                    final BigDecimal value = bt0115.getValue();
                    payableAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(payableAmount);
                }

                if (!bg0022.getBT0114RoundingAmount().isEmpty()) {
                    BT0114RoundingAmount bt0114 = bg0022.getBT0114RoundingAmount(0);
                    Element payableRoundingAmount = new Element("PayableRoundingAmount");
                    final BigDecimal value = bt0114.getValue();
                    payableRoundingAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                    legalMonetaryTotal.addContent(payableRoundingAmount);
                }

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