package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.conversion.DoubleToStringConverter;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VATBreakdownConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(VATBreakdownConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        DoubleToStringConverter dblStrConverter = new DoubleToStringConverter("#.00");

        Element root = document.getRootElement();
        if (root != null) {
            List<BG0023VatBreakdown> bg0023 = cenInvoice.getBG0023VatBreakdown();
            for (BG0023VatBreakdown elemBg23 : bg0023) {
                BT0116VatCategoryTaxableAmount bt0116 = null;
                if (!elemBg23.getBT0116VatCategoryTaxableAmount().isEmpty()) {
                    bt0116 = elemBg23.getBT0116VatCategoryTaxableAmount(0);
                }
                BT0117VatCategoryTaxAmount bt0117 = null;
                if (!elemBg23.getBT0117VatCategoryTaxAmount().isEmpty()) {
                    bt0117 = elemBg23.getBT0117VatCategoryTaxAmount(0);
                }
                BT0118VatCategoryCode bt0118 = null;
                if (!elemBg23.getBT0118VatCategoryCode().isEmpty()) {
                    bt0118 = elemBg23.getBT0118VatCategoryCode(0);
                }
                BT0119VatCategoryRate bt0119 = null;
                if (!elemBg23.getBT0119VatCategoryRate().isEmpty()) {
                    bt0119 = elemBg23.getBT0119VatCategoryRate(0);
                }

                if (bt0116 != null || bt0117 != null || bt0118 != null || bt0119 != null) {

                    Element taxTotal = root.getChild("TaxTotal");
                    if (taxTotal == null) {
                        taxTotal = new Element("TaxTotal");
                        root.addContent(taxTotal);
                    }
                    Element taxSubtotal = new Element("TaxSubtotal");
                    taxTotal.addContent(taxSubtotal);

                    if (bt0116 != null) {
                        Element taxableAmount = new Element("TaxableAmount");
                        taxableAmount.addContent(dblStrConverter.convert(bt0116.getValue()));
                        taxSubtotal.addContent(taxableAmount);
                    }
                    if (bt0117 != null) {
                        Element taxAmount = new Element("TaxAmount");
                        taxAmount.addContent(dblStrConverter.convert(bt0117.getValue()));
                        taxTotal.addContent(taxAmount);
                    }

                    Element taxCategory = new Element("TaxCategory");
                    taxSubtotal.addContent(taxCategory);

                    if (bt0118 != null) {
                        Element id = new Element("ID");
                        Untdid5305DutyTaxFeeCategories dutyTaxFeeCategories = bt0118.getValue();
                        id.addContent(dutyTaxFeeCategories.name());
                        taxCategory.addContent(id);
                    }

                    if (bt0119 != null) {
                        Element percent = new Element("Percent");
                        percent.addContent(dblStrConverter.convert(bt0119.getValue()));
                        taxCategory.addContent(percent);
                    }

                    if (!cenInvoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
                        BT0005InvoiceCurrencyCode bt0005 = cenInvoice.getBT0005InvoiceCurrencyCode(0);
                        Iso4217CurrenciesFundsCodes currencyCode = bt0005.getValue();

                        for (Element element : taxSubtotal.getChildren()) {
                            if (element.getName().equals("TaxableAmount")) {
                                element.setAttribute(new Attribute("currencyID", currencyCode.name()));
                            }
                        }
                        Element taxAmount = taxTotal.getChild("TaxAmount");
                        if (taxAmount != null) {
                            taxAmount.setAttribute(new Attribute("currencyID", currencyCode.name()));
                        }
                    }
                }
            }
        }
    }
}