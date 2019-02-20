package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class VATBreakdownConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(VATBreakdownConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        Element root = document.getRootElement();
        if(root == null) return;


        Iso4217CurrenciesFundsCodes invoiceCur = evalExpression(() -> invoice.getBT0005InvoiceCurrencyCode(0).getValue());
        Iso4217CurrenciesFundsCodes vatCur = evalExpression(() -> invoice.getBT0006VatAccountingCurrencyCode(0).getValue());
        Iso4217CurrenciesFundsCodes currencyForBt111 = invoiceCur.equals( vatCur ) && vatCur!=null ? invoiceCur : vatCur;


        BG0022DocumentTotals documentTotals = evalExpression( () -> invoice.getBG0022DocumentTotals(0) );

        Element taxTotal = null;
        if (documentTotals!=null) {

            String amount = null;
            String currencyId = null;
            BigDecimal value = null;

            // if the invoice has an accounting currency (BT-06) we should add a TaxTotal element with just the total expressed with that currency.
            if (invoice.hasBT0006VatAccountingCurrencyCode() && documentTotals.hasBT0111InvoiceTotalVatAmountInAccountingCurrency()) {

                taxTotal = new Element("TaxTotal");
                root.addContent( taxTotal );

                value = documentTotals.getBT0111InvoiceTotalVatAmountInAccountingCurrency(0).getValue();
                amount = value.setScale(2, RoundingMode.HALF_UP).toString();
                currencyId = invoice.getBT0006VatAccountingCurrencyCode(0).getValue().name();
                if (amount != null) {
                    taxTotal.addContent( newTaxAmountElement(root, amount, currencyId) );
                }

            }

            // this should be the usual vat
            if (invoice.hasBT0005InvoiceCurrencyCode() && documentTotals.hasBT0110InvoiceTotalVatAmount() ) {

                taxTotal = new Element("TaxTotal");
                root.addContent( taxTotal );

                value = documentTotals.getBT0110InvoiceTotalVatAmount(0).getValue();
                amount = value.setScale(2, RoundingMode.HALF_UP).toString();
                currencyId = invoice.getBT0005InvoiceCurrencyCode(0).getValue().name();
                if (amount != null) {
                    taxTotal.addContent( newTaxAmountElement(root, amount, currencyId) );
                }

            }

        }

        if(taxTotal == null) return;

        List<BG0023VatBreakdown> bg0023 = invoice.getBG0023VatBreakdown();
        for (BG0023VatBreakdown elemBg23 : bg0023) {

            BT0116VatCategoryTaxableAmount bt0116 = evalExpression( ()->elemBg23.getBT0116VatCategoryTaxableAmount(0) );
            BT0117VatCategoryTaxAmount bt0117 = evalExpression( ()->elemBg23.getBT0117VatCategoryTaxAmount(0) );
            BT0118VatCategoryCode bt0118 = evalExpression( ()->elemBg23.getBT0118VatCategoryCode(0) );
            BT0119VatCategoryRate bt0119 = evalExpression( ()-> elemBg23.getBT0119VatCategoryRate(0) );

            Element taxSubtotal = new Element("TaxSubtotal");
            taxTotal.addContent(taxSubtotal);

            if (bt0116 != null) {
                Element taxableAmount = new Element("TaxableAmount");
                final BigDecimal value = bt0116.getValue();
                taxableAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                taxSubtotal.addContent(taxableAmount);
            }
            if (bt0117 != null) {
                Element taxAmount = new Element("TaxAmount");
                final BigDecimal value = bt0117.getValue();
                taxAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                taxSubtotal.addContent(taxAmount);
            }

            Element taxCategory = new Element("TaxCategory");
            taxSubtotal.addContent(taxCategory);

            // <xsd:element ref="cbc:ID" minOccurs="0" maxOccurs="1">
            if (bt0118 != null) {
                Element id = new Element("ID");
                Untdid5305DutyTaxFeeCategories dutyTaxFeeCategories = bt0118.getValue();
                id.setText(dutyTaxFeeCategories.name());
                taxCategory.addContent(id);
            }

            // <xsd:element ref="cbc:Name" minOccurs="0" maxOccurs="1">
            // not used

            // <xsd:element ref="cbc:Percent" minOccurs="0" maxOccurs="1">
            if (bt0119 != null) {
                Element percent = new Element("Percent");
                final BigDecimal value = bt0119.getValue();
                percent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                taxCategory.addContent(percent);
            }

            // <xsd:element ref="cbc:BaseUnitMeasure" minOccurs="0" maxOccurs="1">
            // not used

            // <xsd:element ref="cbc:PerUnitAmount" minOccurs="0" maxOccurs="1">
            // not used

            if (!elemBg23.getBT0121VatExemptionReasonCode().isEmpty()) {
                BT0121VatExemptionReasonCode bt0121 = elemBg23.getBT0121VatExemptionReasonCode(0);
                Element taxExemptionReasonCode = new Element("TaxExemptionReasonCode");
                taxExemptionReasonCode.setText(bt0121.getValue());
                taxCategory.addContent(taxExemptionReasonCode);
            }

            Element taxExemptionReason = new Element("TaxExemptionReason");
            if (!elemBg23.getBT0120VatExemptionReasonText().isEmpty()) {
                BT0120VatExemptionReasonText bt0120 = elemBg23.getBT0120VatExemptionReasonText(0);
                taxExemptionReason.setText(bt0120.getValue());
                taxCategory.addContent(taxExemptionReason);
            } else if (bt0118 != null && Untdid5305DutyTaxFeeCategories.E.equals(bt0118.getValue())) {
                taxExemptionReason.setText(bt0118.getValue().getShortDescritpion());
                taxCategory.addContent(taxExemptionReason);
            }

            // <xsd:element ref="cbc:TierRange" minOccurs="0" maxOccurs="1">
            // not used

            // <xsd:element ref="cbc:TierRatePercent" minOccurs="0" maxOccurs="1">
            // not used

            // <xsd:element ref="cac:TaxScheme" minOccurs="1" maxOccurs="1">
            {
                Element taxSchemeId = new Element("ID");
                taxSchemeId.setText("VAT");
                Element taxScheme = new Element("TaxScheme");
                taxScheme.addContent(taxSchemeId);
                taxCategory.addContent(taxScheme);
            }

            if (!invoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
                BT0005InvoiceCurrencyCode bt0005 = invoice.getBT0005InvoiceCurrencyCode(0);
                Iso4217CurrenciesFundsCodes currencyCode = bt0005.getValue();

                String currencyName = currencyCode.name();
                for (Element element : taxSubtotal.getChildren()) {
                    if (element.getName().equals("TaxableAmount") || element.getName().equals("TaxAmount")) {
                            element.setAttribute(new Attribute("currencyID", currencyName));
                        }
                    }
                }
            }
        }

    private Element newTaxAmountElement(Element root, String amount, String currencyId) {

        Element taxAmount = new Element("TaxAmount");
        taxAmount.setText(amount);
        taxAmount.setAttribute("currencyID", currencyId);
        return taxAmount;
    }
}

