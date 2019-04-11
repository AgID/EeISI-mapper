package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid1153ReferenceQualifierCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class InvoiceLineConverter implements CustomMapping<Document> {

    private static TypeConverter<String, Untdid1153ReferenceQualifierCode> untdid1153Converter = LookUpEnumConversion.newConverter(Untdid1153ReferenceQualifierCode.class);
    private static final TypeConverter<LocalDate, String> dateConverter = JavaLocalDateToStringConverter.newConverter();

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0025InvoiceLine().isEmpty()) {

                Iso4217CurrenciesFundsCodes currencyCode = null;
                if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
                    BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
                    currencyCode = bt0005.getValue();
                }

                List<BG0025InvoiceLine> bg0025 = cenInvoice.getBG0025InvoiceLine();
                for (BG0025InvoiceLine elemBg25 : bg0025) {
                    Element invoiceLine = new Element("InvoiceLine");
                    if (!elemBg25.getBT0126InvoiceLineIdentifier().isEmpty()) {
                        BT0126InvoiceLineIdentifier bt0126 = elemBg25.getBT0126InvoiceLineIdentifier(0);
                        Element id = new Element("ID");
                        id.setText(bt0126.getValue());
                        invoiceLine.addContent(id);
                    }

                    if (!elemBg25.getBT0127InvoiceLineNote().isEmpty()) {
                        BT0127InvoiceLineNote bt0127 = elemBg25.getBT0127InvoiceLineNote(0);
                        Element id = new Element("Note");
                        id.setText(bt0127.getValue());
                        invoiceLine.addContent(id);
                    }

                    if (!elemBg25.getBT0129InvoicedQuantity().isEmpty()) {
                        BigDecimal quantity = elemBg25.getBT0129InvoicedQuantity().isEmpty() ? BigDecimal.ZERO : elemBg25.getBT0129InvoicedQuantity(0).getValue();
                        Element invoicedQuantity = new Element("InvoicedQuantity");
                        invoicedQuantity.setText(quantity.setScale(8, RoundingMode.HALF_UP).toString());

                        if (!elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty()) {
                            BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode(0);
                            if (bt0130 != null) {
                                UnitOfMeasureCodes unitOfMeasureCodes = bt0130.getValue();
                                Attribute unitCode = new Attribute("unitCode", unitOfMeasureCodes.getCommonCode());
                                invoicedQuantity.setAttribute(unitCode);
                            }
                        }
                        invoiceLine.addContent(invoicedQuantity);
                    }

                    if (!elemBg25.getBT0131InvoiceLineNetAmount().isEmpty()) {
                        BT0131InvoiceLineNetAmount bt0131 = elemBg25.getBT0131InvoiceLineNetAmount(0);
                        Element lineExtensionAmount = new Element("LineExtensionAmount");
                        final BigDecimal value = bt0131.getValue();
                        lineExtensionAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                        if (currencyCode != null) {
                            lineExtensionAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
                        }
                        invoiceLine.addContent(lineExtensionAmount);
                    }

                    if (!elemBg25.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                        BT0133InvoiceLineBuyerAccountingReference bt133 = elemBg25.getBT0133InvoiceLineBuyerAccountingReference().get(0);
                        Element accountingCost = new Element("AccountingCost");
                        accountingCost.setText(bt133.getValue());
                        invoiceLine.addContent(accountingCost);
                    }

                    if (!elemBg25.getBG0026InvoiceLinePeriod().isEmpty()) {
                        List<BG0026InvoiceLinePeriod> bg0026s = elemBg25.getBG0026InvoiceLinePeriod();
                        for (BG0026InvoiceLinePeriod bg0026 : bg0026s) {
                            LocalDate startDate = evalExpression(() -> bg0026.getBT0134InvoiceLinePeriodStartDate(0).getValue());
                            LocalDate endDate = evalExpression(() -> bg0026.getBT0135InvoiceLinePeriodEndDate(0).getValue());
                            if (startDate == null && endDate == null) continue;
                            Element invoicePeriodElm = new Element("InvoicePeriod");
                            setDateChild(errors, callingLocation, invoicePeriodElm, startDate, "StartDate");
                            setDateChild(errors, callingLocation, invoicePeriodElm, endDate, "EndDate");
                            invoiceLine.addContent(invoicePeriodElm);
                        }
                    }

                    if (!elemBg25.getBT0132ReferencedPurchaseOrderLineReference().isEmpty()) {
                        BT0132ReferencedPurchaseOrderLineReference bt132 = elemBg25.getBT0132ReferencedPurchaseOrderLineReference().get(0);
                        Element orderLineReference = new Element("OrderLineReference");
                        Element lineID = new Element("LineID");
                        lineID.setText(bt132.getValue());
                        orderLineReference.addContent(lineID);
                        invoiceLine.addContent(orderLineReference);
                    }

                    if (!elemBg25.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = elemBg25.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(0);
                        String idValue = bt0128.getValue().getIdentifier();
                        String idValueScheme = bt0128.getValue().getIdentificationSchema() != null
                                ? bt0128.getValue().getIdentificationSchema()
                                : "";
                        idValueScheme = untdid1153Converter.safeConvert(idValueScheme).orElse(Untdid1153ReferenceQualifierCode.ZZZ).name();
                        String typeCode = "130";
                        Element ublDocumentReferenceXml = new Element("DocumentReference");
                        Element ublDocumentTypeCodeXml = new Element("DocumentTypeCode");
                        ublDocumentTypeCodeXml.setText(typeCode);
                        Element ublIdXml = new Element("ID");
                        ublIdXml.setText(idValue);
                        ublIdXml.setAttribute("schemeID", idValueScheme);
                        ublDocumentReferenceXml.addContent(ublIdXml);
                        ublDocumentReferenceXml.addContent(ublDocumentTypeCodeXml);
                        invoiceLine.addContent(ublDocumentReferenceXml);
                    }

                    if (!elemBg25.getBG0027InvoiceLineAllowances().isEmpty()) {
                        BG0027InvoiceLineAllowances elemBg27 = elemBg25.getBG0027InvoiceLineAllowances(0);
                        Element allowanceCharge = new Element("AllowanceCharge");
                        Element chargeIndicator = new Element("ChargeIndicator");
                        chargeIndicator.setText("false");
                        allowanceCharge.addContent(chargeIndicator);

                        if (!elemBg27.getBT0140InvoiceLineAllowanceReasonCode().isEmpty()) {
                            int value = elemBg27.getBT0140InvoiceLineAllowanceReasonCode().get(0).getValue().getCode();
                            Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
                            allowanceChargeReasonCode.setText(value + "");
                            allowanceCharge.addContent(allowanceChargeReasonCode);
                        }

                        if (!elemBg27.getBT0139InvoiceLineAllowanceReason().isEmpty()) {
                            String value = elemBg27.getBT0139InvoiceLineAllowanceReason(0).getValue();
                            Element allowanceChargeReason = new Element("AllowanceChargeReason");
                            allowanceChargeReason.setText(value);
                            allowanceCharge.addContent(allowanceChargeReason);
                        }

                        if (!elemBg27.getBT0138InvoiceLineAllowancePercentage().isEmpty()) {
                            Identifier value = elemBg27.getBT0138InvoiceLineAllowancePercentage(0).getValue();
                            Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                            if (value != null && value.getIdentifier() != null) {
                                multiplierFactorNumeric.setText(value.getIdentifier());
                            }
                            allowanceCharge.addContent(multiplierFactorNumeric);
                        }

                        if (!elemBg27.getBT0136InvoiceLineAllowanceAmount().isEmpty()) {
                            BigDecimal value = elemBg27.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                            Element amount = new Element("Amount");
                            amount.setText(value.toString());
                            if (currencyCode != null && currencyCode.getCode() != null)
                                amount.setAttribute("currencyID", currencyCode.getCode());
                            allowanceCharge.addContent(amount);
                        }

                        if (!elemBg27.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty()) {
                            Identifier value = elemBg27.getBT0137InvoiceLineAllowanceBaseAmount(0).getValue();
                            Element baseAmount = new Element("BaseAmount");
                            if (value != null && value.getIdentifier() != null) {
                                baseAmount.setText(value.getIdentifier());
                            }
                            if (currencyCode != null && currencyCode.getCode() != null) {
                                baseAmount.setAttribute("currencyID", currencyCode.getCode());
                            }
                            allowanceCharge.addContent(baseAmount);
                        }

                        invoiceLine.addContent(allowanceCharge);
                    }

                    if (!elemBg25.getBG0028InvoiceLineCharges().isEmpty()) {
                        BG0028InvoiceLineCharges elemBg28 = elemBg25.getBG0028InvoiceLineCharges(0);
                        Element allowanceCharge = new Element("AllowanceCharge");
                        Element chargeIndicator = new Element("ChargeIndicator");
                        chargeIndicator.setText("false");
                        allowanceCharge.addContent(chargeIndicator);

                        if (!elemBg28.getBT0145InvoiceLineChargeReasonCode().isEmpty()) {
                            String value = elemBg28.getBT0145InvoiceLineChargeReasonCode().get(0).getValue().name();
                            Element allowanceChargeReasonCode = new Element("AllowanceChargeReasonCode");
                            allowanceChargeReasonCode.setText(value);
                            allowanceCharge.addContent(allowanceChargeReasonCode);
                        }

                        if (!elemBg28.getBT0144InvoiceLineChargeReason().isEmpty()) {
                            String value = elemBg28.getBT0144InvoiceLineChargeReason(0).getValue();
                            Element allowanceChargeReason = new Element("AllowanceChargeReason");
                            allowanceChargeReason.setText(value);
                            allowanceCharge.addContent(allowanceChargeReason);
                        }

                        if (!elemBg28.getBT0143InvoiceLineChargePercentage().isEmpty()) {
                            BigDecimal value = elemBg28.getBT0143InvoiceLineChargePercentage(0).getValue();
                            Element multiplierFactorNumeric = new Element("MultiplierFactorNumeric");
                            multiplierFactorNumeric.setText(value.toString());
                            allowanceCharge.addContent(multiplierFactorNumeric);
                        }

                        if (!elemBg28.getBT0141InvoiceLineChargeAmount().isEmpty()) {
                            BigDecimal value = elemBg28.getBT0141InvoiceLineChargeAmount(0).getValue();
                            Element amount = new Element("Amount");
                            if (currencyCode != null && currencyCode.getCode() != null)
                                amount.setAttribute("currencyID", currencyCode.getCode());
                            amount.setText(value.toString());
                            allowanceCharge.addContent(amount);
                        }

                        if (!elemBg28.getBT0142InvoiceLineChargeBaseAmount().isEmpty()) {
                            BigDecimal value = elemBg28.getBT0142InvoiceLineChargeBaseAmount(0).getValue();
                            Element baseAmount = new Element("BaseAmount");
                            if (currencyCode != null && currencyCode.getCode() != null) {
                                baseAmount.setAttribute("currencyID", currencyCode.getCode());
                            }
                            baseAmount.setText(value.toString());
                            allowanceCharge.addContent(baseAmount);
                        }

                        invoiceLine.addContent(allowanceCharge);
                    }

                    if (!elemBg25.getBG0031ItemInformation().isEmpty()) {
                        List<BG0031ItemInformation> bg0031 = elemBg25.getBG0031ItemInformation();
                        for (BG0031ItemInformation elemBg31 : bg0031) {
                            Element item = new Element("Item");

                            if (!elemBg31.getBT0154ItemDescription().isEmpty()) {
                                BT0154ItemDescription bt0154 = elemBg31.getBT0154ItemDescription(0);
                                Element description = new Element("Description");
                                description.setText(bt0154.getValue());
                                item.addContent(description);
                            }

                            if (!elemBg31.getBT0153ItemName().isEmpty()) {
                                BT0153ItemName bt0153 = elemBg31.getBT0153ItemName(0);
                                Element name = new Element("Name");
                                name.setText(bt0153.getValue());
                                item.addContent(name);
                            }

                            if (!elemBg31.getBT0156ItemBuyerSIdentifier().isEmpty()) {
                                BT0156ItemBuyerSIdentifier bt0156 = elemBg31.getBT0156ItemBuyerSIdentifier(0);
                                Element buyersItemIdentification = new Element("BuyersItemIdentification");
                                Element buyersItemIdentificationId = new Element("ID");
                                buyersItemIdentificationId.setText(bt0156.getValue());
                                buyersItemIdentification.addContent(buyersItemIdentificationId);
                                item.addContent(buyersItemIdentification);
                            }

                            if (!elemBg31.getBT0155ItemSellerSIdentifier().isEmpty()) {
                                BT0155ItemSellerSIdentifier bt0155 = elemBg31.getBT0155ItemSellerSIdentifier(0);
                                Element sellersItemIdentification = new Element("SellersItemIdentification");
                                Element sellersItemIdentificationId = new Element("ID");
                                sellersItemIdentificationId.setText(bt0155.getValue());
                                sellersItemIdentification.addContent(sellersItemIdentificationId);
                                item.addContent(sellersItemIdentification);
                            }

                            if (!elemBg31.getBT0157ItemStandardIdentifierAndSchemeIdentifier().isEmpty()) {
                                BT0157ItemStandardIdentifierAndSchemeIdentifier bt0157 = elemBg31.getBT0157ItemStandardIdentifierAndSchemeIdentifier(0);
                                Element standardItemIdentification = new Element("StandardItemIdentification");
                                Element standardItemIdentificationId = new Element("ID");
                                if (bt0157.getValue() != null && bt0157.getValue().getIdentifier() != null) {
                                    standardItemIdentificationId.setText(bt0157.getValue().getIdentifier());
                                }
                                if (bt0157.getValue() != null && bt0157.getValue().getIdentificationSchema() != null) {
                                    standardItemIdentificationId.setAttribute("schemeID", bt0157.getValue().getIdentificationSchema());
                                }
                                standardItemIdentification.addContent(standardItemIdentificationId);
                                item.addContent(standardItemIdentification);
                            }

                            if (!elemBg31.getBT0159ItemCountryOfOrigin().isEmpty()) {
                                BT0159ItemCountryOfOrigin bt0159 = elemBg31.getBT0159ItemCountryOfOrigin(0);
                                Element originCountry = new Element("OriginCountry");
                                Element originCountryIdentificationCode = new Element("IdentificationCode");
                                originCountryIdentificationCode.setText(bt0159.getValue().getIso2charCode());
                                originCountry.addContent(originCountryIdentificationCode);
                                item.addContent(originCountry);
                            }

                            if (!elemBg31.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().isEmpty()) {
                                List<BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier> bt0158list =
                                        elemBg31.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier();
                                for (BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier bt0158 : bt0158list) {
                                    Element commodityClassification = new Element("CommodityClassification");
                                    Element commodityClassificationId = new Element("ItemClassificationCode");
                                    if (bt0158.getValue() != null && bt0158.getValue().getIdentifier() != null) {
                                        commodityClassificationId.setText(bt0158.getValue().getIdentifier());
                                    }
                                    if (bt0158.getValue() != null && bt0158.getValue().getIdentificationSchema() != null) {
                                        commodityClassificationId.setAttribute("listID", bt0158.getValue().getIdentificationSchema());
                                    }
                                    if (bt0158.getValue() != null && bt0158.getValue().getIdentificationSchema() != null) {
                                        commodityClassificationId.setAttribute("listVersionID", bt0158.getValue().getSchemaVersion());
                                    }
                                    commodityClassification.addContent(commodityClassificationId);
                                    item.addContent(commodityClassification);
                                }
                            }

                            if (!elemBg25.getBG0030LineVatInformation().isEmpty()) {
                                List<BG0030LineVatInformation> bg0030 = elemBg25.getBG0030LineVatInformation();
                                for (BG0030LineVatInformation elemBg30 : bg0030) {
                                    Element classifiedTaxCategory = new Element("ClassifiedTaxCategory");
                                    if (!elemBg30.getBT0151InvoicedItemVatCategoryCode().isEmpty()) {
                                        BT0151InvoicedItemVatCategoryCode bt0151 = elemBg30.getBT0151InvoicedItemVatCategoryCode(0);
                                        Element id = new Element("ID");
                                        Untdid5305DutyTaxFeeCategories dutyTaxFeeCategories = bt0151.getValue();
                                        id.setText(dutyTaxFeeCategories.name());
                                        classifiedTaxCategory.addContent(id);
                                    }
                                    if (!elemBg30.getBT0152InvoicedItemVatRate().isEmpty()) {
                                        BT0152InvoicedItemVatRate bt0152 = elemBg30.getBT0152InvoicedItemVatRate(0);
                                        Element percent = new Element("Percent");
                                        final BigDecimal value = bt0152.getValue();
                                        percent.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                                        classifiedTaxCategory.addContent(percent);
                                    }
                                    Element taxScheme = new Element("TaxScheme");
                                    Element id = new Element("ID");
                                    id.setText("VAT");
                                    taxScheme.addContent(id);
                                    classifiedTaxCategory.addContent(taxScheme);

                                    item.addContent(classifiedTaxCategory);
                                }
                            }

                            if (!elemBg31.getBG0032ItemAttributes().isEmpty()) {
                                List<BG0032ItemAttributes> bg0032 = elemBg31.getBG0032ItemAttributes();
                                for (BG0032ItemAttributes elemBg32 : bg0032) {
                                    Element additionalItemProperty = new Element("AdditionalItemProperty");
                                    if (!elemBg32.getBT0160ItemAttributeName().isEmpty()) {
                                        BT0160ItemAttributeName bt0160 = elemBg32.getBT0160ItemAttributeName(0);
                                        Element name = new Element("Name");
                                        name.setText(bt0160.getValue());
                                        additionalItemProperty.addContent(name);
                                    }
                                    if (!elemBg32.getBT0161ItemAttributeValue().isEmpty()) {
                                        BT0161ItemAttributeValue bt0161 = elemBg32.getBT0161ItemAttributeValue(0);
                                        Element value = new Element("Value");
                                        value.setText(bt0161.getValue());
                                        additionalItemProperty.addContent(value);
                                    }
                                    item.addContent(additionalItemProperty);
                                }
                            }

                            invoiceLine.addContent(item);
                        }
                    }

                    if (!elemBg25.getBG0029PriceDetails().isEmpty()) {
                        List<BG0029PriceDetails> bg0029 = elemBg25.getBG0029PriceDetails();
                        for (BG0029PriceDetails elemBg29 : bg0029) {
                            Element price = new Element("Price");
                            if (!elemBg29.getBT0146ItemNetPrice().isEmpty()) {
                                BT0146ItemNetPrice bt0146 = elemBg29.getBT0146ItemNetPrice(0);
                                Element priceAmount = new Element("PriceAmount");
                                final BigDecimal value = bt0146.getValue();
                                priceAmount.setText(value.setScale(2, RoundingMode.HALF_UP).toString());
                                if (currencyCode != null) {
                                    priceAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
                                }
                                price.addContent(priceAmount);
                            }
                            if (!elemBg29.getBT0149ItemPriceBaseQuantity().isEmpty()) {
                                BT0149ItemPriceBaseQuantity bt0149 = elemBg29.getBT0149ItemPriceBaseQuantity(0);
                                Element baseQuantity = new Element("BaseQuantity");
                                final BigDecimal value = bt0149.getValue();
                                baseQuantity.setText(value.setScale(2, RoundingMode.HALF_UP).toString());

                                if (!elemBg29.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                                    BT0150ItemPriceBaseQuantityUnitOfMeasureCode bt0150 = elemBg29.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0);
                                    UnitOfMeasureCodes unitOfMeasureCodes = bt0150.getValue();
                                    Attribute unitCode = new Attribute("unitCode", unitOfMeasureCodes.getCommonCode());
                                    baseQuantity.setAttribute(unitCode);
                                }
                                price.addContent(baseQuantity);

                                if (!elemBg29.getBT0147ItemPriceDiscount().isEmpty() || !elemBg29.getBT0148ItemGrossPrice().isEmpty()) {

                                    Element allowanceCharge = new Element("AllowanceCharge");

                                    Element chargeIndicator = new Element("ChargeIndicator");
                                    chargeIndicator.setText("false");
                                    allowanceCharge.addContent(chargeIndicator);

                                    if (!elemBg29.getBT0147ItemPriceDiscount().isEmpty()) {
                                        Element amount = new Element("Amount");
                                        amount.setText(elemBg29.getBT0147ItemPriceDiscount(0).getValue().toString());
                                        amount.setAttribute("currencyID", currencyCode.getCode());
                                        allowanceCharge.addContent(amount);
                                    }

                                    if (!elemBg29.getBT0148ItemGrossPrice().isEmpty()) {
                                        Element baseAmount = new Element("BaseAmount");
                                        baseAmount.setText(elemBg29.getBT0148ItemGrossPrice(0).getValue().toString());
                                        baseAmount.setAttribute("currencyID", currencyCode.getCode());
                                        allowanceCharge.addContent(baseAmount);
                                    }

                                    price.addContent(allowanceCharge);
                                }
                            }
                            invoiceLine.addContent(price);
                        }
                    }

                    root.addContent(invoiceLine);
                }
            }

        }
    }

    private void setDateChild(List<IConversionIssue> errors, ErrorCode.Location callingLocation, Element parent, LocalDate date, String tagName) {
        try {
            if (date != null) {
                Element startDateElm = new Element(tagName);
                startDateElm.setText(dateConverter.convert(date));
                parent.addContent(startDateElm);
            }
        } catch (IllegalArgumentException | ConversionFailedException e) {
            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder()
                    .message("Invalid date format '" + date.toString() + "'")
                    .location(callingLocation)
                    .action(ErrorCode.Action.HARDCODED_MAP)
                    .error(ErrorCode.Error.ILLEGAL_VALUE)
                    .build());
            errors.add(ConversionIssue.newError(ere));
        }
    }
}
