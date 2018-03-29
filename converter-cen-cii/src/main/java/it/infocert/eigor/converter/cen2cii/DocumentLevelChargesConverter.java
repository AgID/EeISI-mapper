package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.DoubleToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Document Level Charges Custom Converter
 */
public class DocumentLevelChargesConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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

        for (BG0021DocumentLevelCharges bg0021 : cenInvoice.getBG0021DocumentLevelCharges()) {
            Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", rootElement.getNamespace("ram"));

            Element chargeIndicator = new Element("ChargeIndicator", rootElement.getNamespace("ram"));
            Element indicator = new Element("Indicator", rootElement.getNamespace("ram"));
            indicator.setText("true");
            chargeIndicator.addContent(indicator);
            specifiedTradeAllowanceCharge.addContent(chargeIndicator);

            if (!bg0021.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                try {
                    Element actualAmount = new Element("ActualAmount");
                    actualAmount.setText(dblStrConverter.convert(bg0021.getBT0099DocumentLevelChargeAmount(0).getValue()));
                    specifiedTradeAllowanceCharge.addContent(actualAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            if (!bg0021.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                try {
                    Element basisAmount = new Element("BasisAmount");
                    basisAmount.setText(dblStrConverter.convert(bg0021.getBT0100DocumentLevelChargeBaseAmount(0).getValue()));
                    specifiedTradeAllowanceCharge.addContent(basisAmount);
                } catch (NumberFormatException | ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            if (!bg0021.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                try {
                    Element calculationPercent = new Element("CalculationPercent");
                    calculationPercent.setText(dblStrConverter.convert(bg0021.getBT0101DocumentLevelChargePercentage(0).getValue()));
                    specifiedTradeAllowanceCharge.addContent(calculationPercent);
                } catch (NumberFormatException | ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            Element categoryTradeTax = new Element("CategoryTradeTax");
            specifiedTradeAllowanceCharge.addContent(categoryTradeTax);

            if (!bg0021.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                Element categoryCode = new Element("CategoryCode");
                categoryCode.setText(bg0021.getBT0102DocumentLevelChargeVatCategoryCode(0).getValue().name());
                categoryTradeTax.addContent(categoryCode);
            }

            if (!bg0021.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
                try {
                    Element rateApplicablePercent = new Element("RateApplicablePercent");
                    rateApplicablePercent.setText(dblStrConverter.convert(bg0021.getBT0103DocumentLevelChargeVatRate(0).getValue()));
                    categoryTradeTax.addContent(rateApplicablePercent);
                } catch (NumberFormatException | ConversionFailedException e) {
                    errors.add(ConversionIssue.newError(new EigorRuntimeException(
                            e.getMessage(),
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.INVALID,
                            e
                    )));
                }
            }

            if (!bg0021.getBT0104DocumentLevelChargeReason().isEmpty()) {
                Element reason = new Element("Reason");
                reason.setText(bg0021.getBT0104DocumentLevelChargeReason(0).getValue());
                specifiedTradeAllowanceCharge.addContent(reason);
            }

            if (!bg0021.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                Element reasonCode = new Element("ReasonCode");
                reasonCode.setText(bg0021.getBT0105DocumentLevelChargeReasonCode(0).getValue().name());
                specifiedTradeAllowanceCharge.addContent(reasonCode);
            }

            applicableHeaderTradeSettlement.addContent(specifiedTradeAllowanceCharge);
        }

    }
}