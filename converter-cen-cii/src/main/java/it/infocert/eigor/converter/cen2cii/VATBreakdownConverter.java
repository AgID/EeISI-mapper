package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.api.conversion.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        TypeConverter<Double, String> dblStrConverter = DoubleToStringConverter.newConverter("0.00");
        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

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

        Element taxPointDate = null;
        if (!cenInvoice.getBT0007ValueAddedTaxPointDate().isEmpty()) {
            LocalDate bt0007 = cenInvoice.getBT0007ValueAddedTaxPointDate(0).getValue();
            Element dateString = new Element("DateString", udtNs);
            dateString.setAttribute("format", "102");
            try {
                dateString.setText(dateStrConverter.convert(bt0007));
                taxPointDate = new Element("TaxPointDate", ramNs);
                taxPointDate.addContent(dateString);
            } catch (ConversionFailedException e) {
                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("VATBreakdownConverter").build());
                errors.add(ConversionIssue.newError(ere));
            }
        }

        Element dueDateTypeCode = null;
        if (!cenInvoice.getBT0008ValueAddedTaxPointDateCode().isEmpty()) {
            Untdid2005DateTimePeriodQualifiers bt0008 = cenInvoice.getBT0008ValueAddedTaxPointDateCode(0).getValue();
            dueDateTypeCode = new Element("DueDateTypeCode", ramNs);
            dueDateTypeCode.setText(String.valueOf(bt0008.getCode()));
        }

        for (BG0023VatBreakdown bg0023 : cenInvoice.getBG0023VatBreakdown()) {

            Element applicableTradeTax = new Element("ApplicableTradeTax", ramNs);

            if (taxPointDate != null) {
                applicableTradeTax.addContent(taxPointDate.clone());
            }

            if (dueDateTypeCode != null) {
                applicableTradeTax.addContent(dueDateTypeCode.clone());
            }

            if (!bg0023.getBT0116VatCategoryTaxableAmount().isEmpty()) {
                BT0116VatCategoryTaxableAmount bt0116 = bg0023.getBT0116VatCategoryTaxableAmount(0);
                try {
                    Element basisAmount = new Element("BasisAmount", ramNs);
                    basisAmount.setText(dblStrConverter.convert(bt0116.getValue()));
                    applicableTradeTax.addContent(basisAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            if (!bg0023.getBT0117VatCategoryTaxAmount().isEmpty()) {
                BT0117VatCategoryTaxAmount bt0117 = bg0023.getBT0117VatCategoryTaxAmount(0);
                try {
                    Element calculatedAmount = new Element("CalculatedAmount", ramNs);
                    calculatedAmount.setText(dblStrConverter.convert(bt0117.getValue()));
                    applicableTradeTax.addContent(calculatedAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }


            if (!bg0023.getBT0118VatCategoryCode().isEmpty()) {
                BT0118VatCategoryCode bt0118 = bg0023.getBT0118VatCategoryCode(0);

                Element typeCode = new Element("TypeCode", ramNs);
                typeCode.setText("VAT");
                applicableTradeTax.addContent(typeCode);

                Element categoryCode = new Element("CategoryCode", ramNs);
                categoryCode.setText(bt0118.getValue().name());
                applicableTradeTax.addContent(categoryCode);
            }

            if (!bg0023.getBT0119VatCategoryRate().isEmpty()) {
                BT0119VatCategoryRate bt0119 = bg0023.getBT0119VatCategoryRate(0);
                try {
                    Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                    rateApplicablePercent.setText(dblStrConverter.convert(bt0119.getValue()));
                    applicableTradeTax.addContent(rateApplicablePercent);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            if (!bg0023.getBT0120VatExemptionReasonText().isEmpty()) {
                BT0120VatExemptionReasonText bt0120 = bg0023.getBT0120VatExemptionReasonText(0);
                Element exemptionReason = new Element("ExemptionReason", ramNs);
                exemptionReason.setText(bt0120.getValue());
                applicableTradeTax.addContent(exemptionReason);
            }

            if (!bg0023.getBT0121VatExemptionReasonCode().isEmpty()) {
                BT0121VatExemptionReasonCode bt0121 = bg0023.getBT0121VatExemptionReasonCode(0);
                Element exemptionReasonCode = new Element("ExemptionReasonCode", ramNs);
                exemptionReasonCode.setText(bt0121.getValue().name());
                applicableTradeTax.addContent(exemptionReasonCode);
            }

            applicableHeaderTradeSettlement.addContent(applicableTradeTax);
        }
    }
}