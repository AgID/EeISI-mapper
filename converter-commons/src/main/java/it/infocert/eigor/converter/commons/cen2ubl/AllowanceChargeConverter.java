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
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

public class AllowanceChargeConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        TypeConverter<BigDecimal, String> bdStrConverter = BigDecimalToStringConverter.newConverter("#0.00");

        Iso4217CurrenciesFundsCodes currencyCode = null;
        if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
            currencyCode = bt0005.getValue();
        }

        Element root = document.getRootElement();
        if (root != null) {
            for (BG0020DocumentLevelAllowances bg0020 : cenInvoice.getBG0020DocumentLevelAllowances()) {

                Element allowanceCharge = new Element("AllowanceCharge");
                allowanceCharge.addContent(new Element("ChargeIndicator").setText("false"));

                if (!bg0020.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                    BT0098DocumentLevelAllowanceReasonCode bt0098 = bg0020.getBT0098DocumentLevelAllowanceReasonCode(0);
                    Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
                    String value = String.valueOf(bt0098.getValue().getCode());
                    allowanceChargeReasonCode.setText(value);
                    allowanceCharge.addContent(allowanceChargeReasonCode);
                }

                Element allowanceChargeReason = new Element("AllowanceChargeReason");
                if (!bg0020.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                    BT0097DocumentLevelAllowanceReason bt0097 = bg0020.getBT0097DocumentLevelAllowanceReason(0);
                    allowanceChargeReason.setText(bt0097.getValue());
                } else {
                    allowanceChargeReason.setText("Sconto documento");
                }
                allowanceCharge.addContent(allowanceChargeReason);

                if (!bg0020.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                    BT0094DocumentLevelAllowancePercentage bt0094 = bg0020.getBT0094DocumentLevelAllowancePercentage(0);
                    Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                    try {
                        multiplierFactorNumeric.setText(bdStrConverter.convert(bt0094.getValue()));
                        allowanceCharge.addContent(multiplierFactorNumeric);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0094.toString())
                        ));
                    }
                }

                if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                    BT0092DocumentLevelAllowanceAmount bt0092 = bg0020.getBT0092DocumentLevelAllowanceAmount(0);
                    Element amount = new Element("Amount");
                    try {
                        amount.setText(bdStrConverter.convert(bt0092.getValue()));
                        if (currencyCode != null) {
                            amount.setAttribute("currencyID", currencyCode.getCode());
                        }
                        allowanceCharge.addContent(amount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0092.toString())
                        ));
                    }
                }

                if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                    BT0093DocumentLevelAllowanceBaseAmount bt0093 = bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0);
                    Element baseAmount = new Element("BaseAmount");
                    try {
                        baseAmount.setText(bdStrConverter.convert(bt0093.getValue()));
                        if (currencyCode != null) {

                            baseAmount.setAttribute("currencyID", currencyCode.getCode());
                        }
                        allowanceCharge.addContent(baseAmount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, bt0093.toString())
                        ));
                    }
                }

                Element taxCategory = new Element("TaxCategory");

                if (!bg0020.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                    BigDecimal percentValue = bg0020.getBT0096DocumentLevelAllowanceVatRate(0).getValue();

                    Element id = new Element("ID");
                    if (!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                        BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0);
                        id.setText(bt0095.getValue().name());
                    } else if (BigDecimal.ZERO.compareTo(percentValue) == 0) {
                        id.setText(Untdid5305DutyTaxFeeCategories.Z.name());
                    } else {
                        id.setText(Untdid5305DutyTaxFeeCategories.S.name());
                    }
                    taxCategory.addContent(id);

                    Element percent = new Element("Percent");
                    try {
                        percent.setText(bdStrConverter.convert(percentValue));
                        taxCategory.addContent(percent);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, percentValue.toString())
                        ));
                    }
                } else if (!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                    BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0);
                    Element id = new Element("ID");
                    taxCategory.addContent(id);
                    id.setText(bt0095.getValue().name());

                    if (Untdid5305DutyTaxFeeCategories.E.equals(bt0095.getValue())) {
                        Element percent = new Element("Percent");
                        percent.setText("0.00");
                        taxCategory.addContent(percent);
                    }
                }
                Element taxScheme = new Element("TaxScheme").addContent(new Element("ID").setText("VAT"));
                taxCategory.addContent(taxScheme);
                allowanceCharge.addContent(taxCategory);
                root.addContent(allowanceCharge);
            }


            for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {

                Element allowanceCharge = new Element("AllowanceCharge");
                allowanceCharge.addContent(new Element("ChargeIndicator").setText("true"));

                if (!bg0021.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                    BT0105DocumentLevelChargeReasonCode bt0105 = bg0021.getBT0105DocumentLevelChargeReasonCode(0);
                    Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
                    allowanceChargeReasonCode.setText(bt0105.getValue().name());
                    allowanceCharge.addContent(allowanceChargeReasonCode);
                }

                Element allowanceChargeReason = new Element("AllowanceChargeReason");
                if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
                    BT0104DocumentLevelChargeReason bt0104 = bg0021.getBT0104DocumentLevelChargeReason(0);
                    allowanceChargeReason.setText(bt0104.getValue());
                } else {
                    allowanceChargeReason.setText("Maggiorazione documento");
                }
                allowanceCharge.addContent(allowanceChargeReason);

                if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                    BT0101DocumentLevelChargePercentage bt0101 = bg0021.getBT0101DocumentLevelChargePercentage(0);
                    Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                    try {
                        multiplierFactorNumeric.setText(bdStrConverter.convert(bt0101.getValue()));
                        allowanceCharge.addContent(multiplierFactorNumeric);
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
                }

                if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
                    Element amount = new Element("Amount");
                    try {
                        amount.setText(bdStrConverter.convert(bt0099.getValue()));
                        if (currencyCode != null) {
                            amount.setAttribute("currencyID", currencyCode.getCode());
                        }
                        allowanceCharge.addContent(amount);
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
                }

                // <xsd:element ref="cbc:BaseAmount" minOccurs="0" maxOccurs="1">
                if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    BT0100DocumentLevelChargeBaseAmount bt0100 = bg0021.getBT0100DocumentLevelChargeBaseAmount(0);
                    Element baseAmount = new Element("BaseAmount");
                    try {
                        baseAmount.setText(bdStrConverter.convert(bt0100.getValue()));
                        if (currencyCode != null) {
                            baseAmount.setAttribute("currencyID", currencyCode.getCode());
                        }
                        allowanceCharge.addContent(baseAmount);
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
                }


                Element taxCategory = new Element("TaxCategory");

                if (!bg0021.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
                    BigDecimal percentValue = bg0021.getBT0103DocumentLevelChargeVatRate(0).getValue();

                    Element id = new Element("ID");

                    if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                        BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
                        id.setText(bt0102.getValue().name());
                    } else if (BigDecimal.ZERO.compareTo(percentValue) == 0) {
                        id.setText(Untdid5305DutyTaxFeeCategories.Z.name());
                    } else {
                        id.setText(Untdid5305DutyTaxFeeCategories.S.name());
                    }
                    taxCategory.addContent(id);

                    Element percent = new Element("Percent");
                    try {
                        percent.setText(bdStrConverter.convert(percentValue));
                        taxCategory.addContent(percent);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, percentValue.toString())
                        ));
                    }
                } else if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
                    Element id = new Element("ID");
                    id.setText(bt0102.getValue().name());
                    taxCategory.addContent(id);

                    if (Untdid5305DutyTaxFeeCategories.E.equals(bt0102.getValue())) {
                        Element percent = new Element("Percent");
                        percent.setText("0.00");
                        taxCategory.addContent(percent);
                    }
                }
                Element taxScheme = new Element("TaxScheme").addContent(new Element("ID").setText("VAT"));
                taxCategory.addContent(taxScheme);
                allowanceCharge.addContent(taxCategory);

                root.addContent(allowanceCharge);
            }
        }
    }
}
