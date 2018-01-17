package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
public class InvoiceLineConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice cenInvoice, List<IConversionIssue> errors) {

//            //bg0029
//            BG0029PriceDetails bg0029 = new BG0029PriceDetails();
////                    Double netBasisQuantityBT0149 = null;
//            Element netPriceProductTradePrice = findNamespaceChild(specifiedLineTradeAgreement, namespacesInScope, "NetPriceProductTradePrice");
//            if (netPriceProductTradePrice != null) {
//                Element chargeAmount = findNamespaceChild(netPriceProductTradePrice, namespacesInScope, "ChargeAmount");
//                if (chargeAmount != null) {
//                    try {
//                        BT0146ItemNetPrice bt0146 = new BT0146ItemNetPrice(strDblConverter.convert(chargeAmount.getText()));
//                        bg0029.getBT0146ItemNetPrice().add(bt0146);
//                    } catch (NumberFormatException | ConversionFailedException e) {
//                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
//                        errors.add(ConversionIssue.newError(ere));
//                    }
//                }
//
//
//
//            }
//            Element grossPriceProductTradePrice = findNamespaceChild(specifiedLineTradeAgreement, namespacesInScope, "GrossPriceProductTradePrice");
//            if (grossPriceProductTradePrice != null) {
//                Element appliedTradeAllowanceCharge = findNamespaceChild(grossPriceProductTradePrice, namespacesInScope, "AppliedTradeAllowanceCharge");
//                if (appliedTradeAllowanceCharge != null) {
//                    Element actualAmount = findNamespaceChild(appliedTradeAllowanceCharge, namespacesInScope, "ActualAmount");
//                    try {
//                        BT0147ItemPriceDiscount bt0147 = new BT0147ItemPriceDiscount(strDblConverter.convert(actualAmount.getText()));
//                        bg0029.getBT0147ItemPriceDiscount().add(bt0147);
//                    } catch (NumberFormatException | ConversionFailedException e) {
//                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
//                        errors.add(ConversionIssue.newError(ere));
//                    }
//                }
//
//                Element chargeAmount = findNamespaceChild(grossPriceProductTradePrice, namespacesInScope, "ChargeAmount");
//                if (chargeAmount != null) {
//                    try {
//                        BT0148ItemGrossPrice bt0148 = new BT0148ItemGrossPrice(strDblConverter.convert(chargeAmount.getText()));
//                        bg0029.getBT0148ItemGrossPrice().add(bt0148);
//                    } catch (NumberFormatException | ConversionFailedException e) {
//                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
//                        errors.add(ConversionIssue.newError(ere));
//                    }
//                }
//
//                Element grossBasisQuantity = findNamespaceChild(grossPriceProductTradePrice, namespacesInScope, "BasisQuantity");
//                if (grossBasisQuantity != null) {
//                    try {
//                        Double grossBasisQuantityTemp = strDblConverter.convert(grossBasisQuantity.getText());
//                        BT0149ItemPriceBaseQuantity bt0149 = new BT0149ItemPriceBaseQuantity(grossBasisQuantityTemp);
//                        bg0029.getBT0149ItemPriceBaseQuantity().add(bt0149);
//                    } catch (NumberFormatException | ConversionFailedException e) {
//                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
//                        errors.add(ConversionIssue.newError(ere));
//                    }
//
//                    Attribute basisAttribute = grossBasisQuantity.getAttribute("unitCode");
//                    if (basisAttribute != null) {
//                        try {
//                            String commonCode = basisAttribute.getValue();
//                            UnitOfMeasureCodes unitCode = null;
//                            for (UnitOfMeasureCodes elemUnitCode : UnitOfMeasureCodes.values()) {
//                                if (elemUnitCode.getCommonCode().equals(commonCode)) {
//                                    unitCode = elemUnitCode;
//                                }
//                            }
//                            BT0150ItemPriceBaseQuantityUnitOfMeasureCode bt0150 = new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(unitCode);
//                            bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(bt0150);
//                        } catch (NullPointerException e) {
//                            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("UnitOfMeasureCodes not found").action("InvoiceLineConverter").build());
//                            errors.add(ConversionIssue.newError(ere));
//                        }
//                    }
//                }
//            }
//
//
//            bg0025.getBG0029PriceDetails().add(bg0029);
//        }
//
//        //SpecifiedTradeProduct
//        //bg0031
//        BG0031ItemInformation bg0031;
//        Element specifiedTradeProduct = findNamespaceChild(includedSupplyChainTradeLineItem, namespacesInScope, "SpecifiedTradeProduct");
//        if (specifiedTradeProduct != null) {
//            bg0031 = new BG0031ItemInformation();
//
//            Element name = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "Name");
//            if (name != null) {
//                BT0153ItemName bt0153 = new BT0153ItemName(name.getText());
//                bg0031.getBT0153ItemName().add(bt0153);
//            }
//            Element description = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "Description");
//            if (description != null) {
//                BT0154ItemDescription bt0154 = new BT0154ItemDescription(description.getText());
//                bg0031.getBT0154ItemDescription().add(bt0154);
//            }
//            Element sellerAssignedID = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "SellerAssignedID");
//            if (sellerAssignedID != null) {
//                BT0155ItemSellerSIdentifier bt0155 = new BT0155ItemSellerSIdentifier(sellerAssignedID.getText());
//                bg0031.getBT0155ItemSellerSIdentifier().add(bt0155);
//            }
//            Element buyerAssignedID = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "BuyerAssignedID");
//            if (buyerAssignedID != null) {
//                BT0156ItemBuyerSIdentifier bt0156 = new BT0156ItemBuyerSIdentifier(buyerAssignedID.getText());
//                bg0031.getBT0156ItemBuyerSIdentifier().add(bt0156);
//            }
//            Element globalID = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "GlobalID");
//            BT0157ItemStandardIdentifierAndSchemeIdentifier bt0157 = null;
//            if (globalID != null) {
//                Attribute schemeID = globalID.getAttribute("schemeID");
//                if (schemeID != null) {
//                    bt0157 = new BT0157ItemStandardIdentifierAndSchemeIdentifier(new Identifier(globalID.getAttributeValue("schemeID"), globalID.getText()));
//                } else {
//                    bt0157 = new BT0157ItemStandardIdentifierAndSchemeIdentifier(new Identifier(globalID.getText()));
//                }
//                bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier().add(bt0157);
//            }
//
//            List<Element> designatedProductClassification = findNamespaceChildren(specifiedTradeProduct, namespacesInScope, "DesignatedProductClassification");
//            for (Element elemDesProd : designatedProductClassification) {
//                Element classCode = findNamespaceChild(elemDesProd, namespacesInScope, "ClassCode");
//                BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier bt0158 = null;
//                if (classCode != null) {
//                    Attribute listID = classCode.getAttribute("listID");
//                    Attribute listAgencyID = classCode.getAttribute("listVersionID");
//                    if (listID != null) {
//                        if (listAgencyID != null) {
//                            bt0158 = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(listID.getValue(), listAgencyID.getValue(), classCode.getText()));
//                        } else {
//                            bt0158 = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(listID.getValue(), classCode.getText()));
//                        }
//                    } else {
//                        bt0158 = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(classCode.getText()));
//                    }
//                    bg0031.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().add(bt0158);
//                }
//            }
//
//            Element originTradeCountry = findNamespaceChild(specifiedTradeProduct, namespacesInScope, "OriginTradeCountry");
//            if (originTradeCountry != null) {
//                Element id = findNamespaceChild(originTradeCountry, namespacesInScope, "ID");
//                if (id != null) {
//                    try {
//                        BT0159ItemCountryOfOrigin bt0159 = new BT0159ItemCountryOfOrigin(Iso31661CountryCodes.valueOf(id.getText()));
//                        bg0031.getBT0159ItemCountryOfOrigin().add(bt0159);
//                    } catch (IllegalArgumentException e) {
//                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Iso31661CountryCodes not found").action("InvoiceLineConverter").build());
//                        errors.add(ConversionIssue.newError(ere));
//                    }
//                }
//            }
//
//            BG0032ItemAttributes bg0032 = null;
//            List<Element> applicableProductCharacteristic = findNamespaceChildren(specifiedTradeProduct, namespacesInScope, "ApplicableProductCharacteristic");
//            for (Element elemAppProdChar : applicableProductCharacteristic) {
//                bg0032 = new BG0032ItemAttributes();
//
//                Element descriptionBG32 = findNamespaceChild(elemAppProdChar, namespacesInScope, "Description");
//                if (description != null) {
//                    BT0160ItemAttributeName bt0160 = new BT0160ItemAttributeName(descriptionBG32.getText());
//                    bg0032.getBT0160ItemAttributeName().add(bt0160);
//                }
//                Element value = findNamespaceChild(elemAppProdChar, namespacesInScope, "Value");
//                if (value != null) {
//                    BT0161ItemAttributeValue bt0161 = new BT0161ItemAttributeValue(value.getText());
//                    bg0032.getBT0161ItemAttributeValue().add(bt0161);
//                }
//                bg0031.getBG0032ItemAttributes().add(bg0032);
//            }
//            bg0025.getBG0031ItemInformation().add(bg0031);
//        }

