package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The Document Level Allowances Custom Converter
 */
public class DocumentLevelAllowancesConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

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

            if (!bg0020.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                Element calculationPercent = new Element("CalculationPercent");
                BigDecimal value = bg0020.getBT0094DocumentLevelAllowancePercentage(0).getValue();
                calculationPercent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(calculationPercent);
            }

            if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                Element actualAmount = new Element("ActualAmount");
                BigDecimal value = bg0020.getBT0092DocumentLevelAllowanceAmount(0).getValue();
                actualAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(actualAmount);
            }

            if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                Element basisAmount = new Element("BasisAmount");
                BigDecimal value = bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue();
                basisAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(basisAmount);
            }


            Element categoryTradeTax = new Element("CategoryTradeTax");
            specifiedTradeAllowanceCharge.addContent(categoryTradeTax);

            if (!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                Element categoryCode = new Element("CategoryCode");
                categoryCode.setText(bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0).getValue().name());
                categoryTradeTax.addContent(categoryCode);
            }

            if (!bg0020.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                Element rateApplicablePercent = new Element("RateApplicablePercent");
                BigDecimal value = bg0020.getBT0096DocumentLevelAllowanceVatRate(0).getValue();
                rateApplicablePercent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                categoryTradeTax.addContent(rateApplicablePercent);
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