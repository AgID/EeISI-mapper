package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BT0095DocumentLevelAllowanceVatCategoryCode;
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

        for (BG0020DocumentLevelAllowances bg0020 : cenInvoice.getBG0020DocumentLevelAllowances()) {
            Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", ramNs);


//          <xsd:element name="ChargeIndicator" type="udt:IndicatorType" minOccurs="0"/>
//			<xsd:element name="ID" type="udt:IDType" minOccurs="0"/>
//			<xsd:element name="SequenceNumeric" type="udt:NumericType" minOccurs="0"/>
//			<xsd:element name="CalculationPercent" type="udt:PercentType" minOccurs="0"/>
//			<xsd:element name="BasisAmount" type="udt:AmountType" minOccurs="0"/>
//			<xsd:element name="BasisQuantity" type="udt:QuantityType" minOccurs="0"/>
//			<xsd:element name="PrepaidIndicator" type="udt:IndicatorType" minOccurs="0"/>
//			<xsd:element name="ActualAmount" type="udt:AmountType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="UnitBasisAmount" type="udt:AmountType" minOccurs="0"/>
//			<xsd:element name="ReasonCode" type="qdt:AllowanceChargeReasonCodeType" minOccurs="0"/>
//			<xsd:element name="Reason" type="udt:TextType" minOccurs="0"/>
//			<xsd:element name="TypeCode" type="qdt:AllowanceChargeIdentificationCodeType" minOccurs="0"/>
//			<xsd:element name="CategoryTradeTax" type="ram:TradeTaxType" minOccurs="0" maxOccurs="unbounded"/>
//			<xsd:element name="ActualTradeCurrencyExchange" type="ram:TradeCurrencyExchangeType" minOccurs="0"/>

            Element chargeIndicator = new Element("ChargeIndicator", ramNs);
            Element indicator = new Element("Indicator", udtNs);
            indicator.setText("false");
            chargeIndicator.addContent(indicator);
            specifiedTradeAllowanceCharge.addContent(chargeIndicator);

            if (!bg0020.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                Element calculationPercent = new Element("CalculationPercent", ramNs);
                BigDecimal value = bg0020.getBT0094DocumentLevelAllowancePercentage(0).getValue();
                calculationPercent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(calculationPercent);
            }

            if (!bg0020.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                Element basisAmount = new Element("BasisAmount", ramNs);
                BigDecimal value = bg0020.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue();
                basisAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(basisAmount);
            }

            if (!bg0020.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                Element actualAmount = new Element("ActualAmount", ramNs);
                BigDecimal value = bg0020.getBT0092DocumentLevelAllowanceAmount(0).getValue();
                actualAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                specifiedTradeAllowanceCharge.addContent(actualAmount);
            }

            if (!bg0020.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                Element reasonCode = new Element("ReasonCode", ramNs);
                reasonCode.setText(bg0020.getBT0098DocumentLevelAllowanceReasonCode(0).getValue().name());
                specifiedTradeAllowanceCharge.addContent(reasonCode);
            }

            if (!bg0020.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                Element reason = new Element("Reason", ramNs);
                reason.setText(bg0020.getBT0097DocumentLevelAllowanceReason(0).getValue());
                specifiedTradeAllowanceCharge.addContent(reason);
            }

            Element categoryTradeTax = new Element("CategoryTradeTax", ramNs);
            Element typeCode = new Element("TypeCode", ramNs).setText("VA");
            categoryTradeTax.addContent(typeCode);
            specifiedTradeAllowanceCharge.addContent(categoryTradeTax);

            if (!bg0020.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                BigDecimal percentValue = bg0020.getBT0096DocumentLevelAllowanceVatRate(0).getValue();

                Element categoryCode = new Element("CategoryCode", ramNs);
                if (!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                    BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0);
                    categoryCode.setText(bt0095.getValue().name());
                } else if (BigDecimal.ZERO.compareTo(percentValue) == 0) {
                    categoryCode.setText(Untdid5305DutyTaxFeeCategories.Z.name());
                } else {
                    categoryCode.setText(Untdid5305DutyTaxFeeCategories.S.name());
                }
                categoryTradeTax.addContent(categoryCode);

                Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                rateApplicablePercent.setText(percentValue.setScale(2, RoundingMode.HALF_UP).toString());
                categoryTradeTax.addContent(rateApplicablePercent);
            } else if (!bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode(0);
                Element categoryCode = new Element("CategoryCode", ramNs);
                categoryCode.setText(bt0095.getValue().name());
                categoryTradeTax.addContent(categoryCode);

                if (Untdid5305DutyTaxFeeCategories.E.equals(bt0095.getValue())) {
                    Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                    rateApplicablePercent.setText("0.00");
                    categoryTradeTax.addContent(rateApplicablePercent);
                }
            }

            applicableHeaderTradeSettlement.addContent(specifiedTradeAllowanceCharge);
        }

    }
}