        return null;
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        TypeConverter<Double, String> dblStrConverter = DoubleToStringConverter.newConverter("0.00");
        TypeConverter<LocalDate, String> dateStrConverter = JavaLocalDateToStringConverter.newConverter("yyyyMMdd");

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Namespace ramNs = rootElement.getNamespace("ram");
        Namespace udtNs = rootElement.getNamespace("udt");

        Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
        if (supplyChainTradeTransaction == null) {
            supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
            rootElement.addContent(supplyChainTradeTransaction);
        }

        for (BG0025InvoiceLine bg0025 : cenInvoice.getBG0025InvoiceLine()) {
            Element includedSupplyChainTradeLineItem = new Element("IncludedSupplyChainTradeLineItem", ramNs);

            // AssociatedDocumentLineDocument
            if (!bg0025.getBT0126InvoiceLineIdentifier().isEmpty() || !bg0025.getBT0127InvoiceLineNote().isEmpty()) {
                Element associatedDocumentLineDocument = null;
                associatedDocumentLineDocument = new Element("AssociatedDocumentLineDocument", ramNs);
                includedSupplyChainTradeLineItem.addContent(associatedDocumentLineDocument);
                if (!bg0025.getBT0126InvoiceLineIdentifier().isEmpty()) {
                    BT0126InvoiceLineIdentifier bt0126 = bg0025.getBT0126InvoiceLineIdentifier(0);
                    Element lineID = new Element("LineID", ramNs);
                    lineID.setText(bt0126.getValue());
                    associatedDocumentLineDocument.addContent(lineID);
                }
                if (!bg0025.getBT0127InvoiceLineNote().isEmpty()) {
                    BT0127InvoiceLineNote bt0127 = bg0025.getBT0127InvoiceLineNote(0);
                    Element includedNote = new Element("IncludedNote", ramNs);
                    Element content = new Element("Content", ramNs);
                    content.setText(bt0127.getValue());
                    includedNote.addContent(content);
                    associatedDocumentLineDocument.addContent(includedNote);
                }
            }

            Element specifiedLineTradeSettlement = new Element("SpecifiedLineTradeSettlement", ramNs);
            includedSupplyChainTradeLineItem.addContent(specifiedLineTradeSettlement);

            // AdditionalReferencedDocument
            if (!bg0025.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                Identifier bt0128 = bg0025.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(0).getValue();
                Element additionalReferencedDocument = new Element("AdditionalReferencedDocument", ramNs);

                Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                issuerAssignedID.setText(bt0128.getIdentifier());
                additionalReferencedDocument.addContent(issuerAssignedID);

                Element typeCode = new Element("TypeCode", ramNs);
                typeCode.setText("130");
                additionalReferencedDocument.addContent(typeCode);

                if (bt0128.getIdentificationSchema() != null) {
                    Element referenceTypeCode = new Element("ReferenceTypeCode", ramNs);
                    referenceTypeCode.setText(bt0128.getIdentificationSchema());
                    additionalReferencedDocument.addContent(referenceTypeCode);
                }
                specifiedLineTradeSettlement.addContent(additionalReferencedDocument);
            }

            // SpecifiedTradeSettlementLineMonetarySummation
            if (!bg0025.getBT0131InvoiceLineNetAmount().isEmpty()) {
                Double bt0131 = bg0025.getBT0131InvoiceLineNetAmount(0).getValue();
                Element lineTotalAmount = new Element("LineTotalAmount", ramNs);
                try {
                    lineTotalAmount.setText(dblStrConverter.convert(bt0131));
                    Element specifiedTradeSettlementLineMonetarySummation = new Element("SpecifiedTradeSettlementLineMonetarySummation", ramNs);
                    specifiedTradeSettlementLineMonetarySummation.addContent(lineTotalAmount);
                    specifiedLineTradeSettlement.addContent(specifiedTradeSettlementLineMonetarySummation);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            // ReceivableSpecifiedTradeAccountingAccount
            if (!bg0025.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                String bt0133 = bg0025.getBT0133InvoiceLineBuyerAccountingReference(0).getValue();
                Element receivableSpecifiedTradeAccountingAccount = new Element("ReceivableSpecifiedTradeAccountingAccount", ramNs);
                Element id = new Element("ID", ramNs);
                id.setText(bt0133);
                receivableSpecifiedTradeAccountingAccount.addContent(id);
                specifiedLineTradeSettlement.addContent(receivableSpecifiedTradeAccountingAccount);
            }

            // BillingSpecifiedPeriod
            if (!bg0025.getBG0026InvoiceLinePeriod().isEmpty()) {
                BG0026InvoiceLinePeriod bg0026 = bg0025.getBG0026InvoiceLinePeriod(0);
                Element billingSpecifiedPeriod = new Element("BillingSpecifiedPeriod", ramNs);

                if (!bg0026.getBT0134InvoiceLinePeriodStartDate().isEmpty()) {
                    LocalDate bt0134 = bg0026.getBT0134InvoiceLinePeriodStartDate(0).getValue();
                    Element startDateTime = new Element("StartDateTime", ramNs);
                    Element dateTimeString = new Element("DateTimeString", udtNs);
                    try {
                        dateTimeString.setText(dateStrConverter.convert(bt0134));
                        dateTimeString.setAttribute("format", "102");
                        startDateTime.addContent(dateTimeString);
                        billingSpecifiedPeriod.addContent(startDateTime);
                    } catch (IllegalArgumentException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0026.getBT0135InvoiceLinePeriodEndDate().isEmpty()) {
                    LocalDate bt0135 = bg0026.getBT0135InvoiceLinePeriodEndDate(0).getValue();
                    Element endDateTime = new Element("EndDateTime", ramNs);
                    Element dateTimeString = new Element("DateTimeString", udtNs);
                    try {
                        dateTimeString.setText(dateStrConverter.convert(bt0135));
                        dateTimeString.setAttribute("format", "102");
                        endDateTime.addContent(dateTimeString);
                        billingSpecifiedPeriod.addContent(endDateTime);
                    } catch (IllegalArgumentException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
                specifiedLineTradeSettlement.addContent(billingSpecifiedPeriod);
            }

            // SpecifiedTradeAllowanceCharge
            for (BG0027InvoiceLineAllowances bg0027 : bg0025.getBG0027InvoiceLineAllowances()) {
                Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", ramNs);
                Element chargeIndicator = new Element("ChargeIndicator", ramNs);
                Element indicator = new Element("Indicator", udtNs);
                indicator.setText("false");
                chargeIndicator.addContent(indicator);
                specifiedTradeAllowanceCharge.addContent(chargeIndicator);

                if (!bg0027.getBT0136InvoiceLineAllowanceAmount().isEmpty()) {
                    Double bt0136 = bg0027.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                    Element actualAmount = new Element("ActualAmount", ramNs);
                    try {
                        actualAmount.setText(dblStrConverter.convert(bt0136));
                        specifiedTradeAllowanceCharge.addContent(actualAmount);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0027.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty()) {
                    Double bt0137 = bg0027.getBT0137InvoiceLineAllowanceBaseAmount(0).getValue();
                    Element basisAmount = new Element("BasisAmount", ramNs);
                    try {
                        basisAmount.setText(dblStrConverter.convert(bt0137));
                        specifiedTradeAllowanceCharge.addContent(basisAmount);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0027.getBT0138InvoiceLineAllowancePercentage().isEmpty()) {
                    Double bt0138 = bg0027.getBT0138InvoiceLineAllowancePercentage(0).getValue();
                    Element calculationPercent = new Element("CalculationPercent", ramNs);
                    try {
                        calculationPercent.setText(dblStrConverter.convert(bt0138));
                        specifiedTradeAllowanceCharge.addContent(calculationPercent);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0027.getBT0139InvoiceLineAllowanceReason().isEmpty()) {
                    BT0139InvoiceLineAllowanceReason bt0139 = bg0027.getBT0139InvoiceLineAllowanceReason(0);
                    Element reason = new Element("Reason", ramNs);
                    reason.setText(bt0139.getValue());
                    specifiedTradeAllowanceCharge.addContent(reason);
                }

                if (!bg0027.getBT0140InvoiceLineAllowanceReasonCode().isEmpty()) {
                    Untdid5189ChargeAllowanceDescriptionCodes bt0140 = bg0027.getBT0140InvoiceLineAllowanceReasonCode(0).getValue();
                    Element reasonCode = new Element("ReasonCode", ramNs);
                    reasonCode.setText(String.valueOf(bt0140.getCode()));
                    specifiedTradeAllowanceCharge.addContent(reasonCode);
                }

                specifiedLineTradeSettlement.addContent(specifiedTradeAllowanceCharge);
            }
            for (BG0028InvoiceLineCharges bg0028 : bg0025.getBG0028InvoiceLineCharges()) {
                Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", ramNs);
                Element chargeIndicator = new Element("ChargeIndicator", ramNs);
                Element indicator = new Element("Indicator", udtNs);
                indicator.setText("true");
                chargeIndicator.addContent(indicator);
                specifiedTradeAllowanceCharge.addContent(chargeIndicator);

                if (!bg0028.getBT0141InvoiceLineChargeAmount().isEmpty()) {
                    Double bt0141 = bg0028.getBT0141InvoiceLineChargeAmount(0).getValue();
                    Element actualAmount = new Element("ActualAmount", ramNs);
                    try {
                        actualAmount.setText(dblStrConverter.convert(bt0141));
                        specifiedTradeAllowanceCharge.addContent(actualAmount);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0028.getBT0142InvoiceLineChargeBaseAmount().isEmpty()) {
                    Double bt0142 = bg0028.getBT0142InvoiceLineChargeBaseAmount(0).getValue();
                    Element basisAmount = new Element("BasisAmount", ramNs);
                    try {
                        basisAmount.setText(dblStrConverter.convert(bt0142));
                        specifiedTradeAllowanceCharge.addContent(basisAmount);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0028.getBT0143InvoiceLineChargePercentage().isEmpty()) {
                    Double bt0143 = bg0028.getBT0143InvoiceLineChargePercentage(0).getValue();
                    Element calculationPercent = new Element("CalculationPercent", ramNs);
                    try {
                        calculationPercent.setText(dblStrConverter.convert(bt0143));
                        specifiedTradeAllowanceCharge.addContent(calculationPercent);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0028.getBT0144InvoiceLineChargeReason().isEmpty()) {
                    BT0144InvoiceLineChargeReason bt0144 = bg0028.getBT0144InvoiceLineChargeReason(0);
                    Element reason = new Element("Reason", ramNs);
                    reason.setText(bt0144.getValue());
                    specifiedTradeAllowanceCharge.addContent(reason);
                }

                if (!bg0028.getBT0145InvoiceLineChargeReasonCode().isEmpty()) {
                    Untdid7161SpecialServicesCodes bt0145 = bg0028.getBT0145InvoiceLineChargeReasonCode(0).getValue();
                    Element reasonCode = new Element("ReasonCode", ramNs);
                    reasonCode.setText(bt0145.name());
                    specifiedTradeAllowanceCharge.addContent(reasonCode);
                }

                specifiedLineTradeSettlement.addContent(specifiedTradeAllowanceCharge);
            }

            // ApplicableTradeTax
            if (!bg0025.getBG0030LineVatInformation().isEmpty()) {
                BG0030LineVatInformation bg0030 = bg0025.getBG0030LineVatInformation(0);
                Element applicableTradeTax = new Element("ApplicableTradeTax", ramNs);

                if (!bg0030.getBT0151InvoicedItemVatCategoryCode().isEmpty()) {
                    Untdid5305DutyTaxFeeCategories bt0151 = bg0030.getBT0151InvoicedItemVatCategoryCode(0).getValue();
                    Element categoryCode = new Element("CategoryCode", ramNs);
                    categoryCode.setText(bt0151.name());
                    applicableTradeTax.addContent(categoryCode);
                }

                if (!bg0030.getBT0152InvoicedItemVatRate().isEmpty()) {
                    Double bt0152 = bg0030.getBT0152InvoicedItemVatRate(0).getValue();
                    Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                    try {
                        rateApplicablePercent.setText(dblStrConverter.convert(bt0152));
                        applicableTradeTax.addContent(rateApplicablePercent);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
                specifiedLineTradeSettlement.addContent(applicableTradeTax);
            }

            //SpecifiedLineTradeDelivery
            if (!bg0025.getBT0129InvoicedQuantity().isEmpty()) {
                Double bt0129 = bg0025.getBT0129InvoicedQuantity(0).getValue();
                Element specifiedLineTradeDelivery = new Element("SpecifiedLineTradeDelivery", ramNs);
                Element billedQuantity = new Element("BilledQuantity", ramNs);
                if (!bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty()) {
                    UnitOfMeasureCodes bt0130 = bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue();
                    billedQuantity.setAttribute("unitCode", bt0130.getCommonCode());
                }
                try {
                    billedQuantity.setText(dblStrConverter.convert(bt0129));
                    specifiedLineTradeDelivery.addContent(billedQuantity);
                    includedSupplyChainTradeLineItem.addContent(specifiedLineTradeDelivery);
                } catch (NumberFormatException | ConversionFailedException e) {
                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                    errors.add(ConversionIssue.newError(ere));
                }
            }

            //SpecifiedLineTradeAgreement
            Element specifiedLineTradeAgreement = new Element("SpecifiedLineTradeAgreement", ramNs);
            includedSupplyChainTradeLineItem.addContent(specifiedLineTradeAgreement);

            if (!bg0025.getBT0132ReferencedPurchaseOrderLineReference().isEmpty()) {
                BT0132ReferencedPurchaseOrderLineReference bt0132 = bg0025.getBT0132ReferencedPurchaseOrderLineReference(0);
                Element buyerOrderReferencedDocument = new Element("BuyerOrderReferencedDocument", ramNs);
                Element lineID = new Element("LineID", ramNs);
                lineID.setText(bt0132.getValue());
                buyerOrderReferencedDocument.addContent(lineID);
                specifiedLineTradeAgreement.addContent(buyerOrderReferencedDocument);
            }

            if (!bg0025.getBG0029PriceDetails().isEmpty()) {
                BG0029PriceDetails bg0029 = bg0025.getBG0029PriceDetails(0);

                if (!bg0029.getBT0146ItemNetPrice().isEmpty()) {
                    Double bt0146 = bg0029.getBT0146ItemNetPrice(0).getValue();
                    Element netPriceProductTradePrice = new Element("NetPriceProductTradePrice", ramNs);
                    Element chargeAmount = new Element("ChargeAmount", ramNs);
                    try {
                        chargeAmount.setText(dblStrConverter.convert(bt0146));
                        netPriceProductTradePrice.addContent(chargeAmount);
                        specifiedLineTradeAgreement.addContent(netPriceProductTradePrice);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element grossPriceProductTradePrice = new Element("GrossPriceProductTradePrice", ramNs);

                if (!bg0029.getBT0147ItemPriceDiscount().isEmpty()) {
                    Double bt0147 = bg0029.getBT0147ItemPriceDiscount(0).getValue();
                    Element appliedTradeAllowanceCharge = new Element("AppliedTradeAllowanceCharge", ramNs);
                    Element actualAmount = new Element("ActualAmount", ramNs);
                    try {
                        actualAmount.setText(dblStrConverter.convert(bt0147));
                        appliedTradeAllowanceCharge.addContent(actualAmount);
                        grossPriceProductTradePrice.addContent(appliedTradeAllowanceCharge);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0029.getBT0148ItemGrossPrice().isEmpty()) {
                    Double bt0148 = bg0029.getBT0148ItemGrossPrice(0).getValue();
                    Element chargeAmount = new Element("ChargeAmount", ramNs);
                    try {
                        chargeAmount.setText(dblStrConverter.convert(bt0148));
                        grossPriceProductTradePrice.addContent(chargeAmount);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                if (!bg0029.getBT0149ItemPriceBaseQuantity().isEmpty()) {
                    Double bt0149 = bg0029.getBT0149ItemPriceBaseQuantity(0).getValue();
                    Element basisQuantity = new Element("BasisQuantity", ramNs);
                    if (!bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                        UnitOfMeasureCodes bt0150 = bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue();
                        basisQuantity.setAttribute("unitCode", bt0150.getCommonCode());
                    }
                    try {
                        basisQuantity.setText(dblStrConverter.convert(bt0149));
                        grossPriceProductTradePrice.addContent(basisQuantity);
                    } catch (NumberFormatException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
                specifiedLineTradeAgreement.addContent(grossPriceProductTradePrice);
            }

            // SpecifiedTradeProduct
            if (!bg0025.getBG0031ItemInformation().isEmpty()) {
                BG0031ItemInformation bg0031 = bg0025.getBG0031ItemInformation(0);
                Element specifiedTradeProduct = new Element("SpecifiedTradeProduct", ramNs);

                if (!bg0031.getBT0153ItemName().isEmpty()) {
                    BT0153ItemName bt0153 = bg0031.getBT0153ItemName(0);
                    Element name = new Element("Name", ramNs);
                    name.setText(bt0153.getValue());
                    specifiedTradeProduct.addContent(name);
                }

                if (!bg0031.getBT0154ItemDescription().isEmpty()) {
                    BT0154ItemDescription bt0154 = bg0031.getBT0154ItemDescription(0);
                    Element description = new Element("Description", ramNs);
                    description.setText(bt0154.getValue());
                    specifiedTradeProduct.addContent(description);
                }

                if (!bg0031.getBT0155ItemSellerSIdentifier().isEmpty()) {
                    BT0155ItemSellerSIdentifier bt0155 = bg0031.getBT0155ItemSellerSIdentifier(0);
                    Element sellerAssignedID = new Element("SellerAssignedID", ramNs);
                    sellerAssignedID.setText(bt0155.getValue());
                    specifiedTradeProduct.addContent(sellerAssignedID);
                }

                if (!bg0031.getBT0156ItemBuyerSIdentifier().isEmpty()) {
                    BT0156ItemBuyerSIdentifier bt0156 = bg0031.getBT0156ItemBuyerSIdentifier(0);
                    Element buyerAssignedID = new Element("BuyerAssignedID", ramNs);
                    buyerAssignedID.setText(bt0156.getValue());
                    specifiedTradeProduct.addContent(buyerAssignedID);
                }

                if (!bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier().isEmpty()) {
                    Identifier bt0157 = bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier(0).getValue();
                    Element globalID = new Element("GlobalID", ramNs);
                    globalID.setText(bt0157.getIdentifier());
                    if (bt0157.getIdentificationSchema() != null) {
                        globalID.setAttribute("schemeID", bt0157.getIdentificationSchema());
                    }
                    specifiedTradeProduct.addContent(globalID);
                }

                for (BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier bt0158 : bg0031.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier()) {
                    String identifier = bt0158.getValue().getIdentifier();
                    String identificationSchema = bt0158.getValue().getIdentificationSchema();
                    String schemaVersion = bt0158.getValue().getSchemaVersion();

                    Element designatedProductClassification = new Element("DesignatedProductClassification", ramNs);
                    Element classCode = new Element("ClassCode", ramNs);
                    classCode.setText(identifier);

                    if (identificationSchema != null) {
                        classCode.setAttribute("listID", identificationSchema);
                    }
                    if (schemaVersion != null) {
                        classCode.setAttribute("listVersionID", schemaVersion);
                    }
                    designatedProductClassification.addContent(classCode);
                    specifiedTradeProduct.addContent(designatedProductClassification);
                }

                if (!bg0031.getBT0159ItemCountryOfOrigin().isEmpty()) {
                    Iso31661CountryCodes bt0159 = bg0031.getBT0159ItemCountryOfOrigin(0).getValue();
                    Element originTradeCountry = new Element("OriginTradeCountry", ramNs);
                    Element id = new Element("ID", ramNs);
                    id.setText(bt0159.getIso2charCode());
                    originTradeCountry.addContent(id);
                    specifiedTradeProduct.addContent(originTradeCountry);
                }

                for (BG0032ItemAttributes bg0032 : bg0031.getBG0032ItemAttributes()) {
                    Element applicableProductCharacteristic = new Element("ApplicableProductCharacteristic", ramNs);

                    if (!bg0032.getBT0160ItemAttributeName().isEmpty()) {
                        BT0160ItemAttributeName bt0160 = bg0032.getBT0160ItemAttributeName(0);
                        Element description = new Element("Description", ramNs);
                        description.setText(bt0160.getValue());
                        applicableProductCharacteristic.addContent(description);
                    }
                    if (!bg0032.getBT0161ItemAttributeValue().isEmpty()) {
                        BT0161ItemAttributeValue bt0161 = bg0032.getBT0161ItemAttributeValue(0);
                        Element value = new Element("Value", ramNs);
                        value.setText(bt0161.getValue());
                        applicableProductCharacteristic.addContent(value);
                    }

                    specifiedTradeProduct.addContent(applicableProductCharacteristic);
                }

                includedSupplyChainTradeLineItem.addContent(specifiedTradeProduct);
            }


            supplyChainTradeTransaction.addContent(includedSupplyChainTradeLineItem);
        }


    }

}
