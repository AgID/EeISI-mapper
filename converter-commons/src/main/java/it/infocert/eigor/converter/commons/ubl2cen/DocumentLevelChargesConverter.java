package it.infocert.eigor.converter.commons.ubl2cen;


import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.math.BigDecimal;
import java.util.List;

/**
 * The Document Level Charges Custom Converter
 */
public class DocumentLevelChargesConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0021(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        final Element rootElement = document.getRootElement();
        final List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        final List<Element> allowanceCharge = findNamespaceChildren(rootElement, namespacesInScope, "AllowanceCharge");

        for (Element elemAllowance : allowanceCharge) {

            Element chargeIndicator = findNamespaceChild(elemAllowance, namespacesInScope, "ChargeIndicator");
            if (chargeIndicator != null && chargeIndicator.getText().equals("true")) {

                final BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();

                Element amount = findNamespaceChild(elemAllowance, namespacesInScope, "Amount");
                if (amount != null) {
                    final String text = amount.getText();
                    try {
                        BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(new BigDecimal(text));
                        bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element baseAmount = findNamespaceChild(elemAllowance, namespacesInScope, "BaseAmount");
                if (baseAmount != null) {
                    final String text = baseAmount.getText();
                    try {
                        BT0100DocumentLevelChargeBaseAmount bt0100 = new BT0100DocumentLevelChargeBaseAmount(new BigDecimal(text));
                        bg0021.getBT0100DocumentLevelChargeBaseAmount().add(bt0100);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element multiplierFactorNumeric = findNamespaceChild(elemAllowance, namespacesInScope, "MultiplierFactorNumeric");
                if (multiplierFactorNumeric != null) {
                    final String text = multiplierFactorNumeric.getText();
                    try {
                        BT0101DocumentLevelChargePercentage bt0101 = new BT0101DocumentLevelChargePercentage(new BigDecimal(text));
                        bg0021.getBT0101DocumentLevelChargePercentage().add(bt0101);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element taxCategory = findNamespaceChild(elemAllowance, namespacesInScope, "TaxCategory");
                if (taxCategory != null) {

                    Element id = findNamespaceChild(taxCategory, namespacesInScope, "ID");
                    if (id != null) {
                        final String text = id.getText();
                        try {
                            BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(text));
                            bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    Element percent = findNamespaceChild(taxCategory, namespacesInScope, "Percent");
                    if (percent != null) {
                        final String text = percent.getText();
                        try {
                            BT0103DocumentLevelChargeVatRate bt0103 = new BT0103DocumentLevelChargeVatRate(new BigDecimal(text));
                            bg0021.getBT0103DocumentLevelChargeVatRate().add(bt0103);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                }

                Element allowanceChargeReason = findNamespaceChild(elemAllowance, namespacesInScope, "AllowanceChargeReason");
                if (allowanceChargeReason != null) {
                    BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason(allowanceChargeReason.getText());
                    bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);
                }

                Element allowanceChargeReasonCode = findNamespaceChild(elemAllowance, namespacesInScope, "AllowanceChargeReasonCode");
                if (allowanceChargeReasonCode != null) {
                    final String text = allowanceChargeReasonCode.getText();
                    try {
                        BT0105DocumentLevelChargeReasonCode bt0105 = new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.valueOf(text));
                        bg0021.getBT0105DocumentLevelChargeReasonCode().add(bt0105);
                    } catch (IllegalArgumentException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                invoice.getBG0021DocumentLevelCharges().add(bg0021);
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0021(document, cenInvoice, errors, callingLocation);
    }
}
