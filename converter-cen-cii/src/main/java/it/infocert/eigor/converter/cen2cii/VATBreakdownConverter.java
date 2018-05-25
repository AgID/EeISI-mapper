package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
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
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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

        if (!invoice.getBG0013DeliveryInformation().isEmpty() &&
                !invoice.getBG0013DeliveryInformation(0).getBG0014InvoicingPeriod().isEmpty()) {

            BG0014InvoicingPeriod bg14 = invoice.getBG0013DeliveryInformation(0).getBG0014InvoicingPeriod(0);
            BT0073InvoicingPeriodStartDate bt73 = !bg14.getBT0073InvoicingPeriodStartDate().isEmpty() ? bg14.getBT0073InvoicingPeriodStartDate(0) : null;
            BT0074InvoicingPeriodEndDate bt74 = !bg14.getBT0074InvoicingPeriodEndDate().isEmpty() ? bg14.getBT0074InvoicingPeriodEndDate(0) : null;

            Element billingSpecifiedPeriod = new Element("BillingSpecifiedPeriod", ramNs);

            //According to Schematron rule BR-29 the EndDateTime must be greater than the StartDateTime, yet the format
            //specified is yyyy-MM-dd, which will cause fail of said rule for 2 dates with consecutive times on the same
            //calendar day. Given the format, in this case, just StartDateTime element should suffice.
            if (bt73 != null && bt74 != null &&
                    bt73.getValue().getYear() == bt74.getValue().getYear() &&
                    bt73.getValue().getMonthOfYear() == bt74.getValue().getMonthOfYear() &&
                    bt73.getValue().getDayOfMonth() == bt74.getValue().getDayOfMonth()) {

                billingSpecifiedPeriod.addContent(
                        new Element("StartDateTime", ramNs)
                                .addContent(toDateTimeStringElement(udtNs, bt73.getValue()))
                );
            } else {
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
            applicableHeaderTradeSettlement.addContent(billingSpecifiedPeriod);


        }

        for (BG0023VatBreakdown bg0023 : invoice.getBG0023VatBreakdown()) {
            Element applicableTradeTax = new Element("ApplicableTradeTax", ramNs);

//          <xsd:element name="CalculatedAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            if (!bg0023.getBT0117VatCategoryTaxAmount().isEmpty()) {
                BT0117VatCategoryTaxAmount bt0117 = bg0023.getBT0117VatCategoryTaxAmount(0);
                Element calculatedAmount = new Element("CalculatedAmount", ramNs);
                BigDecimal value = bt0117.getValue();
                calculatedAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                applicableTradeTax.addContent(calculatedAmount);
            }

//			<xsd:element name="TypeCode" type="qdt:TaxTypeCodeType" minOccurs="0"/>
            Element typeCode = new Element("TypeCode", ramNs);
            typeCode.setText("VAT");
            applicableTradeTax.addContent(typeCode);

            BT0118VatCategoryCode bt0118 = null;
            if (!bg0023.getBT0118VatCategoryCode().isEmpty()) {
                bt0118 = bg0023.getBT0118VatCategoryCode(0);
            }

//			<xsd:element name="ExemptionReason" type="udt:TextType" minOccurs="0"/>
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

//			<xsd:element name="CalculatedRate" type="udt:RateType" minOccurs="0"/>
//			<xsd:element name="CalculationSequenceNumeric" type="udt:NumericType" minOccurs="0"/>
//			<xsd:element name="BasisQuantity" type="udt:QuantityType" minOccurs="0"/>

//			<xsd:element name="BasisAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            if (!bg0023.getBT0116VatCategoryTaxableAmount().isEmpty()) {
                BT0116VatCategoryTaxableAmount bt0116 = bg0023.getBT0116VatCategoryTaxableAmount(0);
                Element basisAmount = new Element("BasisAmount", ramNs);
                BigDecimal value = bt0116.getValue();
                basisAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                applicableTradeTax.addContent(basisAmount);
            }

