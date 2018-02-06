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
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VATBreakdownConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(VATBreakdownConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        TypeConverter<Double, String> dblStrConverter = DoubleToStringConverter.newConverter("#0.00");

        Element root = document.getRootElement();
        if (root != null) {

            if (!invoice.getBG0022DocumentTotals().isEmpty()) {
                BG0022DocumentTotals documentTotals = invoice.getBG0022DocumentTotals(0);
                String amount = null;
                String currencyId = null;

                Double value = null;
                try {
                    if (!invoice.getBT0006VatAccountingCurrencyCode().isEmpty() && !documentTotals.getBT0111InvoiceTotalVatAmountInAccountingCurrency().isEmpty()) {
                        value = documentTotals.getBT0111InvoiceTotalVatAmountInAccountingCurrency(0).getValue();
                        amount = dblStrConverter.convert(value);
                        currencyId = invoice.getBT0006VatAccountingCurrencyCode(0).getValue().name();
                    } else if (!invoice.getBT0005InvoiceCurrencyCode().isEmpty() && !documentTotals.getBT0110InvoiceTotalVatAmount().isEmpty()) {
                        value = documentTotals.getBT0110InvoiceTotalVatAmount(0).getValue();
                        amount = dblStrConverter.convert(value);
                        currencyId = invoice.getBT0005InvoiceCurrencyCode(0).getValue().name();
                    }

                    if (amount != null) {
                        Element taxTotal = root.getChild("TaxTotal");
                        if (taxTotal == null) {
                            taxTotal = new Element("TaxTotal");
                            root.addContent(taxTotal);
                        }
                        Element taxAmount = new Element("TaxAmount");
                        taxAmount.setText(amount);
                        taxAmount.setAttribute("currencyID", currencyId);
                        taxTotal.addContent(taxAmount);
                    }
                } catch (ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(
                            e,
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.ILLEGAL_VALUE,
                            Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                            value != null ? Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString()) : null
                    ));
                }
            }

            List<BG0023VatBreakdown> bg0023 = invoice.getBG0023VatBreakdown();
            for (BG0023VatBreakdown elemBg23 : bg0023) {
                BT0116VatCategoryTaxableAmount bt0116 = null;
                if (!elemBg23.getBT0116VatCategoryTaxableAmount().isEmpty()) {
                    bt0116 = elemBg23.getBT0116VatCategoryTaxableAmount(0);
                }
                BT0117VatCategoryTaxAmount bt0117 = null;
                if (!elemBg23.getBT0117VatCategoryTaxAmount().isEmpty()) {
                    bt0117 = elemBg23.getBT0117VatCategoryTaxAmount(0);
                }
                BT0118VatCategoryCode bt0118 = null;
                if (!elemBg23.getBT0118VatCategoryCode().isEmpty()) {
                    bt0118 = elemBg23.getBT0118VatCategoryCode(0);
                }
                BT0119VatCategoryRate bt0119 = null;
                if (!elemBg23.getBT0119VatCategoryRate().isEmpty()) {
                    bt0119 = elemBg23.getBT0119VatCategoryRate(0);
                }


                Element taxTotal = root.getChild("TaxTotal");
                if (taxTotal == null) {
                    taxTotal = new Element("TaxTotal");
                    root.addContent(taxTotal);
                }

                Element taxSubtotal = new Element("TaxSubtotal");
                taxTotal.addContent(taxSubtotal);

                if (bt0116 != null) {
                    Element taxableAmount = new Element("TaxableAmount");
                    final Double value = bt0116.getValue();
                    try {
                        taxableAmount.setText(dblStrConverter.convert(value));
                        taxSubtotal.addContent(taxableAmount);
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
                if (bt0117 != null) {
                    Element taxAmount = new Element("TaxAmount");
                    final Double value = bt0117.getValue();
                    try {
                        taxAmount.setText(dblStrConverter.convert(value));
                        taxSubtotal.addContent(taxAmount);
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
                        ;
                    }
                }

                Element taxCategory = new Element("TaxCategory");
                taxSubtotal.addContent(taxCategory);

                if (bt0118 != null) {
                    Element id = new Element("ID");
                    Untdid5305DutyTaxFeeCategories dutyTaxFeeCategories = bt0118.getValue();
                    id.setText(dutyTaxFeeCategories.name());
                    taxCategory.addContent(id);
                }


                if (bt0119 != null) {
                    Element percent = new Element("Percent");
                    final Double value = bt0119.getValue();
                    try {
                        percent.setText(dblStrConverter.convert(value));
                        taxCategory.addContent(percent);
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

                {
                    Element taxScheme = new Element("TaxScheme");
                    Element taxSchemeId = new Element("ID");
                    taxCategory.addContent(taxScheme.addContent(taxSchemeId.setText("VAT")));
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
    }
}