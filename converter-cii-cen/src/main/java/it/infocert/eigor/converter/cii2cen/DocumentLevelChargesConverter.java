package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Document Level Charges Custom Converter
 */
public class DocumentLevelChargesConverter extends CustomConverter {

    public DocumentLevelChargesConverter() {
        super(new Reflections("it.infocert"), new ConversionRegistry());
    }

    public ConversionResult<BG0000Invoice> toBG0021(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        StringToDoubleConverter strDblConverter = new StringToDoubleConverter();

        BG0021DocumentLevelCharges bg0021 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> specifiedTradeAllowanceCharges = null;

        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (child1 != null) {
                specifiedTradeAllowanceCharges = findNamespaceChildren(child1, namespacesInScope, "SpecifiedTradeAllowanceCharge");
            }

            for(Element elem : specifiedTradeAllowanceCharges) {

                Element chargeIndicator = findNamespaceChild(elem, namespacesInScope, "ChargeIndicator");
                if (chargeIndicator != null) {
                    Element indicator = findNamespaceChild(chargeIndicator, namespacesInScope, "Indicator");
                    if (indicator != null && indicator.getText().equals("true")) {

                        bg0021 = new BG0021DocumentLevelCharges();

                        Element actualAmount = findNamespaceChild(elem, namespacesInScope, "ActualAmount");
                        Element basisAmount = findNamespaceChild(elem, namespacesInScope, "BasisAmount");
                        Element calculationPercent = findNamespaceChild(elem, namespacesInScope, "CalculationPercent");

                        Element categoryTradeTax = findNamespaceChild(elem, namespacesInScope, "CategoryTradeTax");
                        Element typeCode = findNamespaceChild(categoryTradeTax, namespacesInScope, "TypeCode");
                        Element categoryCode = findNamespaceChild(categoryTradeTax, namespacesInScope, "CategoryCode");
                        Element rateApplicablePercent = findNamespaceChild(categoryTradeTax, namespacesInScope, "RateApplicablePercent");

                        Element reason = findNamespaceChild(elem, namespacesInScope, "Reason");
                        Element reasonCode = findNamespaceChild(elem, namespacesInScope, "ReasonCode");

                        if (actualAmount != null) {
                            try {
                                BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(strDblConverter.convert(actualAmount.getText()));
                                bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                        if (basisAmount != null) {
                            try {
                                BT0100DocumentLevelChargeBaseAmount bt0100 = new BT0100DocumentLevelChargeBaseAmount(strDblConverter.convert(basisAmount.getText()));
                                bg0021.getBT0100DocumentLevelChargeBaseAmount().add(bt0100);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                        if (calculationPercent != null) {
                            try {
                                BT0101DocumentLevelChargePercentage bt0101 = new BT0101DocumentLevelChargePercentage(strDblConverter.convert(calculationPercent.getText()));
                                bg0021.getBT0101DocumentLevelChargePercentage().add(bt0101);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                        if (typeCode != null && categoryCode != null) {
                            BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(categoryCode.getText()));
                            bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);
                        }
                        if (rateApplicablePercent != null) {
                            try {
                                BT0103DocumentLevelChargeVatRate bt0103 = new BT0103DocumentLevelChargeVatRate(strDblConverter.convert(rateApplicablePercent.getText()));
                                bg0021.getBT0103DocumentLevelChargeVatRate().add(bt0103);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                        if (reason != null) {
                            BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason(reason.getText());
                            bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);
                        }
                        if (reasonCode != null) {
                            BT0105DocumentLevelChargeReasonCode bt0105 = new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.valueOf(reasonCode.getText()));
                            bg0021.getBT0105DocumentLevelChargeReasonCode().add(bt0105);
                        }

                        invoice.getBG0021DocumentLevelCharges().add(bg0021);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
}