//			<xsd:element name="UnitBasisAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="LineTotalBasisAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="AllowanceChargeBasisAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>

//			<xsd:element name="CategoryCode" type="qdt:TaxCategoryCodeType" minOccurs="0"/>
            if (bt0118 != null) {
                bt0118 = bg0023.getBT0118VatCategoryCode(0);

                Element categoryCode = new Element("CategoryCode", ramNs);
                categoryCode.setText(bt0118.getValue().name());
                applicableTradeTax.addContent(categoryCode);
            }

//			<xsd:element name="CurrencyCode" type="qdt:CurrencyCodeType" minOccurs="0"/>
//			<xsd:element name="Jurisdiction" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="CustomsDutyIndicator" type="udt:IndicatorType" minOccurs="0"/>

//			<xsd:element name="ExemptionReasonCode" type="udt:CodeType" minOccurs="0"/>
            if (!bg0023.getBT0121VatExemptionReasonCode().isEmpty()) {
                BT0121VatExemptionReasonCode bt0121 = bg0023.getBT0121VatExemptionReasonCode(0);
                Element exemptionReasonCode = new Element("ExemptionReasonCode", ramNs);
                exemptionReasonCode.setText(bt0121.getValue());
                applicableTradeTax.addContent(exemptionReasonCode);
            }

//			<xsd:element name="TaxBasisAllowanceRate" type="udt:RateType" minOccurs="0"/>

//			<xsd:element name="TaxPointDate" type="udt:DateType" minOccurs="0"/>
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

//			<xsd:element name="Type" type="udt:TextType" minOccurs="0"/>
//			<xsd:element name="InformationAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="CategoryName" type="udt:TextType" minOccurs="0" maxOccurs="unbounded"/>

//			<xsd:element name="DueDateTypeCode" type="qdt:TimeReferenceCodeType" minOccurs="0"/>
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

//			<xsd:element name="RateApplicablePercent" type="udt:PercentType" minOccurs="0"/>
            if (!bg0023.getBT0119VatCategoryRate().isEmpty()) {
                BT0119VatCategoryRate bt0119 = bg0023.getBT0119VatCategoryRate(0);
                Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                BigDecimal value = bt0119.getValue();
                rateApplicablePercent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                applicableTradeTax.addContent(rateApplicablePercent);
            }

//			<xsd:element name="SpecifiedTradeAccountingAccount" type="ram:TradeAccountingAccountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="ServiceSupplyTradeCountry" type="ram:TradeCountryType" minOccurs="0"/>
//			<xsd:element name="BuyerRepayableTaxSpecifiedTradeAccountingAccount" type="ram:TradeAccountingAccountType" minOccurs="0"/>
//			<xsd:element name="SellerPayableTaxSpecifiedTradeAccountingAccount" type="ram:TradeAccountingAccountType" minOccurs="0"/>
//			<xsd:element name="SellerRefundableTaxSpecifiedTradeAccountingAccount" type="ram:TradeAccountingAccountType" minOccurs="0"/>
//			<xsd:element name="BuyerDeductibleTaxSpecifiedTradeAccountingAccount" type="ram:TradeAccountingAccountType" minOccurs="0"/>
//			<xsd:element name="BuyerNonDeductibleTaxSpecifiedTradeAccountingAccount" type="ram:TradeAccountingAccountType" minOccurs="0"/>
//			<xsd:element name="PlaceApplicableTradeLocation" type="ram:TradeLocationType" minOccurs="0" maxOccurs="unbounded"/>

            applicableHeaderTradeSettlement.addContent(applicableTradeTax);
        }
    }

    private Element toDateTimeStringElement(Namespace udtNs, LocalDate value) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        String elementText = dateTimeFormatter.print(value);
        return new Element("DateTimeString", udtNs)
                .setText(elementText)
                .setAttribute("format", "yyyy-mm-dd");
    }
}