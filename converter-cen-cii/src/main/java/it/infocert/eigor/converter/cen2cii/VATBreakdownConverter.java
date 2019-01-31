package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.conversion.converter.Untdid2005DateTimePeriodQualifiersToUntdid2475PaymentTimeReferenceConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;
import it.infocert.eigor.model.core.enums.Untdid2475PaymentTimeReference;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");
        TypeConverter<Untdid2005DateTimePeriodQualifiers, Untdid2475PaymentTimeReference> untdid2005To2475Converter = Untdid2005DateTimePeriodQualifiersToUntdid2475PaymentTimeReferenceConverter.newConverter();

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace ramNs = rootElement.getNamespace("ram");
        Namespace udtNs = rootElement.getNamespace("udt");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
            rootElement.addContent(supplyChainTradeTransaction);
        }

        Element applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
        if (applicableHeaderTradeSettlement == null) {
            applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
        }

        Element billingSpecifiedPeriod = null;
        if (!invoice.getBG0013DeliveryInformation().isEmpty() &&
                !invoice.getBG0013DeliveryInformation(0).getBG0014InvoicingPeriod().isEmpty()) {

            BG0014InvoicingPeriod bg14 = invoice.getBG0013DeliveryInformation(0).getBG0014InvoicingPeriod(0);
            BT0073InvoicingPeriodStartDate bt73 = !bg14.getBT0073InvoicingPeriodStartDate().isEmpty() ? bg14.getBT0073InvoicingPeriodStartDate(0) : null;
            BT0074InvoicingPeriodEndDate bt74 = !bg14.getBT0074InvoicingPeriodEndDate().isEmpty() ? bg14.getBT0074InvoicingPeriodEndDate(0) : null;

            billingSpecifiedPeriod = new Element("BillingSpecifiedPeriod", ramNs);


            if (bt73 != null) {
                billingSpecifiedPeriod.addContent(
                        new Element("StartDateTime", ramNs)
                                .addContent(toDateTimeStringElement(udtNs, bt73.getValue()))
                );
            }
            if (bt74 != null) {
                billingSpecifiedPeriod.addContent(
                        new Element("EndDateTime", ramNs)
                                .addContent(toDateTimeStringElement(udtNs, bt74.getValue()))
                );
            }
        }

        for (BG0023VatBreakdown bg0023 : invoice.getBG0023VatBreakdown()) {
            Element applicableTradeTax = new Element("ApplicableTradeTax", ramNs);

            if (!bg0023.getBT0117VatCategoryTaxAmount().isEmpty()) {
                BT0117VatCategoryTaxAmount bt0117 = bg0023.getBT0117VatCategoryTaxAmount(0);
                Element calculatedAmount = new Element("CalculatedAmount", ramNs);
                BigDecimal value = bt0117.getValue();
                calculatedAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                applicableTradeTax.addContent(calculatedAmount);
            }

            Element typeCode = new Element("TypeCode", ramNs);
            typeCode.setText("VAT");
            applicableTradeTax.addContent(typeCode);

            BT0118VatCategoryCode bt0118 = null;
            if (!bg0023.getBT0118VatCategoryCode().isEmpty()) {
                bt0118 = bg0023.getBT0118VatCategoryCode(0);
            }

            Element exemptionReason = new Element("ExemptionReason", ramNs);
            if (!bg0023.getBT0120VatExemptionReasonText().isEmpty()) {
                BT0120VatExemptionReasonText bt0120 = bg0023.getBT0120VatExemptionReasonText(0);
                exemptionReason.setText(bt0120.getValue());
                applicableTradeTax.addContent(exemptionReason);
            } else {
                if (bt0118 != null && Untdid5305DutyTaxFeeCategories.E.equals(bt0118.getValue())) {
                    exemptionReason.setText(bt0118.getValue().getShortDescritpion());
                    applicableTradeTax.addContent(exemptionReason);
                }
            }

            if (!bg0023.getBT0116VatCategoryTaxableAmount().isEmpty()) {
                BT0116VatCategoryTaxableAmount bt0116 = bg0023.getBT0116VatCategoryTaxableAmount(0);
                Element basisAmount = new Element("BasisAmount", ramNs);
                BigDecimal value = bt0116.getValue();
                basisAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                applicableTradeTax.addContent(basisAmount);
            }

            if (bt0118 != null) {
                bt0118 = bg0023.getBT0118VatCategoryCode(0);

                Element categoryCode = new Element("CategoryCode", ramNs);
                categoryCode.setText(bt0118.getValue().name());
                applicableTradeTax.addContent(categoryCode);
            }

            if (!bg0023.getBT0121VatExemptionReasonCode().isEmpty()) {
                BT0121VatExemptionReasonCode bt0121 = bg0023.getBT0121VatExemptionReasonCode(0);
                Element exemptionReasonCode = new Element("ExemptionReasonCode", ramNs);
                exemptionReasonCode.setText(bt0121.getValue());
                applicableTradeTax.addContent(exemptionReasonCode);
            }

            if (!invoice.getBT0007ValueAddedTaxPointDate().isEmpty()) {
                LocalDate bt0007 = invoice.getBT0007ValueAddedTaxPointDate(0).getValue();
                Element dateString = new Element("DateString", udtNs);
                dateString.setAttribute("format", "102");
                try {
                    dateString.setText(dateStrConverter.convert(bt0007));
                    Element taxPointDate = new Element("TaxPointDate", ramNs);
                    taxPointDate.addContent(dateString);
                    applicableTradeTax.addContent(taxPointDate);
                } catch (ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            if (!invoice.getBT0008ValueAddedTaxPointDateCode().isEmpty()) {
                Untdid2005DateTimePeriodQualifiers bt0008 = invoice.getBT0008ValueAddedTaxPointDateCode(0).getValue();
                try {
                    String value = String.valueOf(untdid2005To2475Converter.convert(bt0008).getCode());
                    Element dueDateTypeCode = new Element("DueDateTypeCode", ramNs);
                    dueDateTypeCode.setText(value);
                    applicableTradeTax.addContent(dueDateTypeCode);
                } catch (ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            if (!bg0023.getBT0119VatCategoryRate().isEmpty()) {
                BT0119VatCategoryRate bt0119 = bg0023.getBT0119VatCategoryRate(0);
                Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                BigDecimal value = bt0119.getValue();
                rateApplicablePercent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                applicableTradeTax.addContent(rateApplicablePercent);
            }

            applicableHeaderTradeSettlement.addContent(applicableTradeTax);
        }

        if (billingSpecifiedPeriod != null) {
            applicableHeaderTradeSettlement.addContent(billingSpecifiedPeriod);
        }
    }

    private Element toDateTimeStringElement(Namespace udtNs, LocalDate value) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        String elementText = dateTimeFormatter.print(value);
        return new Element("DateTimeString", udtNs)
                .setText(elementText)
                .setAttribute("format", "102");
    }
}
