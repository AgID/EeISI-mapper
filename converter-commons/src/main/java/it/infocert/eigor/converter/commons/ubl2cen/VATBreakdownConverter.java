package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.VatExemptionReasonsCodes;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.api.CustomConverterUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0023(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        TypeConverter<String, Double> strDblConverter = StringToDoubleConverter.newConverter();

        BG0023VatBreakdown bg0023 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> taxSubtotals = null;
        Element taxTotal = findNamespaceChild(rootElement, namespacesInScope, "TaxTotal");

        if (taxTotal != null) {

        	taxSubtotals = findNamespaceChildren(taxTotal, namespacesInScope, "TaxSubtotal");

        	for(Element elemSub : taxSubtotals) {

        		bg0023 = new BG0023VatBreakdown();

        		Element taxableAmount = findNamespaceChild(elemSub, namespacesInScope, "TaxableAmount");
        		if (taxableAmount != null) {
                    try {
                        BT0116VatCategoryTaxableAmount bt0116 = new BT0116VatCategoryTaxableAmount(strDblConverter.convert(taxableAmount.getText()));
                        bg0023.getBT0116VatCategoryTaxableAmount().add(bt0116);
                    }catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
        		
        		Element taxAmount = findNamespaceChild(elemSub, namespacesInScope, "TaxAmount");
        		if (taxAmount != null) {
                    try {
                        BT0117VatCategoryTaxAmount bt0117 = new BT0117VatCategoryTaxAmount(strDblConverter.convert(taxAmount.getText()));
                        bg0023.getBT0117VatCategoryTaxAmount().add(bt0117);
                    }catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
        		
        		Element taxCategory = findNamespaceChild(elemSub, namespacesInScope, "TaxCategory");
        		if (taxCategory != null) {
                    Element id = findNamespaceChild(taxCategory, namespacesInScope, "ID");
                    if (id != null) {
                        try{
                            BT0118VatCategoryCode bt0118 = new BT0118VatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(id.getText()));
                            bg0023.getBT0118VatCategoryCode().add(bt0118);
                        }catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Untdid5305DutyTaxFeeCategories not found").action("VATBreakdownConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    
                    Element percent = findNamespaceChild(taxCategory, namespacesInScope, "Percent");
                    if (percent != null) {
                        try {
                            BT0119VatCategoryRate bt0119 = new BT0119VatCategoryRate(strDblConverter.convert(percent.getText()));
                            bg0023.getBT0119VatCategoryRate().add(bt0119);
                        }catch (NumberFormatException | ConversionFailedException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("VATBreakdownConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    
                    Element taxExemptionReason = findNamespaceChild(taxCategory, namespacesInScope, "TaxExemptionReason");
                    if (taxExemptionReason != null) {
                        BT0120VatExemptionReasonText bt0120 = new BT0120VatExemptionReasonText(taxExemptionReason.getText());
                        bg0023.getBT0120VatExemptionReasonText().add(bt0120);
                    }
                    
                    Element taxExemptionReasonCode = findNamespaceChild(taxCategory, namespacesInScope, "TaxExemptionReasonCode");
                    if (taxExemptionReasonCode != null) {
                        try{
                            BT0121VatExemptionReasonCode bt0121 = new BT0121VatExemptionReasonCode(VatExemptionReasonsCodes.valueOf(taxExemptionReasonCode.getText()));
                            bg0023.getBT0121VatExemptionReasonCode().add(bt0121);
                        }catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("VatExemptionReasonsCodes not found").action("VATBreakdownConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
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