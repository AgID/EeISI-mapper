package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Document Level Allowances Custom Converter
 */
public class DocumentLevelAllowancesConverter extends CustomConverter {

    public DocumentLevelAllowancesConverter() {
        super(new Reflections("it.infocert"), new ConversionRegistry());
    }

    public ConversionResult<BG0000Invoice> toBG0020(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        StringToDoubleConverter strDblConverter = new StringToDoubleConverter();

        BG0020DocumentLevelAllowances bg0020 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> specifiedTradeAllowanceCharges = null;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (child1 != null) {
                specifiedTradeAllowanceCharges = findNamespaceChildren(child1, namespacesInScope, "SpecifiedTradeAllowanceCharge");

                for(Element elem : specifiedTradeAllowanceCharges) {

                    Element chargeIndicator = findNamespaceChild(elem, namespacesInScope, "ChargeIndicator");
                    if (chargeIndicator != null) {
                        Element indicator = findNamespaceChild(chargeIndicator, namespacesInScope, "Indicator");
                        if (indicator != null && indicator.getText().equals("false")) {

                            bg0020 = new BG0020DocumentLevelAllowances();

                            Element actualAmount = findNamespaceChild(elem, namespacesInScope, "ActualAmount");
                            Element basisAmount = findNamespaceChild(elem, namespacesInScope, "BasisAmount");
                            Element calculationPercent = findNamespaceChild(elem, namespacesInScope, "CalculationPercent");

                            Element typeCode = null;
                            Element categoryCode = null;
                            Element rateApplicablePercent = null;
                            List<Element> categoryTradeTaxes = findNamespaceChildren(elem, namespacesInScope, "CategoryTradeTax");
                            for(Element taxesElem : categoryTradeTaxes) {
                                typeCode = findNamespaceChild(taxesElem, namespacesInScope, "TypeCode");
                                categoryCode = findNamespaceChild(taxesElem, namespacesInScope, "CategoryCode");
                                rateApplicablePercent = findNamespaceChild(taxesElem, namespacesInScope, "RateApplicablePercent");
                            }

                            Element reason = findNamespaceChild(elem, namespacesInScope, "Reason");
                            Element reasonCode = findNamespaceChild(elem, namespacesInScope, "ReasonCode");

                            if (actualAmount != null) {
                                try {
                                    BT0092DocumentLevelAllowanceAmount bt0092 = new BT0092DocumentLevelAllowanceAmount(strDblConverter.convert(actualAmount.getValue()));
                                    bg0020.getBT0092DocumentLevelAllowanceAmount().add(bt0092);
                                }catch (NumberFormatException e) {
                                    errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                }
                            }
                            if (basisAmount != null) {
                                try {
                                    BT0093DocumentLevelAllowanceBaseAmount bt0093 = new BT0093DocumentLevelAllowanceBaseAmount(strDblConverter.convert(basisAmount.getText()));
                                    bg0020.getBT0093DocumentLevelAllowanceBaseAmount().add(bt0093);
                                }catch (NumberFormatException e) {
                                    errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                }
                            }
                            if (calculationPercent != null) {
                                try{
                                    BT0094DocumentLevelAllowancePercentage bt0094 = new BT0094DocumentLevelAllowancePercentage(strDblConverter.convert(calculationPercent.getText()));
                                    bg0020.getBT0094DocumentLevelAllowancePercentage().add(bt0094);
                                }catch (NumberFormatException e) {
                                    errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                }
                            }
                            if (typeCode != null && categoryCode != null) {
                                BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = new BT0095DocumentLevelAllowanceVatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(categoryCode.getText()));
                                bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().add(bt0095);
                            }
                            if (rateApplicablePercent != null) {
                                try {
                                    BT0096DocumentLevelAllowanceVatRate bt0096 = new BT0096DocumentLevelAllowanceVatRate(strDblConverter.convert(rateApplicablePercent.getText()));
                                    bg0020.getBT0096DocumentLevelAllowanceVatRate().add(bt0096);
                                }catch (NumberFormatException e) {
                                    errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                }
                            }
                            if (reason != null) {
                                BT0097DocumentLevelAllowanceReason bt0097 = new BT0097DocumentLevelAllowanceReason(reason.getText());
                                bg0020.getBT0097DocumentLevelAllowanceReason().add(bt0097);
                            }
                            if (reasonCode != null) {
                                BT0098DocumentLevelAllowanceReasonCode bt0098 = new BT0098DocumentLevelAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.valueOf("Code"+reasonCode.getText()));
                                bg0020.getBT0098DocumentLevelAllowanceReasonCode().add(bt0098);
                            }

                            invoice.getBG0020DocumentLevelAllowances().add(bg0020);
                        }
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
}