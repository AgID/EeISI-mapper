package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
public class InvoiceLineConverter extends CustomConverterUtils implements CustomMapping<Document> {

    private final static Logger logger = LoggerFactory.getLogger(InvoiceLineConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
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
            supplyChainTradeTransaction.addContent(includedSupplyChainTradeLineItem);

            // AssociatedDocumentLineDocument
            Element associatedDocumentLineDocument = new Element("AssociatedDocumentLineDocument", ramNs);
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

            // SpecifiedTradeProduct
            if (!bg0025.getBG0031ItemInformation().isEmpty()) {
                BG0031ItemInformation bg0031 = bg0025.getBG0031ItemInformation(0);
                Element specifiedTradeProduct = new Element("SpecifiedTradeProduct", ramNs);

                if (!bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier().isEmpty()) {
                    Identifier bt0157 = bg0031.getBT0157ItemStandardIdentifierAndSchemeIdentifier(0).getValue();
                    Element globalID = new Element("GlobalID", ramNs);
                    globalID.setText(bt0157.getIdentifier());
                    if (bt0157.getIdentificationSchema() != null) {
                        globalID.setAttribute("schemeID", bt0157.getIdentificationSchema());
                    }
                    specifiedTradeProduct.addContent(globalID);
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

                includedSupplyChainTradeLineItem.addContent(specifiedTradeProduct);
            }

            //SpecifiedLineTradeAgreement
            Element specifiedLineTradeAgreement = specifiedLineTradeAgreement(errors, callingLocation, ramNs, bg0025, udtNs);

            Element specifiedLineTradeDelivery = specifiedLineTradeDelivery(errors, callingLocation, ramNs, bg0025);

            Element specifiedLineTradeSettlement = new Element("SpecifiedLineTradeSettlement", ramNs);


            // ApplicableTradeTax
            if (!bg0025.getBG0030LineVatInformation().isEmpty()) {
                BG0030LineVatInformation bg0030 = bg0025.getBG0030LineVatInformation(0);
                Element applicableTradeTax = new Element("ApplicableTradeTax", ramNs);

                if (!bg0030.getBT0151InvoicedItemVatCategoryCode().isEmpty()) {

                    Element typeCode = new Element("TypeCode", ramNs);
                    typeCode.setText("VAT");
                    applicableTradeTax.addContent(typeCode);

                    Untdid5305DutyTaxFeeCategories bt0151 = bg0030.getBT0151InvoicedItemVatCategoryCode(0).getValue();
                    Element categoryCode = new Element("CategoryCode", ramNs);
                    categoryCode.setText(bt0151.name());
                    applicableTradeTax.addContent(categoryCode);
                }
                if (!bg0030.getBT0152InvoicedItemVatRate().isEmpty()) {
                    BigDecimal bt0152 = bg0030.getBT0152InvoicedItemVatRate(0).getValue();
                    Element rateApplicablePercent = new Element("RateApplicablePercent", ramNs);
                    rateApplicablePercent.setText(bt0152.setScale(2, RoundingMode.HALF_UP).toString());
                    applicableTradeTax.addContent(rateApplicablePercent);
                }
                specifiedLineTradeSettlement.addContent(applicableTradeTax);
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
                        errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.INVALID,
                                e
                        )));
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
                        errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.INVALID,
                                e
                        )));
                    }
                }
                if (!billingSpecifiedPeriod.getChildren().isEmpty()) {
                    specifiedLineTradeSettlement.addContent(billingSpecifiedPeriod);
                }
            }


            // SpecifiedTradeAllowanceCharge
            // TAG Sequence
            // <ram:ChargeIndicator><udt:Indicator>false</udt:Indicator></ram:ChargeIndicator>
            // <ram:CalculationPercent>10.00</ram:CalculationPercent><!--BT-138-->
            // <ram:BasisAmount>147.00</ram:BasisAmount><!--BT-137-->
            // <ram:ActualAmount>14.7</ram:ActualAmount><!--BT-136-->
            // <ram:ReasonCode>66</ram:ReasonCode><!--BT-140-->
            // <ram:Reason>Sales discount</ram:Reason><!--BT-139-->
            for (BG0027InvoiceLineAllowances bg0027 : bg0025.getBG0027InvoiceLineAllowances()) {
                Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", ramNs);

                Element chargeIndicator = new Element("ChargeIndicator", ramNs);
                Element indicator = new Element("Indicator", udtNs);
                indicator.setText("false");
                chargeIndicator.addContent(indicator);
                specifiedTradeAllowanceCharge.addContent(chargeIndicator);

                if (!bg0027.getBT0138InvoiceLineAllowancePercentage().isEmpty()) {
                    BigDecimal bt0138 = bg0027.getBT0138InvoiceLineAllowancePercentage(0).getValue();
                    Element calculationPercent = new Element("CalculationPercent", ramNs);
                    try {
                        calculationPercent.setText(bt0138.setScale(2, RoundingMode.HALF_UP).toString());
                        specifiedTradeAllowanceCharge.addContent(calculationPercent);
                    } catch (NumberFormatException e) {
                        errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.INVALID,
                                e
                        )));
                    }
                }

                if (!bg0027.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty()) {
                    BigDecimal bt0137 = bg0027.getBT0137InvoiceLineAllowanceBaseAmount(0).getValue();
                    Element basisAmount = new Element("BasisAmount", ramNs);
                    try {
                        basisAmount.setText(bt0137.setScale(2, RoundingMode.HALF_UP).toString());
                        specifiedTradeAllowanceCharge.addContent(basisAmount);
                    } catch (NumberFormatException e) {
                        errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.INVALID,
                                e
                        )));
                    }
                }

                if (!bg0027.getBT0136InvoiceLineAllowanceAmount().isEmpty()) {
                    BigDecimal bt0136 = bg0027.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                    Element actualAmount = new Element("ActualAmount", ramNs);
                    actualAmount.setText(bt0136.setScale(2, RoundingMode.HALF_UP).toString());
                    specifiedTradeAllowanceCharge.addContent(actualAmount);
                }

                if (!bg0027.getBT0140InvoiceLineAllowanceReasonCode().isEmpty()) {
                    Untdid5189ChargeAllowanceDescriptionCodes bt0140 = bg0027.getBT0140InvoiceLineAllowanceReasonCode(0).getValue();
                    Element reasonCode = new Element("ReasonCode", ramNs);
                    reasonCode.setText(String.valueOf(bt0140.getCode()));
                    specifiedTradeAllowanceCharge.addContent(reasonCode);
                }

                if (!bg0027.getBT0139InvoiceLineAllowanceReason().isEmpty()) {
                    BT0139InvoiceLineAllowanceReason bt0139 = bg0027.getBT0139InvoiceLineAllowanceReason(0);
                    Element reason = new Element("Reason", ramNs);
                    reason.setText(bt0139.getValue());
                    specifiedTradeAllowanceCharge.addContent(reason);
                }

                specifiedLineTradeSettlement.addContent(specifiedTradeAllowanceCharge);
            }

            // SpecifiedTradeAllowanceCharge
            // TAG Sequence
            // <ram:ChargeIndicator><udt:Indicator>false</udt:Indicator></ram:ChargeIndicator>
            // <ram:CalculationPercent>10.00</ram:CalculationPercent>
            // <ram:BasisAmount>147.00</ram:BasisAmount>
            // <ram:ActualAmount>14.7</ram:ActualAmount>
            // <ram:ReasonCode>66</ram:ReasonCode>
            // <ram:Reason>Sales discount</ram:Reason>
            for (BG0028InvoiceLineCharges bg0028 : bg0025.getBG0028InvoiceLineCharges()) {
                Element specifiedTradeAllowanceCharge = new Element("SpecifiedTradeAllowanceCharge", ramNs);

                Element chargeIndicator = new Element("ChargeIndicator", ramNs);
                Element indicator = new Element("Indicator", udtNs);
                indicator.setText("true");
                chargeIndicator.addContent(indicator);
                specifiedTradeAllowanceCharge.addContent(chargeIndicator);

                if (!bg0028.getBT0143InvoiceLineChargePercentage().isEmpty()) {
                    BigDecimal bt0143 = bg0028.getBT0143InvoiceLineChargePercentage(0).getValue();
                    Element calculationPercent = new Element("CalculationPercent", ramNs);
                    calculationPercent.setText(bt0143.setScale(2, RoundingMode.HALF_UP).toString());
                    specifiedTradeAllowanceCharge.addContent(calculationPercent);
                }

                if (!bg0028.getBT0142InvoiceLineChargeBaseAmount().isEmpty()) {
                    BigDecimal bt0142 = bg0028.getBT0142InvoiceLineChargeBaseAmount(0).getValue();
                    Element basisAmount = new Element("BasisAmount", ramNs);
                    basisAmount.setText(bt0142.setScale(2, RoundingMode.HALF_UP).toString());
                    specifiedTradeAllowanceCharge.addContent(basisAmount);
                }

                if (!bg0028.getBT0141InvoiceLineChargeAmount().isEmpty()) {
                    BigDecimal bt0141 = bg0028.getBT0141InvoiceLineChargeAmount(0).getValue();
                    Element actualAmount = new Element("ActualAmount", ramNs);
                    actualAmount.setText(bt0141.setScale(2, RoundingMode.HALF_UP).toString());
                    specifiedTradeAllowanceCharge.addContent(actualAmount);
                }

                if (!bg0028.getBT0145InvoiceLineChargeReasonCode().isEmpty()) {
                    Untdid7161SpecialServicesCodes bt0145 = bg0028.getBT0145InvoiceLineChargeReasonCode(0).getValue();
                    Element reasonCode = new Element("ReasonCode", ramNs);
                    reasonCode.setText(bt0145.name());
                    specifiedTradeAllowanceCharge.addContent(reasonCode);
                }

                if (!bg0028.getBT0144InvoiceLineChargeReason().isEmpty()) {
                    BT0144InvoiceLineChargeReason bt0144 = bg0028.getBT0144InvoiceLineChargeReason(0);
                    Element reason = new Element("Reason", ramNs);
                    reason.setText(bt0144.getValue());
                    specifiedTradeAllowanceCharge.addContent(reason);
                }


                specifiedLineTradeSettlement.addContent(specifiedTradeAllowanceCharge);
            }

            // SpecifiedTradeSettlementLineMonetarySummation
            if (!bg0025.getBT0131InvoiceLineNetAmount().isEmpty()) {
                BigDecimal bt0131 = bg0025.getBT0131InvoiceLineNetAmount(0).getValue();
                Element lineTotalAmount = new Element("LineTotalAmount", ramNs);
                lineTotalAmount.setText(bt0131.setScale(2, RoundingMode.HALF_UP).toString());
                Element specifiedTradeSettlementLineMonetarySummation = new Element("SpecifiedTradeSettlementLineMonetarySummation", ramNs);
                specifiedTradeSettlementLineMonetarySummation.addContent(lineTotalAmount);
                specifiedLineTradeSettlement.addContent(specifiedTradeSettlementLineMonetarySummation);
            }

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

            // ReceivableSpecifiedTradeAccountingAccount
            if (!bg0025.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                String bt0133 = bg0025.getBT0133InvoiceLineBuyerAccountingReference(0).getValue();
                Element receivableSpecifiedTradeAccountingAccount = new Element("ReceivableSpecifiedTradeAccountingAccount", ramNs);
                Element id = new Element("ID", ramNs);
                id.setText(bt0133);
                receivableSpecifiedTradeAccountingAccount.addContent(id);
                specifiedLineTradeSettlement.addContent(receivableSpecifiedTradeAccountingAccount);
            }

            if (!specifiedLineTradeAgreement.getChildren().isEmpty()) {
                includedSupplyChainTradeLineItem.addContent(specifiedLineTradeAgreement);
            }

            if (specifiedLineTradeDelivery != null) {
                includedSupplyChainTradeLineItem.addContent(specifiedLineTradeDelivery);
            }

            if (!specifiedLineTradeSettlement.getChildren().isEmpty()) {
                includedSupplyChainTradeLineItem.addContent(specifiedLineTradeSettlement);
            }


        }

        logger.error("{}, {}", supplyChainTradeTransaction.getName(), supplyChainTradeTransaction.getChildren());
    }


    private Element specifiedLineTradeDelivery(List<IConversionIssue> errors, ErrorCode.Location callingLocation, Namespace ramNs, BG0025InvoiceLine bg0025) {
        Element specifiedLineTradeDelivery = null;
        if (!bg0025.getBT0129InvoicedQuantity().isEmpty()) {
            BigDecimal bt0129 = bg0025.getBT0129InvoicedQuantity(0).getValue();
            specifiedLineTradeDelivery = new Element("SpecifiedLineTradeDelivery", ramNs);
            Element billedQuantity = new Element("BilledQuantity", ramNs);
            if (!bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty()) {
                UnitOfMeasureCodes bt0130 = bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue();
                billedQuantity.setAttribute("unitCode", bt0130.getCommonCode());
            }
            billedQuantity.setText(bt0129.setScale(2, RoundingMode.HALF_UP).toString());
            specifiedLineTradeDelivery.addContent(billedQuantity);
        }
        return specifiedLineTradeDelivery;
    }


    private Element specifiedLineTradeAgreement(List<IConversionIssue> errors, ErrorCode.Location callingLocation, Namespace ramNs, BG0025InvoiceLine bg0025, Namespace udtNs) {
        Element specifiedLineTradeAgreement = new Element("SpecifiedLineTradeAgreement", ramNs);

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

            Element grossPriceProductTradePrice = new Element("GrossPriceProductTradePrice", ramNs);


            if (!bg0029.getBT0148ItemGrossPrice().isEmpty()) {
                Element chargeAmount = new Element("ChargeAmount", ramNs);
                BigDecimal bt0148 = bg0029.getBT0148ItemGrossPrice(0).getValue();
                chargeAmount.setText(bt0148.setScale(2, RoundingMode.HALF_UP).toString());
                grossPriceProductTradePrice.addContent(chargeAmount);
            } else if (!bg0029.getBT0146ItemNetPrice().isEmpty()) {
                Element chargeAmount = new Element("ChargeAmount", ramNs);
                final BigDecimal net = bg0029.getBT0146ItemNetPrice(0).getValue();
                if (!bg0029.getBT0147ItemPriceDiscount().isEmpty()) {
                    final BigDecimal discount = bg0029.getBT0147ItemPriceDiscount(0).getValue();
                    chargeAmount.setText(net.subtract(discount).setScale(2, RoundingMode.HALF_UP).toString());
                    grossPriceProductTradePrice.addContent(chargeAmount);
                }
            }

            mapBt149Bt150(bg0029, ramNs, grossPriceProductTradePrice);

            if (!bg0029.getBT0147ItemPriceDiscount().isEmpty()) {
                BigDecimal bt0147 = bg0029.getBT0147ItemPriceDiscount(0).getValue();
                Element appliedTradeAllowanceCharge = new Element("AppliedTradeAllowanceCharge", ramNs);

                Element chargeIndicator = new Element("ChargeIndicator", ramNs);
                Element indicator = new Element("Indicator", udtNs);
                indicator.setText("false");
                chargeIndicator.addContent(indicator);
                appliedTradeAllowanceCharge.addContent(chargeIndicator);

                Element actualAmount = new Element("ActualAmount", ramNs);
                actualAmount.setText(bt0147.setScale(2, RoundingMode.HALF_UP).toString());
                appliedTradeAllowanceCharge.addContent(actualAmount);
                grossPriceProductTradePrice.addContent(appliedTradeAllowanceCharge);
            }

            Element netPriceProductTradePrice = null;
            if (!bg0029.getBT0146ItemNetPrice().isEmpty()) {
                BigDecimal bt0146 = bg0029.getBT0146ItemNetPrice(0).getValue();
                netPriceProductTradePrice = new Element("NetPriceProductTradePrice", ramNs);
                Element chargeAmount = new Element("ChargeAmount", ramNs);
                chargeAmount.setText(bt0146.setScale(2, RoundingMode.HALF_UP).toString());
                netPriceProductTradePrice.addContent(chargeAmount);

                mapBt149Bt150(bg0029, ramNs, netPriceProductTradePrice);
            }

            if (grossPriceProductTradePrice.getChild("ChargeAmount", ramNs) != null) {
                specifiedLineTradeAgreement.addContent(grossPriceProductTradePrice);
            }

            if (netPriceProductTradePrice != null) {
                specifiedLineTradeAgreement.addContent(netPriceProductTradePrice);
            }
        }
        return specifiedLineTradeAgreement;
    }

    private void mapBt149Bt150(BG0029PriceDetails bg0029, Namespace ramNs, Element target) {
        if (!bg0029.getBT0149ItemPriceBaseQuantity().isEmpty()) {
            BigDecimal bt0149 = bg0029.getBT0149ItemPriceBaseQuantity(0).getValue();
            Element basisQuantity = new Element("BasisQuantity", ramNs);
            if (!bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                UnitOfMeasureCodes bt0150 = bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue();
                basisQuantity.setAttribute("unitCode", bt0150.getCommonCode());
            }
            basisQuantity.setText(bt0149.setScale(2, RoundingMode.HALF_UP).toString());
            target.addContent(basisQuantity);
        }
    }

}
