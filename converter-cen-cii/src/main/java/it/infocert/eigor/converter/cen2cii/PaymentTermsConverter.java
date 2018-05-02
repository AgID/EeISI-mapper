package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
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
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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

            if (!bg0022.getBT0106SumOfInvoiceLineNetAmount().isEmpty()) {
                BigDecimal bt0106 = bg0022.getBT0106SumOfInvoiceLineNetAmount(0).getValue();
                Element lineTotalAmount = new Element("LineTotalAmount", ramNs);
                lineTotalAmount.setText(bt0106.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(lineTotalAmount);
            }

            if (!bg0022.getBT0108SumOfChargesOnDocumentLevel().isEmpty()) {
                BigDecimal bt0108 = bg0022.getBT0108SumOfChargesOnDocumentLevel(0).getValue();
                Element chargeTotalAmount = new Element("ChargeTotalAmount", ramNs);
                chargeTotalAmount.setText(bt0108.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(chargeTotalAmount);
            }

            if (!bg0022.getBT0107SumOfAllowancesOnDocumentLevel().isEmpty()) {
                BigDecimal bt0107 = bg0022.getBT0107SumOfAllowancesOnDocumentLevel(0).getValue();
                Element allowanceTotalAmount = new Element("AllowanceTotalAmount", ramNs);
                allowanceTotalAmount.setText(bt0107.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(allowanceTotalAmount);
            }

            if (!bg0022.getBT0109InvoiceTotalAmountWithoutVat().isEmpty()) {
                BigDecimal bt0109 = bg0022.getBT0109InvoiceTotalAmountWithoutVat(0).getValue();
                Element taxBasisTotalAmount = new Element("TaxBasisTotalAmount", ramNs);
                taxBasisTotalAmount.setText(bt0109.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(taxBasisTotalAmount);
            }

            Element taxTotalAmount = taxTotalAmount(errors, callingLocation, ramNs, bg0022, invoice);
            if (taxTotalAmount != null) {
                specifiedTradeSettlementHeaderMonetarySummation.addContent(taxTotalAmount);
            }

            //FIXME BT-111

            if (!bg0022.getBT0114RoundingAmount().isEmpty()) {
                BigDecimal bt0114 = bg0022.getBT0114RoundingAmount(0).getValue();
                Element roundingAmount = new Element("RoundingAmount", ramNs);
                roundingAmount.setText(bt0114.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(roundingAmount);
            }

            if (!bg0022.getBT0112InvoiceTotalAmountWithVat().isEmpty()) {
                BigDecimal bt0112 = bg0022.getBT0112InvoiceTotalAmountWithVat(0).getValue();
                Element grandTotalAmount = new Element("GrandTotalAmount", ramNs);
                grandTotalAmount.setText(bt0112.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(grandTotalAmount);
            }

            if (!bg0022.getBT0113PaidAmount().isEmpty()) {
                BigDecimal bt0113 = bg0022.getBT0113PaidAmount(0).getValue();
                Element totalPrepaidAmount = new Element("TotalPrepaidAmount", ramNs);
                totalPrepaidAmount.setText(bt0113.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(totalPrepaidAmount);
            }

            if (!bg0022.getBT0115AmountDueForPayment().isEmpty()) {
                BigDecimal bt0115 = bg0022.getBT0115AmountDueForPayment(0).getValue();
                Element duePayableAmount = new Element("DuePayableAmount", ramNs);
                duePayableAmount.setText(bt0115.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeSettlementHeaderMonetarySummation.addContent(duePayableAmount);
            }
        }
    }

    private Element taxTotalAmount(List<IConversionIssue> errors, ErrorCode.Location callingLocation, Namespace ramNs, BG0022DocumentTotals bg0022, BG0000Invoice invoice) {

        Element taxTotalAmount = null;
        if (!bg0022.getBT0110InvoiceTotalVatAmount().isEmpty()) {
            BigDecimal bt0110 = bg0022.getBT0110InvoiceTotalVatAmount(0).getValue();
            taxTotalAmount = new Element("TaxTotalAmount", ramNs);
            taxTotalAmount.setText(bt0110.setScale(2, RoundingMode.HALF_UP).toString());

            if (!invoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
                Iso4217CurrenciesFundsCodes bt0005 = invoice.getBT0005InvoiceCurrencyCode(0).getValue();
                taxTotalAmount.setAttribute("currencyID", bt0005.getCode());
            }
        }
        return taxTotalAmount;
    }
}

