package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.api.CustomConverterUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
@SuppressWarnings("Duplicates")
public class InvoiceLineConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        StringToDoubleConverter strDblConverter = new StringToDoubleConverter();

        BG0025InvoiceLine bg0025 = null;
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> invoiceLines = findNamespaceChildren(rootElement, namespacesInScope, "InvoiceLine");

        for (Element elemInv : invoiceLines) {

            bg0025 = new BG0025InvoiceLine();

            Element id = findNamespaceChild(elemInv, namespacesInScope, "ID");
            if (id != null) {
                BT0126InvoiceLineIdentifier bt0126 = new BT0126InvoiceLineIdentifier(id.getText());
                bg0025.getBT0126InvoiceLineIdentifier().add(bt0126);
            }
            Element note = findNamespaceChild(elemInv, namespacesInScope, "Note");
            if (note != null) {
                BT0127InvoiceLineNote bt0127 = new BT0127InvoiceLineNote(note.getText());
                bg0025.getBT0127InvoiceLineNote().add(bt0127);
            }

            Element documentReference = findNamespaceChild(elemInv, namespacesInScope, "DocumentReference");
            if (documentReference != null) {
                Element documentTypeCode = findNamespaceChild(documentReference, namespacesInScope, "DocumentTypeCode");
                if (documentTypeCode != null && documentTypeCode.getText().equals("130")) {
                    Element idRef = findNamespaceChild(documentReference, namespacesInScope, "ID");
                    if (idRef != null) {
                        bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier(idRef.getAttributeValue("schemeID"), idRef.getText()));
                        bg0025.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);
                    }
                }
            }

            Element invoicedQuantity = findNamespaceChild(elemInv, namespacesInScope, "InvoicedQuantity");
            if (invoicedQuantity != null) {
                try {
                    BT0129InvoicedQuantity bt0129 = new BT0129InvoicedQuantity(strDblConverter.convert(invoicedQuantity.getText()));
                    bg0025.getBT0129InvoicedQuantity().add(bt0129);
                } catch (NumberFormatException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
                Attribute invoicedQuantityAttribute = invoicedQuantity.getAttribute("unitCode");
                if (invoicedQuantityAttribute != null) {
                    String commonCode = invoicedQuantityAttribute.getValue();
                    try {
                        UnitOfMeasureCodes unitCode = null;
                        for (UnitOfMeasureCodes elemUnitCode : UnitOfMeasureCodes.values()) {
                            if (elemUnitCode.getCommonCode().equals(commonCode)) {
                                unitCode = elemUnitCode;
                            }
                        }
                        BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = new BT0130InvoicedQuantityUnitOfMeasureCode(unitCode);
                        bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(bt0130);
                    } catch (NullPointerException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder()
                                .message(String.format("Unit Of Measure Code %s not found in list", commonCode))
                                .action("InvoiceLineConverter")
                                .error("UnitOfMeasureNotFound")
                                .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
            }

            Element lineExtensionAmount = findNamespaceChild(elemInv, namespacesInScope, "LineExtensionAmount");
            if (lineExtensionAmount != null) {
                try {
                    BT0131InvoiceLineNetAmount bt0131 = new BT0131InvoiceLineNetAmount(strDblConverter.convert(lineExtensionAmount.getText()));
                    bg0025.getBT0131InvoiceLineNetAmount().add(bt0131);
                } catch (NumberFormatException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            Element orderLineReference = findNamespaceChild(elemInv, namespacesInScope, "OrderLineReference");
            if (orderLineReference != null) {
                Element idOrder = findNamespaceChild(orderLineReference, namespacesInScope, "LineID");
                if (idOrder != null) {
                    BT0132ReferencedPurchaseOrderLineReference bt0132 = new BT0132ReferencedPurchaseOrderLineReference(idOrder.getText());
                    bg0025.getBT0132ReferencedPurchaseOrderLineReference().add(bt0132);
                }
            }

            Element accountingCost = findNamespaceChild(elemInv, namespacesInScope, "AccountingCost");
            if (accountingCost != null) {
                BT0133InvoiceLineBuyerAccountingReference bt0133 = new BT0133InvoiceLineBuyerAccountingReference(accountingCost.getText());
                bg0025.getBT0133InvoiceLineBuyerAccountingReference().add(bt0133);
            }

            //BG0026
            BG0026InvoiceLinePeriod bg0026 = new BG0026InvoiceLinePeriod();
            Element invoicePeriodLine = findNamespaceChild(elemInv, namespacesInScope, "InvoicePeriod");
            if (invoicePeriodLine != null) {
                Element startDateLine = findNamespaceChild(invoicePeriodLine, namespacesInScope, "StartDate");
                Element endDateLine = findNamespaceChild(invoicePeriodLine, namespacesInScope, "EndDate");
                if (startDateLine != null && endDateLine != null) {
                    try {
                        BT0134InvoiceLinePeriodStartDate bt0134 = new BT0134InvoiceLinePeriodStartDate(new StringToJavaLocalDateConverter("yyyy-MM-dd").convert(startDateLine.getText()));
                        bg0026.getBT0134InvoiceLinePeriodStartDate().add(bt0134);
                    } catch (IllegalArgumentException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                    try {
                        BT0135InvoiceLinePeriodEndDate bt0135 = new BT0135InvoiceLinePeriodEndDate(new StringToJavaLocalDateConverter("yyyy-MM-dd").convert(endDateLine.getText()));
                        bg0026.getBT0135InvoiceLinePeriodEndDate().add(bt0135);
                    } catch (IllegalArgumentException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
            } else {
                Element invoicePeriod = findNamespaceChild(rootElement, namespacesInScope, "InvoicePeriod");
                if (invoicePeriod != null) {
                    Element startDate = findNamespaceChild(invoicePeriod, namespacesInScope, "StartDate");
                    if (startDate != null) {
                        try {
                            BT0134InvoiceLinePeriodStartDate bt0134 = new BT0134InvoiceLinePeriodStartDate(new StringToJavaLocalDateConverter("yyyy-MM-dd").convert(startDate.getText()));
                            bg0026.getBT0134InvoiceLinePeriodStartDate().add(bt0134);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    Element endDate = findNamespaceChild(invoicePeriod, namespacesInScope, "EndDate");
                    if (endDate != null) {
                        try {
                            BT0135InvoiceLinePeriodEndDate bt0135 = new BT0135InvoiceLinePeriodEndDate(new StringToJavaLocalDateConverter("yyyy-MM-dd").convert(endDate.getText()));
                            bg0026.getBT0135InvoiceLinePeriodEndDate().add(bt0135);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                }
            }
            bg0025.getBG0026InvoiceLinePeriod().add(bg0026);

            //BG0027 - BG0028
            BG0027InvoiceLineAllowances bg0027 = null;
            BG0028InvoiceLineCharges bg0028 = null;
            List<Element> allowanceCharges = findNamespaceChildren(elemInv, namespacesInScope, "AllowanceCharge");
            for (Element elemInvAll : allowanceCharges) {

                Element chargeIndicator = findNamespaceChild(elemInvAll, namespacesInScope, "ChargeIndicator");
                if (chargeIndicator != null && chargeIndicator.getText().equals("false")) {

                    //indicator == false --> BG0027
                    bg0027 = new BG0027InvoiceLineAllowances();
                    Element amount = findNamespaceChild(elemInvAll, namespacesInScope, "Amount");
                    if (amount != null) {
                        try {
                            BT0136InvoiceLineAllowanceAmount bt0136 = new BT0136InvoiceLineAllowanceAmount(strDblConverter.convert(amount.getText()));
                            bg0027.getBT0136InvoiceLineAllowanceAmount().add(bt0136);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element baseAmount = findNamespaceChild(elemInvAll, namespacesInScope, "BaseAmount");
                    if (baseAmount != null) {
                        try {
                            BT0137InvoiceLineAllowanceBaseAmount bt0137 = new BT0137InvoiceLineAllowanceBaseAmount(strDblConverter.convert(baseAmount.getText()));
                            bg0027.getBT0137InvoiceLineAllowanceBaseAmount().add(bt0137);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element multiplierFactorNumeric = findNamespaceChild(elemInvAll, namespacesInScope, "MultiplierFactorNumeric");
                    if (multiplierFactorNumeric != null) {
                        try {
                            BT0138InvoiceLineAllowancePercentage bt0138 = new BT0138InvoiceLineAllowancePercentage(strDblConverter.convert(multiplierFactorNumeric.getText()));
                            bg0027.getBT0138InvoiceLineAllowancePercentage().add(bt0138);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element allowanceChargeReason = findNamespaceChild(elemInvAll, namespacesInScope, "AllowanceChargeReason");
                    if (allowanceChargeReason != null) {
                        BT0139InvoiceLineAllowanceReason bt0139 = new BT0139InvoiceLineAllowanceReason(allowanceChargeReason.getText());
                        bg0027.getBT0139InvoiceLineAllowanceReason().add(bt0139);
                    }

                    Element allowanceChargeReasonCode = findNamespaceChild(elemInvAll, namespacesInScope, "AllowanceChargeReasonCode");
                    if (allowanceChargeReasonCode != null) {
                        try {
                            BT0140InvoiceLineAllowanceReasonCode bt0140 = new BT0140InvoiceLineAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.valueOf("Code" + allowanceChargeReasonCode.getText()));
                            bg0027.getBT0140InvoiceLineAllowanceReasonCode().add(bt0140);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Untdid5189ChargeAllowanceDescriptionCodes not found").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    bg0025.getBG0027InvoiceLineAllowances().add(bg0027);
                }

                if (chargeIndicator != null && chargeIndicator.getText().equals("true")) {
                    //indicator == true --> BG0028
                    bg0028 = new BG0028InvoiceLineCharges();
                    Element amount = findNamespaceChild(elemInvAll, namespacesInScope, "Amount");
                    if (amount != null) {
                        try {
                            BT0141InvoiceLineChargeAmount bt0141 = new BT0141InvoiceLineChargeAmount(strDblConverter.convert(amount.getText()));
                            bg0028.getBT0141InvoiceLineChargeAmount().add(bt0141);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    Element baseAmount = findNamespaceChild(elemInvAll, namespacesInScope, "BaseAmount");
                    if (baseAmount != null) {
                        try {
                            BT0142InvoiceLineChargeBaseAmount bt0142 = new BT0142InvoiceLineChargeBaseAmount(strDblConverter.convert(baseAmount.getText()));
                            bg0028.getBT0142InvoiceLineChargeBaseAmount().add(bt0142);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element multiplierFactorNumeric = findNamespaceChild(elemInvAll, namespacesInScope, "MultiplierFactorNumeric");
                    if (multiplierFactorNumeric != null) {
                        try {
                            BT0143InvoiceLineChargePercentage bt0143 = new BT0143InvoiceLineChargePercentage(strDblConverter.convert(multiplierFactorNumeric.getText()));
                            bg0028.getBT0143InvoiceLineChargePercentage().add(bt0143);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element allowanceChargeReason = findNamespaceChild(elemInvAll, namespacesInScope, "AllowanceChargeReason");
                    if (allowanceChargeReason != null) {
                        BT0144InvoiceLineChargeReason bt0144 = new BT0144InvoiceLineChargeReason(allowanceChargeReason.getText());
                        bg0028.getBT0144InvoiceLineChargeReason().add(bt0144);
                    }

                    Element allowanceChargeReasonCode = findNamespaceChild(elemInvAll, namespacesInScope, "AllowanceChargeReasonCode");
                    if (allowanceChargeReasonCode != null) {
                        try {
                            BT0145InvoiceLineChargeReasonCode bt0145 = new BT0145InvoiceLineChargeReasonCode(Untdid7161SpecialServicesCodes.valueOf(allowanceChargeReasonCode.getText()));
                            bg0028.getBT0145InvoiceLineChargeReasonCode().add(bt0145);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Untdid7161SpecialServicesCodes not found").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    bg0025.getBG0028InvoiceLineCharges().add(bg0028);
                }
            }

            //BG0029
            BG0029PriceDetails bg0029 = new BG0029PriceDetails();
            Element price = findNamespaceChild(elemInv, namespacesInScope, "Price");
            if (price != null) {

                Element priceAmount = findNamespaceChild(price, namespacesInScope, "PriceAmount");
                if (priceAmount != null) {
                    try {
                        BT0146ItemNetPrice bt0146 = new BT0146ItemNetPrice(strDblConverter.convert(priceAmount.getText()));
                        bg0029.getBT0146ItemNetPrice().add(bt0146);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element allowanceCharge = findNamespaceChild(price, namespacesInScope, "AllowanceCharge");
                if (allowanceCharge != null) {

                    Element chargeIndicatorAllowance = findNamespaceChild(allowanceCharge, namespacesInScope, "ChargeIndicator");
                    if (chargeIndicatorAllowance != null && chargeIndicatorAllowance.getText().equals("false")) {

                        Element amount = findNamespaceChild(allowanceCharge, namespacesInScope, "Amount");
                        if (amount != null) {
                            try {
                                BT0147ItemPriceDiscount bt0147 = new BT0147ItemPriceDiscount(strDblConverter.convert(amount.getText()));
                                bg0029.getBT0147ItemPriceDiscount().add(bt0147);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }

                        Element baseAmount = findNamespaceChild(allowanceCharge, namespacesInScope, "BaseAmount");
                        if (baseAmount != null) {
                            try {
                                BT0148ItemGrossPrice bt0148 = new BT0148ItemGrossPrice(strDblConverter.convert(baseAmount.getText()));
                                bg0029.getBT0148ItemGrossPrice().add(bt0148);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                    }
                }

                Element baseQuantity = findNamespaceChild(price, namespacesInScope, "BaseQuantity");
                if (baseQuantity != null) {
                    try {
                        BT0149ItemPriceBaseQuantity bt0149 = new BT0149ItemPriceBaseQuantity(strDblConverter.convert(baseQuantity.getText()));
                        bg0029.getBT0149ItemPriceBaseQuantity().add(bt0149);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }

                    Attribute baseQuantityAttribute = baseQuantity.getAttribute("unitCode");
                    if (baseQuantityAttribute != null) {

                        try {
                            String commonCode = baseQuantityAttribute.getValue();
                            UnitOfMeasureCodes unitCode = null;
                            for (UnitOfMeasureCodes elemUnitCode : UnitOfMeasureCodes.values()) {
                                if (elemUnitCode.getCommonCode().equals(commonCode)) {
                                    unitCode = elemUnitCode;
                                }
                            }
                            BT0150ItemPriceBaseQuantityUnitOfMeasureCode bt0150 = new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(unitCode);
                            bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(bt0150);
                        } catch (NullPointerException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("UnitOfMeasureCodes not found").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                } else {
                    BT0149ItemPriceBaseQuantity bt0149 = new BT0149ItemPriceBaseQuantity(1d);
                    bg0029.getBT0149ItemPriceBaseQuantity().add(bt0149);
                }
                bg0025.getBG0029PriceDetails().add(bg0029);
            }

            //BG0031
            BG0031ItemInformation bg0031 = null;
            BT0157ItemStandardIdentifierAndSchemeIdentifier bt0157 = null;
            Element item = findNamespaceChild(elemInv, namespacesInScope, "Item");
            if (item != null) {
                bg0031 = new BG0031ItemInformation();

                Element name = findNamespaceChild(item, namespacesInScope, "Name");
                if (name != null) {
                    BT0153ItemName bt0153 = new BT0153ItemName(name.getText());
                    bg0031.getBT0153ItemName().add(bt0153);
                }
                Element description = findNamespaceChild(item, namespacesInScope, "Description");
                if (description != null) {
                    BT0154ItemDescription bt0154 = new BT0154ItemDescription(description.getText());
                    bg0031.getBT0154ItemDescription().add(bt0154);
                }
                Element sellersItemIdentification = findNamespaceChild(item, namespacesInScope, "SellersItemIdentification");
                if (sellersItemIdentification != null) {
                    Element idSeller = findNamespaceChild(sellersItemIdentification, namespacesInScope, "ID");
                    if (idSeller != null) {
                        BT0155ItemSellerSIdentifier bt0155 = new BT0155ItemSellerSIdentifier(idSeller.getText());
                        bg0031.getBT0155ItemSellerSIdentifier().add(bt0155);
                    }
                }
                Element buyersItemIdentification = findNamespaceChild(item, namespacesInScope, "BuyersItemIdentification");
                if (buyersItemIdentification != null) {
                    Element idBuyer = findNamespaceChild(buyersItemIdentification, namespacesInScope, "ID");
                    if (idBuyer != null) {
                        BT0156ItemBuyerSIdentifier bt0156 = new BT0156ItemBuyerSIdentifier(idBuyer.getText());
                        bg0031.getBT0156ItemBuyerSIdentifier().add(bt0156);
                    }
                }
                Element standardItemIdentification = findNamespaceChild(item, namespacesInScope, "StandardItemIdentification");
                if (standardItemIdentification != null) {
                    Element idStandard = findNamespaceChild(standardItemIdentification, namespacesInScope, "ID");
                    if (idStandard != null) {
                        bt0157 = new BT0157ItemStandardIdentifierAndSchemeIdentifier(new Identifier(id.getAttributeValue("schemeID"), idStandard.getText()));
                        bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier().add(bt0157);
                    }
                }
                BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier bt0158 = null;
                List<Element> commodityClassifications = findNamespaceChildren(item, namespacesInScope, "CommodityClassification");
                for (Element elemComm : commodityClassifications) {
                    Element itemClassificationCode = findNamespaceChild(elemComm, namespacesInScope, "ItemClassificationCode");
                    if (itemClassificationCode != null) {
                        Attribute listID = id.getAttribute("listID");
                        Attribute listAgencyID = id.getAttribute("listVersionID");
                        if (listAgencyID != null) {
                            bt0158 = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(listID.getValue(), listAgencyID.getValue(), itemClassificationCode.getText()));
                        } else {
                            bt0158 = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(itemClassificationCode.getAttributeValue("listID"), itemClassificationCode.getText()));
                        }
                        bg0031.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().add(bt0158);
                    }
                }
                Element originCountry = findNamespaceChild(item, namespacesInScope, "OriginCountry");
                if (originCountry != null) {
                    Element identificationCode = findNamespaceChild(originCountry, namespacesInScope, "IdentificationCode");
                    if (identificationCode != null) {
                        try {
                            BT0159ItemCountryOfOrigin bt0159 = new BT0159ItemCountryOfOrigin(Iso31661CountryCodes.valueOf(identificationCode.getText()));
                            bg0031.getBT0159ItemCountryOfOrigin().add(bt0159);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Iso31661CountryCodes not found").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                }

                bg0025.getBG0031ItemInformation().add(bg0031);

                //BG0030
                BG0030LineVatInformation bg0030 = null;
                Element classifiedTaxCategory = findNamespaceChild(item, namespacesInScope, "ClassifiedTaxCategory");
                if (classifiedTaxCategory != null) {
                    bg0030 = new BG0030LineVatInformation();
                    Element idClassified = findNamespaceChild(classifiedTaxCategory, namespacesInScope, "ID");
                    if (idClassified != null) {
                        try {
                            BT0151InvoicedItemVatCategoryCode bt0151 = new BT0151InvoicedItemVatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(idClassified.getText()));
                            bg0030.getBT0151InvoicedItemVatCategoryCode().add(bt0151);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Untdid5305DutyTaxFeeCategories not found").action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element percent = findNamespaceChild(classifiedTaxCategory, namespacesInScope, "Percent");
                    if (percent != null) {
                        try {
                            BT0152InvoicedItemVatRate bt0152 = new BT0152InvoicedItemVatRate(strDblConverter.convert(percent.getText()));
                            bg0030.getBT0152InvoicedItemVatRate().add(bt0152);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }
                    bg0025.getBG0030LineVatInformation().add(bg0030);
                }

                //BG0032
                BG0032ItemAttributes bg0032 = null;
                List<Element> additionalItemProperties = findNamespaceChildren(item, namespacesInScope, "AdditionalItemProperty");
                for (Element elemAdd : additionalItemProperties) {
                    bg0032 = new BG0032ItemAttributes();

                    Element nameAdd = findNamespaceChild(elemAdd, namespacesInScope, "Name");
                    if (nameAdd != null) {
                        BT0160ItemAttributeName bt0160 = new BT0160ItemAttributeName(nameAdd.getText());
                        bg0032.getBT0160ItemAttributeName().add(bt0160);
                    }

                    Element valueAdd = findNamespaceChild(elemAdd, namespacesInScope, "Value");
                    if (valueAdd != null) {
                        BT0161ItemAttributeValue bt0161 = new BT0161ItemAttributeValue(valueAdd.getText());
                        bg0032.getBT0161ItemAttributeValue().add(bt0161);
                    }
                    bg0025.getBG0031ItemInformation(0).getBG0032ItemAttributes().add(bg0032);
                }
            }
            invoice.getBG0025InvoiceLine().add(bg0025);
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0025(document, cenInvoice, errors);
    }
}