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

                if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                    BT0092DocumentLevelAllowanceAmount bt0092 = bg0020.getBT0092DocumentLevelAllowanceAmount(0);
                    Element amount = new Element("Amount");
                    try {
                        amount.setText(bdStrConverter.convert(bt0092.getValue()));
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
                    amount.setAttribute("currencyID", currencyCode.getCode());
                    allowanceCharge.addContent(amount);
                }

                if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                    BT0093DocumentLevelAllowanceBaseAmount bt0093 = bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0);
                    Element baseAmount = new Element("BaseAmount");
                    try {
                        baseAmount.setText(bdStrConverter.convert(bt0093.getValue()));
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
                    baseAmount.setAttribute("currencyID", currencyCode.getCode());
                    allowanceCharge.addContent(baseAmount);
                }

                if (!bg0020.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                    BT0094DocumentLevelAllowancePercentage bt0094 = bg0020.getBT0094DocumentLevelAllowancePercentage(0);
                    Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                    try {
                        multiplierFactorNumeric.setText(bdStrConverter.convert(bt0094.getValue()));
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
                    allowanceCharge.addContent(multiplierFactorNumeric);
                }

                if(!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()){
                    BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0);
                    Element taxCategory = new Element("TaxCategory");
                    Element id = new Element("ID");
                    taxCategory.addContent(id);
                    id.setText(bt0095.getValue().name());
                    allowanceCharge.addContent(taxCategory);
                }

//                if(!bg0020.getBT0096DocumentLevelAllowanceVatRate().isEmpty()){
//                    BT0096DocumentLevelAllowanceVatRate bt0096 = bg0020.getBT0096DocumentLevelAllowanceVatRate(0);
//                }

                if(!bg0020.getBT0097DocumentLevelAllowanceReason().isEmpty()){
                    BT0097DocumentLevelAllowanceReason bt0097 = bg0020.getBT0097DocumentLevelAllowanceReason(0);
                    Element allowanceChargeReason = new Element("AllowanceChargeReason");
                    allowanceChargeReason.setText(bt0097.getValue());
                    allowanceCharge.addContent(allowanceChargeReason);
                }

//                if(!bg0020.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()){
//                    BT0098DocumentLevelAllowanceReasonCode bt0098 = bg0020.getBT0098DocumentLevelAllowanceReasonCode(0);
//                }

                root.addContent(allowanceCharge);

            }


            for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {

                Element allowanceCharge = new Element("AllowanceCharge");
                allowanceCharge.addContent(new Element("ChargeIndicator").setText("true"));

                if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
                    Element amount = new Element("Amount");
                    try {
                        amount.setText(bdStrConverter.convert(bt0099.getValue()));
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

                if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    BT0100DocumentLevelChargeBaseAmount bt0100 = bg0021.getBT0100DocumentLevelChargeBaseAmount(0);
                    Element baseAmount = new Element("BaseAmount");
                    try {
                        baseAmount.setText(bdStrConverter.convert(bt0100.getValue()));
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

                if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                    BT0101DocumentLevelChargePercentage bt0101 = bg0021.getBT0101DocumentLevelChargePercentage(0);
                    Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                    try {
                        multiplierFactorNumeric.setText(bdStrConverter.convert(bt0101.getValue()));
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


        }
    }
}