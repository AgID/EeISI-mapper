package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        TypeConverter<Double, String> dblStrConverter = DoubleToStringConverter.newConverter("0.00");

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        Element applicableHeaderTradeSettlement = null;

        if(supplyChainTradeTransaction == null){
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
            applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
            supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            rootElement.addContent(supplyChainTradeTransaction);
        } else {
            applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if(applicableHeaderTradeSettlement == null){
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            }
        }

        for (BG0023VatBreakdown bg0023 : cenInvoice.getBG0023VatBreakdown()) {

            Element applicableTradeTax = new Element("ApplicableTradeTax", rootElement.getNamespace("ram"));

            if (!bg0023.getBT0116VatCategoryTaxableAmount().isEmpty()) {
                BT0116VatCategoryTaxableAmount bt0116 = bg0023.getBT0116VatCategoryTaxableAmount(0);
                try {
                    Element basisAmount = new Element("BasisAmount", rootElement.getNamespace("ram"));
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
                    Element calculatedAmount = new Element("CalculatedAmount", rootElement.getNamespace("ram"));
                    calculatedAmount.setText(dblStrConverter.convert(bt0117.getValue()));
                    applicableTradeTax.addContent(calculatedAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }


            if (!bg0023.getBT0118VatCategoryCode().isEmpty()) {
                BT0118VatCategoryCode bt0118 = bg0023.getBT0118VatCategoryCode(0);

                Element typeCode = new Element("TypeCode", rootElement.getNamespace("ram"));
                typeCode.setText("VAT");
                applicableTradeTax.addContent(typeCode);

                Element categoryCode = new Element("CategoryCode", rootElement.getNamespace("ram"));
                categoryCode.setText(bt0118.getValue().name());
                applicableTradeTax.addContent(categoryCode);
            }

            if (!bg0023.getBT0119VatCategoryRate().isEmpty()) {
                BT0119VatCategoryRate bt0119 = bg0023.getBT0119VatCategoryRate(0);
                try {
                    Element rateApplicablePercent = new Element("RateApplicablePercent", rootElement.getNamespace("ram"));
                    rateApplicablePercent.setText(dblStrConverter.convert(bt0119.getValue()));
                    applicableTradeTax.addContent(rateApplicablePercent);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            if (!bg0023.getBT0120VatExemptionReasonText().isEmpty()) {
                BT0120VatExemptionReasonText bt0120 = bg0023.getBT0120VatExemptionReasonText(0);
                Element exemptionReason = new Element("ExemptionReason", rootElement.getNamespace("ram"));
                exemptionReason.setText(bt0120.getValue());
                applicableTradeTax.addContent(exemptionReason);
            }

            if (!bg0023.getBT0121VatExemptionReasonCode().isEmpty()) {
                BT0121VatExemptionReasonCode bt0121 = bg0023.getBT0121VatExemptionReasonCode(0);
                Element exemptionReasonCode = new Element("ExemptionReasonCode", rootElement.getNamespace("ram"));
                exemptionReasonCode.setText(bt0121.getValue().name());
                applicableTradeTax.addContent(exemptionReasonCode);
            }

            applicableHeaderTradeSettlement.addContent(applicableTradeTax);
        }
    }
}