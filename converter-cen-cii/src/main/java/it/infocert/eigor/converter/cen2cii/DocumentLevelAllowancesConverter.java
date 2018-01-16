package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Document Level Allowances Custom Converter
 */
public class DocumentLevelAllowancesConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        TypeConverter<Double, String> dblStrConverter = DoubleToStringConverter.newConverter("0.00");

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        Element applicableHeaderTradeSettlement = null;

        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
            applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
            supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            rootElement.addContent(supplyChainTradeTransaction);
        } else {
            applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (applicableHeaderTradeSettlement == null) {
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            }
        }

        for (BG0020DocumentLevelAllowances bg0020 : cenInvoice.getBG0020DocumentLevelAllowances()) {
            Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", rootElement.getNamespace("ram"));

            Element chargeIndicator = new Element("ChargeIndicator", rootElement.getNamespace("ram"));
            Element indicator = new Element("Indicator", rootElement.getNamespace("ram"));
            indicator.setText("false");
            chargeIndicator.addContent(indicator);
            specifiedTradeAllowanceCharge.addContent(chargeIndicator);

            if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                try {
                    Element actualAmount = new Element("ActualAmount");
                    actualAmount.setText(dblStrConverter.convert(bg0020.getBT0092DocumentLevelAllowanceAmount(0).getValue()));
                    specifiedTradeAllowanceCharge.addContent(actualAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("DocumentLevelChargesConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                try {
                    Element basisAmount = new Element("BasisAmount");
                    basisAmount.setText(dblStrConverter.convert(bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue()));
                    specifiedTradeAllowanceCharge.addContent(basisAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("DocumentLevelChargesConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            if (!bg0020.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                try {
                    Element calculationPercent = new Element("CalculationPercent");
                    calculationPercent.setText(dblStrConverter.convert(bg0020.getBT0094DocumentLevelAllowancePercentage(0).getValue()));
                    specifiedTradeAllowanceCharge.addContent(calculationPercent);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("DocumentLevelChargesConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            Element categoryTradeTax = new Element("CategoryTradeTax");
            specifiedTradeAllowanceCharge.addContent(categoryTradeTax);

            if (!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                Element categoryCode = new Element("CategoryCode");
                categoryCode.setText(bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0).getValue().name());
                categoryTradeTax.addContent(categoryCode);
            }

            if (!bg0020.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                try {
                    Element rateApplicablePercent = new Element("RateApplicablePercent");
                    rateApplicablePercent.setText(dblStrConverter.convert(bg0020.getBT0096DocumentLevelAllowanceVatRate(0).getValue()));
                    categoryTradeTax.addContent(rateApplicablePercent);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("DocumentLevelChargesConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            if (!bg0020.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                Element reason = new Element("Reason");
                reason.setText(bg0020.getBT0097DocumentLevelAllowanceReason(0).getValue());
                specifiedTradeAllowanceCharge.addContent(reason);
            }

            if (!bg0020.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                Element reasonCode = new Element("ReasonCode");
                reasonCode.setText(bg0020.getBT0098DocumentLevelAllowanceReasonCode(0).getValue().name());
                specifiedTradeAllowanceCharge.addContent(reasonCode);
            }

            applicableHeaderTradeSettlement.addContent(specifiedTradeAllowanceCharge);
        }

    }
}