package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
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
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        TypeConverter<Double, String> dblStrConverter = DoubleToStringConverter.newConverter("#0.00");

        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0022DocumentTotals().isEmpty()) {
                BG0022DocumentTotals bg0022 = cenInvoice.getBG0022DocumentTotals(0);
                Element legalMonetaryTotal = new Element("LegalMonetaryTotal");
                if (!bg0022.getBT0106SumOfInvoiceLineNetAmount().isEmpty()) {
                    BT0106SumOfInvoiceLineNetAmount bt0106 = bg0022.getBT0106SumOfInvoiceLineNetAmount(0);
                    Element lineExtensionAmount = new Element("LineExtensionAmount");
                    final Double value = bt0106.getValue();
                    try {
                        lineExtensionAmount.setText(dblStrConverter.convert(value));
                        legalMonetaryTotal.addContent(lineExtensionAmount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
                    }
                }
                if (!bg0022.getBT0109InvoiceTotalAmountWithoutVat().isEmpty()) {
                    BT0109InvoiceTotalAmountWithoutVat bt0109 = bg0022.getBT0109InvoiceTotalAmountWithoutVat(0);
                    Element taxExclusiveAmount = new Element("TaxExclusiveAmount");
                    final Double value = bt0109.getValue();
                    try {
                        taxExclusiveAmount.setText(dblStrConverter.convert(value));
                        legalMonetaryTotal.addContent(taxExclusiveAmount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
                    }
                }

                if (!bg0022.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                    BT0112InvoiceTotalAmountWithVat bt0112 = bg0022.getBT0112InvoiceTotalAmountWithVat(0);
                    Element taxInclusiveAmount = new Element("TaxInclusiveAmount");
                    final Double value = bt0112.getValue();
                    try {
                        taxInclusiveAmount.setText(dblStrConverter.convert(value));
                        legalMonetaryTotal.addContent(taxInclusiveAmount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
                    }
                }

                if (!bg0022.getBT0115AmountDueForPayment().isEmpty()) {
                    BT0115AmountDueForPayment bt0115 = bg0022.getBT0115AmountDueForPayment(0);
                    Element payableAmount = new Element("PayableAmount");
                    final Double value = bt0115.getValue();
                    try {
                        payableAmount.setText(dblStrConverter.convert(value));
                        legalMonetaryTotal.addContent(payableAmount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
                    }
                }
                if (!bg0022.getBT0114RoundingAmount().isEmpty()) {
                    BT0114RoundingAmount bt0114 = bg0022.getBT0114RoundingAmount(0);
                    Element payableRoundingAmount = new Element("PayableRoundingAmount");
                    final Double value = bt0114.getValue();
                    try {
                        payableRoundingAmount.setText(dblStrConverter.convert(value));
                        legalMonetaryTotal.addContent(payableRoundingAmount);
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
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