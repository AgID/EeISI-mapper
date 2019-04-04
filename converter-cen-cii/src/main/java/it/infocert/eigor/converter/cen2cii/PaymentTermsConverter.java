package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The Payment Terms Custom Converter
 */
public class PaymentTermsConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");
        Namespace udtNs = rootElement.getNamespace("udt");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
            rootElement.addContent(supplyChainTradeTransaction);
        }

        Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
        if (applicableHeaderTradeAgreement == null) {
            applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
        }

        Element applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
        if (applicableHeaderTradeSettlement == null) {
            applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
        }

        Element specifiedTradePaymentTerms = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "SpecifiedTradePaymentTerms");
        if (specifiedTradePaymentTerms == null) {
            specifiedTradePaymentTerms = new Element("SpecifiedTradePaymentTerms", ramNs);
            applicableHeaderTradeSettlement.addContent(specifiedTradePaymentTerms);
        }

        if (!invoice.getBT0020PaymentTerms().isEmpty()) {
            String bt0020 = invoice.getBT0020PaymentTerms(0).getValue();
            Element description = new Element("Description", ramNs);
            description.setText(bt0020);
            specifiedTradePaymentTerms.addContent(description);
        }

        if (!invoice.getBT0009PaymentDueDate().isEmpty()) {
            LocalDate bt0009 = invoice.getBT0009PaymentDueDate(0).getValue();
            Element dueDateDateTime = new Element("DueDateDateTime", ramNs);
            Element dateTimeString = new Element("DateTimeString", udtNs);
            dateTimeString.setAttribute("format", "102");
            try {
                dateTimeString.setText(dateStrConverter.convert(bt0009));
                dueDateDateTime.addContent(dateTimeString);
                specifiedTradePaymentTerms.addContent(dueDateDateTime);
            } catch (IllegalArgumentException | ConversionFailedException e) {
                errors.add(ConversionIssue.newError(new EigorRuntimeException(
                        e.getMessage(),
                        callingLocation,
                        ErrorCode.Action.HARDCODED_MAP,
                        ErrorCode.Error.INVALID,
                        e
                )));
            }
        }

        List<BG0016PaymentInstructions> bg0016s = invoice.getBG0016PaymentInstructions();
        if (!bg0016s.isEmpty()) {
            List<BG0019DirectDebit> bg0019s = bg0016s.get(0).getBG0019DirectDebit();
            if (!bg0019s.isEmpty()) {
                List<BT0089MandateReferenceIdentifier> bt0089s = bg0019s.get(0).getBT0089MandateReferenceIdentifier();
                if (!bt0089s.isEmpty()) {
                    BT0089MandateReferenceIdentifier bt0089 = bt0089s.get(0);
                    Element directDebitMandateID = new Element("DirectDebitMandateID", ramNs);
                    directDebitMandateID.setText(bt0089.getValue());
                    specifiedTradePaymentTerms.addContent(directDebitMandateID);
                }
            }
        }

        if (!invoice.getBG0022DocumentTotals().isEmpty()) {
            BG0022DocumentTotals bg0022 = invoice.getBG0022DocumentTotals(0);

            Element specifiedTradeSettlementHeaderMonetarySummation = findNamespaceChild(applicableHeaderTradeAgreement, namespacesInScope, "SpecifiedTradeSettlementHeaderMonetarySummation");
            if (specifiedTradeSettlementHeaderMonetarySummation == null) {
                specifiedTradeSettlementHeaderMonetarySummation = new Element("SpecifiedTradeSettlementHeaderMonetarySummation", ramNs);
                applicableHeaderTradeSettlement.addContent(specifiedTradeSettlementHeaderMonetarySummation);
            }

            final BigDecimal bt106SumOfInvoiceLineNetAmounts = getBT106SumOfInvoiceLineNetAmounts(invoice);
            final BigDecimal bt107SumOfAllowances = getBT107SumOfAllowances(invoice);
            final BigDecimal bt108SumOfCharges = getBT108SumOfCharges(invoice);
            final BigDecimal bt109TaxExclusiveAmount = bt106SumOfInvoiceLineNetAmounts.subtract(bt107SumOfAllowances).add(bt108SumOfCharges);
            final BigDecimal bt110TotalTaxAmount = getBT110TotalTaxAmount(invoice);
            final BigDecimal bt112TaxInclusiveAmount = bt109TaxExclusiveAmount.add(bt110TotalTaxAmount);
            final BigDecimal bt0113PrepaidAmount = bg0022.getBT0113PaidAmount().isEmpty() ? BigDecimal.ZERO : bg0022.getBT0113PaidAmount(0).getValue();
            final BigDecimal bt0114RoundingAmount = bg0022.getBT0114RoundingAmount().isEmpty() ? BigDecimal.ZERO : bg0022.getBT0114RoundingAmount(0).getValue();
            final BigDecimal bt0115PayableAmount = bt112TaxInclusiveAmount.subtract(bt0113PrepaidAmount).add(bt0114RoundingAmount);
            final BigDecimal bt111OrNull = getBT111OrNull(invoice);
            final String bt0005OrNull = InvoiceUtils.evalExpression( ()-> invoice.getBT0005InvoiceCurrencyCode(0).getValue().getCode() );
            final String bt0006OrNull = InvoiceUtils.evalExpression( ()-> invoice.getBT0006VatAccountingCurrencyCode(0).getValue().getCode() );



//          <xsd:element name="LineTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            Element lineTotalAmount = new Element("LineTotalAmount", ramNs);
            lineTotalAmount.setText(bt106SumOfInvoiceLineNetAmounts.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedTradeSettlementHeaderMonetarySummation.addContent(lineTotalAmount);

//			<xsd:element name="ChargeTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            if (BigDecimal.ZERO.compareTo(bt108SumOfCharges) != 0) {
                Element chargeTotalAmount = new Element("ChargeTotalAmount", ramNs);
                chargeTotalAmount.setText(bt108SumOfCharges.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(chargeTotalAmount);
            }

//			<xsd:element name="AllowanceTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            if (BigDecimal.ZERO.compareTo(bt107SumOfAllowances) != 0) {
                Element allowanceTotalAmount = new Element("AllowanceTotalAmount", ramNs);
                allowanceTotalAmount.setText(bt107SumOfAllowances.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(allowanceTotalAmount);
            }

//			<xsd:element name="TaxBasisTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            Element taxBasisTotalAmount = new Element("TaxBasisTotalAmount", ramNs);
            taxBasisTotalAmount.setText(bt109TaxExclusiveAmount.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedTradeSettlementHeaderMonetarySummation.addContent(taxBasisTotalAmount);

//          <xsd:element name="TaxTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            // TODO: This part that depends on the bt0060 seems to me a little weird.
            Element taxTotalAmount = new Element("TaxTotalAmount", ramNs);
            if (bt0006OrNull != null) {
                taxTotalAmount.setText(bt111OrNull.toString());
                taxTotalAmount.setAttribute("currencyID", bt0006OrNull);
            }else{
                taxTotalAmount.setText(bt110TotalTaxAmount.setScale(2, RoundingMode.HALF_UP).toString());
                taxTotalAmount.setAttribute("currencyID", bt0005OrNull);
            }
            specifiedTradeSettlementHeaderMonetarySummation.addContent(taxTotalAmount);

//			<xsd:element name="RoundingAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            Element roundingAmount = new Element("RoundingAmount", ramNs);
            roundingAmount.setText(bt0114RoundingAmount.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedTradeSettlementHeaderMonetarySummation.addContent(roundingAmount);

//			<xsd:element name="GrandTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            Element grandTotalAmount = new Element("GrandTotalAmount", ramNs);
            grandTotalAmount.setText(bt112TaxInclusiveAmount.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedTradeSettlementHeaderMonetarySummation.addContent(grandTotalAmount);

//			<xsd:element name="InformationAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>

//			<xsd:element name="TotalPrepaidAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            Element totalPrepaidAmount = new Element("TotalPrepaidAmount", ramNs);
            totalPrepaidAmount.setText(bt0113PrepaidAmount.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedTradeSettlementHeaderMonetarySummation.addContent(totalPrepaidAmount);

//			<xsd:element name="TotalDiscountAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="TotalAllowanceChargeAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>

//			<xsd:element name="DuePayableAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
            Element duePayableAmount = new Element("DuePayableAmount", ramNs);
            duePayableAmount.setText(bt0115PayableAmount.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedTradeSettlementHeaderMonetarySummation.addContent(duePayableAmount);

//			<xsd:element name="RetailValueExcludingTaxInformationAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="TotalDepositFeeInformationAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="ProductValueExcludingTobaccoTaxInformationAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="TotalRetailValueInformationAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="GrossLineTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="NetLineTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="NetIncludingTaxesLineTotalAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
        }
    }

    private BigDecimal getBT106SumOfInvoiceLineNetAmounts(BG0000Invoice invoice) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BG0025InvoiceLine bg0025 : invoice.getBG0025InvoiceLine()) {
            if (!bg0025.getBT0131InvoiceLineNetAmount().isEmpty()) {
                BT0131InvoiceLineNetAmount bt0131 = bg0025.getBT0131InvoiceLineNetAmount(0);
                sum = sum.add(bt0131.getValue());
            }
        }
        return sum;
    }

    private BigDecimal getBT107SumOfAllowances(BG0000Invoice invoice) {
        BigDecimal sumOfAllowances = BigDecimal.ZERO;
        for (BG0020DocumentLevelAllowances bg0020 : invoice.getBG0020DocumentLevelAllowances()) {
            if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                BT0092DocumentLevelAllowanceAmount bt0092 = bg0020.getBT0092DocumentLevelAllowanceAmount(0);
                sumOfAllowances = sumOfAllowances.add(bt0092.getValue());
            }
        }
        return sumOfAllowances;
    }

    private BigDecimal getBT108SumOfCharges(BG0000Invoice invoice) {
        BigDecimal sumOfCharges = BigDecimal.ZERO;
        for (BG0021DocumentLevelCharges bg0021 : invoice.getBG0021DocumentLevelCharges()) {
            if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                BT0099DocumentLevelChargeAmount bt0099 = bg0021.getBT0099DocumentLevelChargeAmount(0);
                sumOfCharges = sumOfCharges.add(bt0099.getValue());
            }
        }
        return sumOfCharges;
    }

    private BigDecimal getBT110TotalTaxAmount(BG0000Invoice cenInvoice) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BG0023VatBreakdown bg0023 : cenInvoice.getBG0023VatBreakdown()) {
            if (!bg0023.getBT0117VatCategoryTaxAmount().isEmpty()) {
                BT0117VatCategoryTaxAmount bt0117 = bg0023.getBT0117VatCategoryTaxAmount(0);
                sum = sum.add(bt0117.getValue());
            }
        }
        return sum;
    }

    private BigDecimal getBT111OrNull(BG0000Invoice cenInvoice) {
        return InvoiceUtils.evalExpression(() -> cenInvoice.getBG0022DocumentTotals(0).getBT0111InvoiceTotalVatAmountInAccountingCurrency(0).getValue().setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}

