package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InvoiceLineConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(InvoiceLineConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        DoubleToStringConverter dblStrConverter = new DoubleToStringConverter("#.00");

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
                        if (bt0126 != null) {
                            Element id = new Element("ID");
                            id.addContent(bt0126.getValue());
                            invoiceLine.addContent(id);
                        }
                    }
                    if (!elemBg25.getBT0129InvoicedQuantity().isEmpty()) {
                        BT0129InvoicedQuantity bt0129 = elemBg25.getBT0129InvoicedQuantity(0);
                        if (bt0129 != null) {
                            Element invoicedQuantity = new Element("InvoicedQuantity");
                            invoicedQuantity.addContent(dblStrConverter.convert(bt0129.getValue()));

                            if (!elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty()) {
                                BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = elemBg25.getBT0130InvoicedQuantityUnitOfMeasureCode(0);
                                if (bt0130 != null) {
                                    UnitOfMeasureCodes unitOfMeasureCodes = bt0130.getValue();
                                    Attribute unitCode = new Attribute("unitCode", unitOfMeasureCodes.name());
                                    invoicedQuantity.setAttribute(unitCode);
                                }
                            }
                            invoiceLine.addContent(invoicedQuantity);
                        }
                    }

                    if (!elemBg25.getBT0131InvoiceLineNetAmount().isEmpty()) {
                        BT0131InvoiceLineNetAmount bt0131 = elemBg25.getBT0131InvoiceLineNetAmount(0);
                        if (bt0131 != null) {
                            Element lineExtensionAmount = new Element("LineExtensionAmount");
                            lineExtensionAmount.addContent(dblStrConverter.convert(bt0131.getValue()));
                            if (currencyCode != null) {
                                lineExtensionAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
                            }
                            invoiceLine.addContent(lineExtensionAmount);
                        }
                    }

                    if (!elemBg25.getBG0026InvoiceLinePeriod().isEmpty()) {
                        List<BG0026InvoiceLinePeriod> bg0026 = elemBg25.getBG0026InvoiceLinePeriod();
                        for (BG0026InvoiceLinePeriod elemBg26 : bg0026) {
                            Element invoicePeriod = new Element("InvoicePeriod");
                            invoiceLine.addContent(invoicePeriod);
                        }
                    }

                    if (!elemBg25.getBG0031ItemInformation().isEmpty()) {
                        List<BG0031ItemInformation> bg0031 = elemBg25.getBG0031ItemInformation();
                        for (BG0031ItemInformation elemBg31 : bg0031) {
                            Element item = new Element("Item");
                            if (!elemBg31.getBT0153ItemName().isEmpty()) {
                                BT0153ItemName bt0153 = elemBg31.getBT0153ItemName(0);
                                if (bt0153 != null) {
                                    Element name = new Element("Name");
                                    name.addContent(bt0153.getValue());
                                    item.addContent(name);
                                }
                            }
                            if (!elemBg25.getBG0030LineVatInformation().isEmpty()) {
                                List<BG0030LineVatInformation> bg0030 = elemBg25.getBG0030LineVatInformation();
                                for (BG0030LineVatInformation elemBg30 : bg0030) {
                                    Element classifiedTaxCategory = new Element("ClassifiedTaxCategory");
                                    if (!elemBg30.getBT0151InvoicedItemVatCategoryCode().isEmpty()) {
                                        BT0151InvoicedItemVatCategoryCode bt0151 = elemBg30.getBT0151InvoicedItemVatCategoryCode(0);
                                        if (bt0151 != null) {
                                            Element id = new Element("ID");
                                            Untdid5305DutyTaxFeeCategories dutyTaxFeeCategories = bt0151.getValue();
                                            id.addContent(dutyTaxFeeCategories.name());
                                            classifiedTaxCategory.addContent(id);
                                        }
                                    }
                                    if (!elemBg30.getBT0152InvoicedItemVatRate().isEmpty()) {
                                        BT0152InvoicedItemVatRate bt0152 = elemBg30.getBT0152InvoicedItemVatRate(0);
                                        if (bt0152 != null) {
                                            Element percent = new Element("Percent");
                                            percent.addContent(dblStrConverter.convert(bt0152.getValue()));
                                            classifiedTaxCategory.addContent(percent);
                                        }
                                    }
                                    item.addContent(classifiedTaxCategory);
                                }
                            }

                            if (!elemBg31.getBG0032ItemAttributes().isEmpty()) {
                                List<BG0032ItemAttributes> bg0032 = elemBg31.getBG0032ItemAttributes();
                                for (BG0032ItemAttributes elemBg32 : bg0032) {
                                    Element additionalItemProperty = new Element("AdditionalItemProperty");
                                    if (!elemBg32.getBT0160ItemAttributeName().isEmpty()) {
                                        BT0160ItemAttributeName bt0160 = elemBg32.getBT0160ItemAttributeName(0);
                                        if (bt0160 != null) {
                                            Element name = new Element("Name");
                                            name.addContent(bt0160.getValue());
                                            additionalItemProperty.addContent(name);
                                        }
                                    }
                                    if (!elemBg32.getBT0161ItemAttributeValue().isEmpty()) {
                                        BT0161ItemAttributeValue bt0161 = elemBg32.getBT0161ItemAttributeValue(0);
                                        if (bt0161 != null) {
                                            Element value = new Element("Value");
                                            value.addContent(bt0161.getValue());
                                            additionalItemProperty.addContent(value);
                                        }
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
                                if (bt0146 != null) {
                                    Element priceAmount = new Element("PriceAmount");
                                    priceAmount.addContent(dblStrConverter.convert(bt0146.getValue()));
                                    if (currencyCode != null) {
                                        priceAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
                                    }
                                    price.addContent(priceAmount);
                                }
                            }
                            if (!elemBg29.getBT0149ItemPriceBaseQuantity().isEmpty()) {
                                BT0149ItemPriceBaseQuantity bt0149 = elemBg29.getBT0149ItemPriceBaseQuantity(0);
                                if (bt0149 != null) {
                                    Element baseQuantity = new Element("BaseQuantity");
                                    baseQuantity.addContent(dblStrConverter.convert(bt0149.getValue()));

                                    if (!elemBg29.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty()) {
                                        BT0150ItemPriceBaseQuantityUnitOfMeasureCode bt0150 = elemBg29.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0);
                                        if (bt0150 != null) {
                                            UnitOfMeasureCodes unitOfMeasureCodes = bt0150.getValue();
                                            Attribute unitCode = new Attribute("unitCode", unitOfMeasureCodes.name());
                                            baseQuantity.setAttribute(unitCode);
                                        }
                                    }
                                    price.addContent(baseQuantity);
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
}