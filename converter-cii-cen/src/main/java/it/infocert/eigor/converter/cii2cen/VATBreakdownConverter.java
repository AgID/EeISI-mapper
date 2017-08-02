package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.VatExemptionReasonsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0023(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        StringToDoubleConverter strDblConverter = new StringToDoubleConverter();

        BG0023VatBreakdown bg0023 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> applicableTradeTaxes = null;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (child1 != null) {

                applicableTradeTaxes = findNamespaceChildren(child1, namespacesInScope, "ApplicableTradeTax");

                for(Element elem : applicableTradeTaxes) {

                    bg0023 = new BG0023VatBreakdown();

                    Element basisAmount = findNamespaceChild(elem, namespacesInScope, "BasisAmount");
                    Element calculatedAmount = findNamespaceChild(elem, namespacesInScope, "CalculatedAmount");
                    Element typeCode = findNamespaceChild(elem, namespacesInScope, "TypeCode");
                    Element categoryCode = findNamespaceChild(elem, namespacesInScope, "CategoryCode");
                    Element exemptionReasonCode = findNamespaceChild(elem, namespacesInScope, "ExemptionReasonCode");
                    Element rateApplicablePercent = findNamespaceChild(elem, namespacesInScope, "RateApplicablePercent");
                    Element exemptionReason = findNamespaceChild(elem, namespacesInScope, "ExemptionReason");

                    if (basisAmount != null) {
                        try {
                            BT0116VatCategoryTaxableAmount bt0116 = new BT0116VatCategoryTaxableAmount(strDblConverter.convert(basisAmount.getText()));
                            bg0023.getBT0116VatCategoryTaxableAmount().add(bt0116);
                        }catch (NumberFormatException e) {
                            errors.add(ConversionIssue.newError(e, e.getMessage()));
                        }
                    }
                    if (calculatedAmount != null) {
                        try {
                            BT0117VatCategoryTaxAmount bt0117 = new BT0117VatCategoryTaxAmount(strDblConverter.convert(calculatedAmount.getText()));
                            bg0023.getBT0117VatCategoryTaxAmount().add(bt0117);
                        }catch (NumberFormatException e) {
                            errors.add(ConversionIssue.newError(e, e.getMessage()));
                        }
                    }
                    if (typeCode != null && categoryCode != null) {
                        BT0118VatCategoryCode bt0118 = new BT0118VatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(categoryCode.getText()));
                        bg0023.getBT0118VatCategoryCode().add(bt0118);
                    }
                    if (rateApplicablePercent != null) {
                        try {
                            BT0119VatCategoryRate bt0119 = new BT0119VatCategoryRate(strDblConverter.convert(rateApplicablePercent.getText()));
                            bg0023.getBT0119VatCategoryRate().add(bt0119);
                        }catch (NumberFormatException e) {
                            errors.add(ConversionIssue.newError(e, e.getMessage()));
                        }
                    }
                    if (exemptionReason != null) {
                        BT0120VatExemptionReasonText bt0120 = new BT0120VatExemptionReasonText(exemptionReason.getText());
                        bg0023.getBT0120VatExemptionReasonText().add(bt0120);
                    }
                    if (exemptionReasonCode != null) {
                        BT0121VatExemptionReasonCode bt0121 = new BT0121VatExemptionReasonCode(VatExemptionReasonsCodes.valueOf(exemptionReasonCode.getText()));
                        bg0023.getBT0121VatExemptionReasonCode().add(bt0121);
                    }
                    invoice.getBG0023VatBreakdown().add(bg0023);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0023(document, cenInvoice, errors);
    }
}