package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
public class InvoiceLineConverter extends CustomConverterUtils implements CustomMapping<Document> {

    //BG0025
    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        StringToDoubleConverter strDblConverter = new StringToDoubleConverter();

        BG0025InvoiceLine bg0025 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> includedSupplyChainTradeLineItems = null;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            includedSupplyChainTradeLineItems = findNamespaceChildren(child, namespacesInScope, "IncludedSupplyChainTradeLineItem");

            for(Element elemIncSup : includedSupplyChainTradeLineItems) {

                bg0025 = new BG0025InvoiceLine();

                //AssociatedDocumentLineDocument
                Element childAssociated = findNamespaceChild(elemIncSup, namespacesInScope, "AssociatedDocumentLineDocument");
                if (childAssociated != null) {
                    Element lineId = findNamespaceChild(childAssociated, namespacesInScope, "LineID");
                    if (lineId != null) {
                        BT0126InvoiceLineIdentifier bt0126 = new BT0126InvoiceLineIdentifier(lineId.getText());
                        bg0025.getBT0126InvoiceLineIdentifier().add(bt0126);
                    }
                    Element childInclude = findNamespaceChild(childAssociated, namespacesInScope, "IncludedNote");
                    if (childInclude != null) {
                        Element content = findNamespaceChild(childInclude, namespacesInScope, "Content");
                        BT0127InvoiceLineNote bt0127 = new BT0127InvoiceLineNote(content.getText());
                        bg0025.getBT0127InvoiceLineNote().add(bt0127);
                    }
                }

                //SpecifiedLineTradeSettlement
                Element childSpecifiedSett = findNamespaceChild(elemIncSup, namespacesInScope, "SpecifiedLineTradeSettlement");
                if (childSpecifiedSett != null) {
                    //AdditionalReferencedDocument
                    Element childAdditional = findNamespaceChild(childSpecifiedSett, namespacesInScope, "AdditionalReferencedDocument");
                    if (childAdditional != null) {
                        Element issuerAssignedID = findNamespaceChild(childAdditional, namespacesInScope, "IssuerAssignedID");
                        Element typeCode = findNamespaceChild(childAdditional, namespacesInScope, "TypeCode");
                        if (issuerAssignedID != null && typeCode != null) {
                            BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(issuerAssignedID.getText()+" "+typeCode.getText());
                            bg0025.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);
                        }
                    }
                    //SpecifiedTradeSettlementLineMonetarySummation
                    Element childSpecified = findNamespaceChild(childSpecifiedSett, namespacesInScope, "SpecifiedTradeSettlementLineMonetarySummation");
                    if (childSpecified != null) {
                        Element lineTotalAmount = findNamespaceChild(childSpecified, namespacesInScope, "LineTotalAmount");
                        if (lineTotalAmount != null) {
                            try {
                                BT0131InvoiceLineNetAmount bt0131 = new BT0131InvoiceLineNetAmount(strDblConverter.convert(lineTotalAmount.getText()));
                                bg0025.getBT0131InvoiceLineNetAmount().add(bt0131);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                    }
                    //ReceivableSpecifiedTradeAccountingAccount
                    Element childReceivable = findNamespaceChild(childSpecifiedSett, namespacesInScope, "ReceivableSpecifiedTradeAccountingAccount");
                    if (childReceivable != null) {
                        Element id = findNamespaceChild(childReceivable, namespacesInScope, "ID");
                        if (id != null) {
                            BT0133InvoiceLineBuyerAccountingReference bt0133 = new BT0133InvoiceLineBuyerAccountingReference(id.getText());
                            bg0025.getBT0133InvoiceLineBuyerAccountingReference().add(bt0133);
                        }
                    }

                    //BG0026
                    //BillingSpecifiedPeriod
                    BG0026InvoiceLinePeriod bg0026 = new BG0026InvoiceLinePeriod();
                    Element childBillingSpec = findNamespaceChild(childSpecifiedSett, namespacesInScope, "BillingSpecifiedPeriod");
                    if (childBillingSpec != null) {
                        Element childStartDateTime = findNamespaceChild(childBillingSpec, namespacesInScope, "StartDateTime");
                        if (childStartDateTime != null) {
                            Element dateTimeString = findNamespaceChild(childStartDateTime, namespacesInScope, "DateTimeString");
                            if (dateTimeString != null) {

                                Attribute dateTimeAttribute = dateTimeString.getAttribute("format");
                                if (dateTimeAttribute != null && dateTimeAttribute.getValue().equals("102")) {
                                    BT0134InvoiceLinePeriodStartDate bt0134 = new BT0134InvoiceLinePeriodStartDate(new StringToJavaLocalDateConverter("yyyyMMdd").convert(dateTimeString.getText()));
                                    bg0026.getBT0134InvoiceLinePeriodStartDate().add(bt0134);
                                }
                            }
                        }
                        Element childEndDateTime = findNamespaceChild(childBillingSpec, namespacesInScope, "EndDateTime");
                        if (childEndDateTime != null) {
                            Element dateTimeString = findNamespaceChild(childEndDateTime, namespacesInScope, "DateTimeString");
                            if (dateTimeString != null) {

                                Attribute dateTimeAttribute = dateTimeString.getAttribute("format");
                                if (dateTimeAttribute != null && dateTimeAttribute.getValue().equals("102")) {
                                    BT0135InvoiceLinePeriodEndDate bt0135 = new BT0135InvoiceLinePeriodEndDate(new StringToJavaLocalDateConverter("yyyyMMdd").convert(dateTimeString.getText()));
                                    bg0026.getBT0135InvoiceLinePeriodEndDate().add(bt0135);
                                }
                            }
                        }
                        bg0025.getBG0026InvoiceLinePeriod().add(bg0026);
                    }

                    //BG0027 - BG0028
                    BG0027InvoiceLineAllowances bg0027 = null;
                    BG0028InvoiceLineCharges bg0028 = null;
                    List<Element> childrenInvoiceAllowances = findNamespaceChildren(childSpecifiedSett, namespacesInScope, "SpecifiedTradeAllowanceCharge");
                    for(Element elemInvAll : childrenInvoiceAllowances) {

                        Element chargeIndicator = findNamespaceChild(elemInvAll, namespacesInScope, "ChargeIndicator");
                        if (chargeIndicator != null) {
                            Element indicator = findNamespaceChild(chargeIndicator, namespacesInScope, "Indicator");
                            if (indicator != null && indicator.getText().equals("false")) {

                                //indicator == false --> BG0027
                                bg0027 = new BG0027InvoiceLineAllowances();
                                Element actualAmount = findNamespaceChild(elemInvAll, namespacesInScope, "ActualAmount");
                                if (actualAmount != null) {
                                    try {
                                        BT0136InvoiceLineAllowanceAmount bt0136 = new BT0136InvoiceLineAllowanceAmount(strDblConverter.convert(actualAmount.getText()));
                                        bg0027.getBT0136InvoiceLineAllowanceAmount().add(bt0136);
                                    }catch (NumberFormatException e) {
                                        errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                    }
                                }

                                Element basisAmount = findNamespaceChild(elemInvAll, namespacesInScope, "BasisAmount");
                                if (basisAmount != null) {
                                    try {
                                        BT0137InvoiceLineAllowanceBaseAmount bt0137 = new BT0137InvoiceLineAllowanceBaseAmount(strDblConverter.convert(basisAmount.getText()));
                                        bg0027.getBT0137InvoiceLineAllowanceBaseAmount().add(bt0137);
                                    }catch (NumberFormatException e) {
                                        errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                    }
                                }

                                Element calculationPercent = findNamespaceChild(elemInvAll, namespacesInScope, "CalculationPercent");
                                if (calculationPercent != null) {
                                    try {
                                        BT0138InvoiceLineAllowancePercentage bt0138 = new BT0138InvoiceLineAllowancePercentage(strDblConverter.convert(calculationPercent.getText()));
                                        bg0027.getBT0138InvoiceLineAllowancePercentage().add(bt0138);
                                    }catch (NumberFormatException e) {
                                        errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                    }
                                }

                                Element reason = findNamespaceChild(elemInvAll, namespacesInScope, "Reason");
                                if (reason != null) {
                                    BT0139InvoiceLineAllowanceReason bt0139 = new BT0139InvoiceLineAllowanceReason(reason.getText());
                                    bg0027.getBT0139InvoiceLineAllowanceReason().add(bt0139);
                                }

                                Element reasonCode = findNamespaceChild(elemInvAll, namespacesInScope, "ReasonCode");
                                if (reasonCode != null) {
                                    BT0140InvoiceLineAllowanceReasonCode bt0140 = new BT0140InvoiceLineAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.valueOf("Code"+reasonCode.getText()));
                                    bg0027.getBT0140InvoiceLineAllowanceReasonCode().add(bt0140);
                                }

                                bg0025.getBG0027InvoiceLineAllowances().add(bg0027);
                            }

                            if (indicator != null && indicator.getText().equals("true")) {

                                //indicator == true --> BG0028
                                bg0028 = new BG0028InvoiceLineCharges();
                                Element actualAmount = findNamespaceChild(elemInvAll, namespacesInScope, "ActualAmount");
                                if (actualAmount != null) {
                                    try {
                                        BT0141InvoiceLineChargeAmount bt0141 = new BT0141InvoiceLineChargeAmount(strDblConverter.convert(actualAmount.getText()));
                                        bg0028.getBT0141InvoiceLineChargeAmount().add(bt0141);
                                    }catch (NumberFormatException e) {
                                        errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                    }
                                }
                                Element basisAmount = findNamespaceChild(elemInvAll, namespacesInScope, "BasisAmount");
                                if (basisAmount != null) {
                                    try {
                                        BT0142InvoiceLineChargeBaseAmount bt0142 = new BT0142InvoiceLineChargeBaseAmount(strDblConverter.convert(basisAmount.getText()));
                                        bg0028.getBT0142InvoiceLineChargeBaseAmount().add(bt0142);
                                    }catch (NumberFormatException e) {
                                        errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                    }
                                }

                                Element calculationPercent = findNamespaceChild(elemInvAll, namespacesInScope, "CalculationPercent");
                                if (calculationPercent != null) {
                                    try {
                                        BT0143InvoiceLineChargePercentage bt0143 = new BT0143InvoiceLineChargePercentage(strDblConverter.convert(calculationPercent.getText()));
                                        bg0028.getBT0143InvoiceLineChargePercentage().add(bt0143);
                                    }catch (NumberFormatException e) {
                                        errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                                    }
                                }

                                Element reason = findNamespaceChild(elemInvAll, namespacesInScope, "Reason");
                                if (reason != null) {
                                    BT0144InvoiceLineChargeReason bt0144 = new BT0144InvoiceLineChargeReason(reason.getText());
                                    bg0028.getBT0144InvoiceLineChargeReason().add(bt0144);
                                }

                                Element reasonCode = findNamespaceChild(elemInvAll, namespacesInScope, "ReasonCode");
                                if (reasonCode != null) {
                                    BT0145InvoiceLineChargeReasonCode bt0145 = new BT0145InvoiceLineChargeReasonCode(Untdid7161SpecialServicesCodes.valueOf(reasonCode.getText()));
                                    bg0028.getBT0145InvoiceLineChargeReasonCode().add(bt0145);
                                }

                                bg0025.getBG0028InvoiceLineCharges().add(bg0028);
                            }
                        }

                    }

                    //BG0030
                    BG0030LineVatInformation bg0030 = null;
                    Element applicableTradeTax = findNamespaceChild(childSpecifiedSett, namespacesInScope, "ApplicableTradeTax");
                    if (applicableTradeTax != null) {
                        bg0030 = new BG0030LineVatInformation();

                        Element typeCode = findNamespaceChild(applicableTradeTax, namespacesInScope, "TypeCode");
                        Element categoryCode = findNamespaceChild(applicableTradeTax, namespacesInScope, "CategoryCode");
                        if (typeCode != null && categoryCode != null) {
                            BT0151InvoicedItemVatCategoryCode bt0151 = new BT0151InvoicedItemVatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(categoryCode.getText()));
                            bg0030.getBT0151InvoicedItemVatCategoryCode().add(bt0151);
                        }
                        Element rateApplicablePercent = findNamespaceChild(applicableTradeTax, namespacesInScope, "RateApplicablePercent");
                        if (rateApplicablePercent != null) {
                            try {
                                BT0152InvoicedItemVatRate bt0152 = new BT0152InvoicedItemVatRate(strDblConverter.convert(rateApplicablePercent.getText()));
                                bg0030.getBT0152InvoicedItemVatRate().add(bt0152);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                        bg0025.getBG0030LineVatInformation().add(bg0030);
                    }
                }

                //SpecifiedLineTradeDelivery
                Element childSpecifiedDel = findNamespaceChild(elemIncSup, namespacesInScope, "SpecifiedLineTradeDelivery");
                if (childSpecifiedDel != null) {
                    Element billedQuantity = findNamespaceChild(childSpecifiedDel, namespacesInScope, "BilledQuantity");
                    if (billedQuantity != null) {
                        try {
                            BT0129InvoicedQuantity bt0129 = new BT0129InvoicedQuantity(strDblConverter.convert(billedQuantity.getText()));
                            bg0025.getBT0129InvoicedQuantity().add(bt0129);
                        }catch (NumberFormatException e) {
                            errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                        }

                        Attribute billedAttribute = billedQuantity.getAttribute("unitCode");
                        if (billedAttribute != null) {
                            String commonCode = billedAttribute.getValue();
                            UnitOfMeasureCodes unitCode = null;
                            for(UnitOfMeasureCodes elemUnitCode : UnitOfMeasureCodes.values()) {
                                if (elemUnitCode.getCommonCode().equals(commonCode)) {
                                    unitCode = elemUnitCode;
                                }
                            }
                            BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = new BT0130InvoicedQuantityUnitOfMeasureCode(unitCode);
                            bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(bt0130);
                        }
                    }
                }

                //SpecifiedLineTradeAgreement
                Element childSpecifiedAgree = findNamespaceChild(elemIncSup, namespacesInScope, "SpecifiedLineTradeAgreement");
                if (childSpecifiedAgree != null) {
                    Element buyerOrderReferencedDocument = findNamespaceChild(childSpecifiedAgree, namespacesInScope, "BuyerOrderReferencedDocument");
                    if (buyerOrderReferencedDocument != null) {
                        Element lineID = findNamespaceChild(buyerOrderReferencedDocument, namespacesInScope, "LineID");
                        if (lineID != null) {
                            BT0132ReferencedPurchaseOrderLineReference bt0132 = new BT0132ReferencedPurchaseOrderLineReference(lineID.getText());
                            bg0025.getBT0132ReferencedPurchaseOrderLineReference().add(bt0132);
                        }
                    }

                    //bg0029
                    BG0029PriceDetails bg0029 = new BG0029PriceDetails();
                    Double netBasisQuantityBT0149 = null;
                    Element netPriceProductTradePrice = findNamespaceChild(childSpecifiedAgree, namespacesInScope, "NetPriceProductTradePrice");
                    if (netPriceProductTradePrice != null) {
                        Element chargeAmount = findNamespaceChild(netPriceProductTradePrice, namespacesInScope, "ChargeAmount");
                        if (chargeAmount != null) {
                            try {
                                BT0146ItemNetPrice bt0146 = new BT0146ItemNetPrice(strDblConverter.convert(chargeAmount.getText()));
                                bg0029.getBT0146ItemNetPrice().add(bt0146);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }

                        Element netBasisQuantity = findNamespaceChild(netPriceProductTradePrice, namespacesInScope, "BasisQuantity");
                        if (netBasisQuantity != null) {
                            try {
                                netBasisQuantityBT0149 = strDblConverter.convert(netBasisQuantity.getText());
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }
                    }
                    Element grossPriceProductTradePrice = findNamespaceChild(childSpecifiedAgree, namespacesInScope, "GrossPriceProductTradePrice");
                    if (grossPriceProductTradePrice != null) {
                        Element appliedTradeAllowanceCharge = findNamespaceChild(grossPriceProductTradePrice, namespacesInScope, "AppliedTradeAllowanceCharge");
                        if (appliedTradeAllowanceCharge != null) {
                            Element actualAmount = findNamespaceChild(appliedTradeAllowanceCharge, namespacesInScope, "ActualAmount");
                            try {
                                BT0147ItemPriceDiscount bt0147 = new BT0147ItemPriceDiscount(strDblConverter.convert(actualAmount.getText()));
                                bg0029.getBT0147ItemPriceDiscount().add(bt0147);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }

                        Element chargeAmount = findNamespaceChild(grossPriceProductTradePrice, namespacesInScope, "ChargeAmount");
                        if (chargeAmount != null) {
                            try {
                                BT0148ItemGrossPrice bt0148 = new BT0148ItemGrossPrice(strDblConverter.convert(chargeAmount.getText()));
                                bg0029.getBT0148ItemGrossPrice().add(bt0148);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }
                        }

                        Element grossBasisQuantity = findNamespaceChild(grossPriceProductTradePrice, namespacesInScope, "BasisQuantity");
                        if (grossBasisQuantity != null) {
                            try {
                                Double grossBasisQuantityTemp = strDblConverter.convert(grossBasisQuantity.getText());
                                BT0149ItemPriceBaseQuantity bt0149 = new BT0149ItemPriceBaseQuantity(grossBasisQuantityTemp + netBasisQuantityBT0149);
                                bg0029.getBT0149ItemPriceBaseQuantity().add(bt0149);
                            }catch (NumberFormatException e) {
                                errors.add(ConversionIssue.newWarning(e, e.getMessage()));
                            }

                            Attribute basisAttribute = grossBasisQuantity.getAttribute("unitCode");
                            if (basisAttribute != null) {
                                String commonCode = basisAttribute.getValue();
                                UnitOfMeasureCodes unitCode = null;
                                for(UnitOfMeasureCodes elemUnitCode : UnitOfMeasureCodes.values()) {
                                    if (elemUnitCode.getCommonCode().equals(commonCode)) {
                                        unitCode = elemUnitCode;
                                    }
                                }
                                BT0150ItemPriceBaseQuantityUnitOfMeasureCode bt0150 = new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(unitCode);
                                bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(bt0150);
                            }
                        }
                    }

                    bg0025.getBG0029PriceDetails().add(bg0029);
                }

                //SpecifiedTradeProduct
                //bg0031
                BG0031ItemInformation bg0031 = null;
                Element specifiedTradeProduct = findNamespaceChild(elemIncSup, namespacesInScope, "SpecifiedTradeProduct");
                if (specifiedTradeProduct != null) {
                    bg0031 = new BG0031ItemInformation();

                    Element name = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "Name");
                    if (name != null) {
                        BT0153ItemName bt0153 = new BT0153ItemName(name.getText());
                        bg0031.getBT0153ItemName().add(bt0153);
                    }
                    Element description = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "Description");
                    if (description != null) {
                        BT0154ItemDescription bt0154 = new BT0154ItemDescription(description.getText());
                        bg0031.getBT0154ItemDescription().add(bt0154);
                    }
                    Element sellerAssignedID = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "SellerAssignedID");
                    if (sellerAssignedID != null) {
                        BT0155ItemSellerSIdentifier bt0155 = new BT0155ItemSellerSIdentifier(sellerAssignedID.getText());
                        bg0031.getBT0155ItemSellerSIdentifier().add(bt0155);
                    }
                    Element buyerAssignedID = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "BuyerAssignedID");
                    if (buyerAssignedID != null) {
                        BT0156ItemBuyerSIdentifier bt0156 = new BT0156ItemBuyerSIdentifier(buyerAssignedID.getText());
                        bg0031.getBT0156ItemBuyerSIdentifier().add(bt0156);
                    }
                    Element globalID = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "GlobalID");
                    if (globalID != null) {
                        BT0157ItemStandardIdentifierAndSchemeIdentifier bt0157 = new BT0157ItemStandardIdentifierAndSchemeIdentifier(globalID.getText());
                        bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier().add(bt0157);
                    }

                    List<Element> designatedProductClassification = findNamespaceChildren(specifiedTradeProduct, namespacesInScope, "DesignatedProductClassification");
                    for(Element elemDesProd : designatedProductClassification) {
                        Element classCode = findNamespaceChild(elemDesProd, namespacesInScope, "ClassCode");
                        BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier bt0158 = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(classCode.getText());
                        bg0031.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().add(bt0158);
                    }

                    Element originTradeCountry = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "OriginTradeCountry");
                    if (originTradeCountry != null) {
                        Element id = findNamespaceChild(originTradeCountry, namespacesInScope, "ID");
                        if (id != null) {
                            BT0159ItemCountryOfOrigin bt0159 = new BT0159ItemCountryOfOrigin(Iso31661CountryCodes.valueOf(id.getText()));
                            bg0031.getBT0159ItemCountryOfOrigin().add(bt0159);
                        }
                    }

                    BG0032ItemAttributes bg0032 = null;
                    List<Element> applicableProductCharacteristic = findNamespaceChildren(specifiedTradeProduct, namespacesInScope, "ApplicableProductCharacteristic");
                    for(Element elemAppProdChar : applicableProductCharacteristic) {
                        bg0032 = new BG0032ItemAttributes();

                        Element descriptionBG32 = findNamespaceChild(elemAppProdChar, namespacesInScope, "Description");
                        if (description != null) {
                            BT0160ItemAttributeName bt0160 = new BT0160ItemAttributeName(descriptionBG32.getText());
                            bg0032.getBT0160ItemAttributeName().add(bt0160);
                        }
                        Element value = findNamespaceChild(elemAppProdChar, namespacesInScope, "Value");
                        if (value != null) {
                            BT0161ItemAttributeValue bt0161 = new BT0161ItemAttributeValue(value.getText());
                            bg0032.getBT0161ItemAttributeValue().add(bt0161);
                        }
                        bg0031.getBG0032ItemAttributes().add(bg0032);
                    }
                    bg0025.getBG0031ItemInformation().add(bg0031);
                }
                invoice.getBG0025InvoiceLine().add(bg0025);
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0025(document, cenInvoice, errors);
    }
}