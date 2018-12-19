package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BT0102DocumentLevelChargeVatCategoryCode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The Document Level Charges Custom Converter
 */
public class DocumentLevelChargesConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace rsmNs = rootElement.getNamespace("rsm");
        Namespace ramNs = rootElement.getNamespace("ram");
        Namespace udtNs = rootElement.getNamespace("udt");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        Element applicableHeaderTradeSettlement = null;

        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
            applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
            supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            rootElement.addContent(supplyChainTradeTransaction);
        } else {
            applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (applicableHeaderTradeSettlement == null) {
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            }
        }

        for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {
            Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", ramNs);

            Element chargeIndicator = new Element("ChargeIndicator", ramNs);
            Element indicator = new Element("Indicator", udtNs);
            indicator.setText("true");
            chargeIndicator.addContent(indicator);
            specifiedTradeAllowanceCharge.addContent(chargeIndicator);

            if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                Element calculationPercent = new Element("CalculationPercent", ramNs);
                BigDecimal value = bg0021.getBT0101DocumentLevelChargePercentage(0).getValue();
                calculationPercent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(calculationPercent);
            }
            if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                Element basisAmount = new Element("BasisAmount", ramNs);
                BigDecimal value = bg0021.getBT0100DocumentLevelChargeBaseAmount(0).getValue();
                basisAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(basisAmount);
            }
            if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                Element actualAmount = new Element("ActualAmount", ramNs);
                BigDecimal value = bg0021.getBT0099DocumentLevelChargeAmount(0).getValue();
                actualAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(actualAmount);
            }
            if (!bg0021.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                Element reasonCode = new Element("ReasonCode", ramNs);
                reasonCode.setText(bg0021.getBT0105DocumentLevelChargeReasonCode(0).getValue().name());
                specifiedTradeAllowanceCharge.addContent(reasonCode);
            }
            if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
                Element reason = new Element("Reason", ramNs);
                reason.setText(bg0021.getBT0104DocumentLevelChargeReason(0).getValue());
                specifiedTradeAllowanceCharge.addContent(reason);
            }

            Element categoryTradeTax = new Element("CategoryTradeTax", ramNs);
            Element typeCode = new Element("TypeCode", ramNs).setText("VAT");
            categoryTradeTax.addContent(typeCode);
            specifiedTradeAllowanceCharge.addContent(categoryTradeTax);

            if (!bg0021.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
                BigDecimal percentValue = bg0021.getBT0103DocumentLevelChargeVatRate(0).getValue();

                Element categoryCode = new Element("CategoryCode", ramNs);
                if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
                    categoryCode.setText(bt0102.getValue().name());
                } else if (BigDecimal.ZERO.compareTo(percentValue) == 0) {
                    categoryCode.setText(Untdid5305DutyTaxFeeCategories.Z.name());
                } else {
                    categoryCode.setText(Untdid5305DutyTaxFeeCategories.S.name());
                }
                categoryTradeTax.addContent(categoryCode);

                Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                rateApplicablePercent.setText(percentValue.setScale(2, RoundingMode.HALF_UP).toString());
                categoryTradeTax.addContent(rateApplicablePercent);

            } else if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                BT0102DocumentLevelChargeVatCategoryCode bt0102 = bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0);
                Element categoryCode = new Element("CategoryCode", ramNs);
                categoryCode.setText(bt0102.getValue().name());
                categoryTradeTax.addContent(categoryCode);

                if (Untdid5305DutyTaxFeeCategories.E.equals(bt0102.getValue())) {
                    Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                    rateApplicablePercent.setText("0.00");
                    categoryTradeTax.addContent(rateApplicablePercent);
                }
            }

            applicableHeaderTradeSettlement.addContent(specifiedTradeAllowanceCharge);
        }

    }
}