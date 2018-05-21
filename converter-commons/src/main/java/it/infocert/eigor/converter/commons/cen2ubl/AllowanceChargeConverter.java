package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.BigDecimalToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

public class AllowanceChargeConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        TypeConverter<BigDecimal, String> dblStrConverter = BigDecimalToStringConverter.newConverter("#0.00");

        Iso4217CurrenciesFundsCodes currencyCode = null;
        if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
            currencyCode = bt0005.getValue();
        }

        Element root = document.getRootElement();
        if (root != null) {
            for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {

                Element allowanceCharge = new Element("AllowanceCharge");

                // <xsd:element ref="cbc:ID" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:ChargeIndicator" minOccurs="1" maxOccurs="1">
                Element chargeIndicator = new Element("ChargeIndicator");
                chargeIndicator.setText("true");
                allowanceCharge.addContent(chargeIndicator);

                // <xsd:element ref="cbc:AllowanceChargeReasonCode" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:AllowanceChargeReason" minOccurs="0" maxOccurs="unbounded">
                if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
                    BT0104DocumentLevelChargeReason bt0104 = bg0021.getBT0104DocumentLevelChargeReason(0);
                    Element allowanceChargeReason = new Element("AllowanceChargeReason");
                    allowanceChargeReason.setText(bt0104.getValue());
                    allowanceCharge.addContent(allowanceChargeReason);
                }

                // <xsd:element ref="cbc:MultiplierFactorNumeric" minOccurs="0" maxOccurs="1">
                if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                    BT0101DocumentLevelChargePercentage bt0101 = bg0021.getBT0101DocumentLevelChargePercentage(0);
                    Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                    try {
                        multiplierFactorNumeric.setText(dblStrConverter.convert(bt0101.getValue()));
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0101.toString())
                        ));
                    }
                    allowanceCharge.addContent(multiplierFactorNumeric);
                }

                // <xsd:element ref="cbc:PrepaidIndicator" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:SequenceNumeric" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:Amount" minOccurs="1" maxOccurs="1">
                if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
                    Element amount = new Element("Amount");
                    try {
                        amount.setText(dblStrConverter.convert(bt0099.getValue()));
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0099.toString())
                        ));
                    }
                    amount.setAttribute("currencyID", currencyCode.getCode());
                    allowanceCharge.addContent(amount);
                }

                // <xsd:element ref="cbc:BaseAmount" minOccurs="0" maxOccurs="1">
                if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    BT0100DocumentLevelChargeBaseAmount bt0100 = bg0021.getBT0100DocumentLevelChargeBaseAmount(0);
                    Element baseAmount = new Element("BaseAmount");
                    try {
                        baseAmount.setText(dblStrConverter.convert(bt0100.getValue()));
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0100.toString())
                        ));
                    }
                    baseAmount.setAttribute("currencyID", currencyCode.getCode());
                    allowanceCharge.addContent(baseAmount);
                }

                // <xsd:element ref="cbc:AccountingCostCode" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:AccountingCost" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cbc:PerUnitAmount" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cac:TaxCategory" minOccurs="0" maxOccurs="unbounded">
                if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
                    Element taxCategory = new Element("TaxCategory");

                    // <xsd:element ref="cbc:ID" minOccurs="0" maxOccurs="1">
                    Element id = new Element("ID");
                    taxCategory.addContent(id);
                    id.setText(bt0102.getValue().name());

                    // <xsd:element ref="cbc:Name" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cbc:Percent" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cbc:BaseUnitMeasure" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cbc:PerUnitAmount" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cbc:TaxExemptionReasonCode" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cbc:TaxExemptionReason" minOccurs="0" maxOccurs="unbounded">
                    // not used

                    // <xsd:element ref="cbc:TierRange" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cbc:TierRatePercent" minOccurs="0" maxOccurs="1">
                    // not used

                    // <xsd:element ref="cac:TaxScheme" minOccurs="1" maxOccurs="1">
                    Element taxScheme = new Element("TaxScheme").addContent(new Element("ID").setText("VAT"));
                    taxCategory.addContent(taxScheme);

                    allowanceCharge.addContent(taxCategory);
                }

                // <xsd:element ref="cac:TaxTotal" minOccurs="0" maxOccurs="1">
                // not used

                // <xsd:element ref="cac:PaymentMeans" minOccurs="0" maxOccurs="unbounded">
                // not used

                root.addContent(allowanceCharge);
            }


        }
    }
}