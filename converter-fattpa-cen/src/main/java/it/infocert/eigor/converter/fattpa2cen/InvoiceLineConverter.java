package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.StringToDoubleConverter;
import it.infocert.eigor.api.conversion.StringToUnitOfMeasureConverter;
import it.infocert.eigor.api.conversion.UnitOfMeasureCodesToStringConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
public class InvoiceLineConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        StringToDoubleConverter strDblConverter = new StringToDoubleConverter();
        ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter taxFeeCategoriesConverter = new ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter();
        StringToUnitOfMeasureConverter strToUnitOfMeasure = new StringToUnitOfMeasureConverter();

        BG0025InvoiceLine bg0025 = null;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiBeniServizi = fatturaElettronicaBody.getChild("DatiBeniServizi");
            if (datiBeniServizi != null) {
                List<Element> dettagliLinee = datiBeniServizi.getChildren();
                for (Element dettaglioLinee : dettagliLinee) {
                    if (dettaglioLinee.getName().equals("DettaglioLinee")) {
                        bg0025 = new BG0025InvoiceLine();
                        Element numeroLinea = dettaglioLinee.getChild("NumeroLinea");
                        if (numeroLinea != null) {
                            BT0126InvoiceLineIdentifier invoiceLineIdentifier = new BT0126InvoiceLineIdentifier(numeroLinea.getText());
                            bg0025.getBT0126InvoiceLineIdentifier().add(invoiceLineIdentifier);
                        }
                        Element quantita = dettaglioLinee.getChild("Quantita");
                        if (quantita != null) {
                            try {
                                BT0129InvoicedQuantity invoicedQuantity = new BT0129InvoicedQuantity(strDblConverter.convert(quantita.getText()));
                                bg0025.getBT0129InvoicedQuantity().add(invoicedQuantity);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        Element unitaMisura = dettaglioLinee.getChild("UnitaMisura");
                        UnitOfMeasureCodes unitCode = null;
                        if (unitaMisura != null) {
                            try {
                                unitCode = strToUnitOfMeasure.convert(unitaMisura.getText());
                                BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = new BT0130InvoicedQuantityUnitOfMeasureCode(unitCode);
                                bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(bt0130);
                            } catch (NullPointerException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("UnitOfMeasureCodes not found").action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        Element prezzoTotale = dettaglioLinee.getChild("PrezzoTotale");
                        if (prezzoTotale != null) {
                            try {
                                BT0131InvoiceLineNetAmount invoiceLineNetAmount = new BT0131InvoiceLineNetAmount(strDblConverter.convert(prezzoTotale.getText()));
                                bg0025.getBT0131InvoiceLineNetAmount().add(invoiceLineNetAmount);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }

                        BG0029PriceDetails bg0029 = new BG0029PriceDetails();
                        Element prezzoUnitario = dettaglioLinee.getChild("PrezzoUnitario");
                        if (prezzoUnitario != null) {
                            BT0146ItemNetPrice itemNetPrice = new BT0146ItemNetPrice(strDblConverter.convert(prezzoUnitario.getText()));
                            bg0029.getBT0146ItemNetPrice().add(itemNetPrice);
                        }
                        Element unitaMisuraDett = dettaglioLinee.getChild("UnitaMisura");
                        if (unitaMisuraDett != null) {
                            try {
                                String commonQuantity = unitaMisuraDett.getValue().split(" ")[0];
                                BT0149ItemPriceBaseQuantity itemPriceBaseQuantity = new BT0149ItemPriceBaseQuantity(strDblConverter.convert(commonQuantity));
                                bg0029.getBT0149ItemPriceBaseQuantity().add(itemPriceBaseQuantity);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        if (unitCode != null) {
                            try {
                                BT0150ItemPriceBaseQuantityUnitOfMeasureCode itemPriceBaseQuantityUnitOfMeasureCode = new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(unitCode);
                                bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(itemPriceBaseQuantityUnitOfMeasureCode);
                            } catch (NullPointerException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        bg0025.getBG0029PriceDetails().add(bg0029);

                        BG0030LineVatInformation bg0030 = new BG0030LineVatInformation();
                        Element natura = dettaglioLinee.getChild("Natura");
                        if (natura != null) {
                            try {
                                BT0151InvoicedItemVatCategoryCode invoicedItemVatCategoryCode = new BT0151InvoicedItemVatCategoryCode(taxFeeCategoriesConverter.convert(natura.getText()));
                                bg0030.getBT0151InvoicedItemVatCategoryCode().add(invoicedItemVatCategoryCode);
                            } catch (NullPointerException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        Element aliquotaIVA = dettaglioLinee.getChild("AliquotaIVA");
                        if (aliquotaIVA != null) {
                            try {
                                BT0152InvoicedItemVatRate invoicedItemVatRate = new BT0152InvoicedItemVatRate(strDblConverter.convert(aliquotaIVA.getText()));
                                bg0030.getBT0152InvoicedItemVatRate().add(invoicedItemVatRate);
                            } catch (NumberFormatException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("InvoiceLineConverter").build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                        bg0025.getBG0030LineVatInformation().add(bg0030);

                        BG0031ItemInformation bg0031 = new BG0031ItemInformation();
                        Element descrizione = dettaglioLinee.getChild("Descrizione");
                        if (descrizione != null) {
                            BT0153ItemName itemName = new BT0153ItemName(descrizione.getText());
                            bg0031.getBT0153ItemName().add(itemName);
                        }

                        BG0032ItemAttributes bg0032 = null;
                        List<Element> altriDatiGestionaliList = dettaglioLinee.getChildren();
                        for (Element altriDatiGestionali : altriDatiGestionaliList) {
                            if (altriDatiGestionali.getName().equals("AltriDatiGestionali")) {
                                bg0032 = new BG0032ItemAttributes();
                                Element tipoDato = altriDatiGestionali.getChild("TipoDato");
                                if (tipoDato != null) {
                                    BT0160ItemAttributeName itemAttributeName = new BT0160ItemAttributeName(tipoDato.getText());
                                    bg0032.getBT0160ItemAttributeName().add(itemAttributeName);
                                }
                                Element riferimentoTesto = altriDatiGestionali.getChild("RiferimentoTesto");
                                if (riferimentoTesto != null) {
                                    BT0161ItemAttributeValue itemAttributeValue = new BT0161ItemAttributeValue(riferimentoTesto.getText());
                                    bg0032.getBT0161ItemAttributeValue().add(itemAttributeValue);
                                }
                                bg0031.getBG0032ItemAttributes().add(bg0032);
                            }
                        }

                        bg0025.getBG0031ItemInformation().add(bg0031);

                        invoice.getBG0025InvoiceLine().add(bg0025);
                    }
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0025(document, cenInvoice, errors);
    }
}
