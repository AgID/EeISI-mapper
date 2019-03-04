package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToUnitOfMeasureConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
public class InvoiceLineConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        TypeConverter<String, Untdid5305DutyTaxFeeCategories> taxFeeCategoriesConverter = ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter.newConverter();
        TypeConverter<String, UnitOfMeasureCodes> strToUnitOfMeasure = StringToUnitOfMeasureConverter.newConverter();


        BG0025InvoiceLine bg0025;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiBeniServizi = fatturaElettronicaBody.getChild("DatiBeniServizi");
            if (datiBeniServizi != null) {
                List<Element> dettagliLinee = datiBeniServizi.getChildren();
                BigDecimal invoiceLineNetAmountTotal = new BigDecimal(0);
                for (Element dettaglioLinee : dettagliLinee) {
                    if (dettaglioLinee.getName().equals("DettaglioLinee")) {

                        BigDecimal prezzoTotaleValue = null;
                        {
                            Element prezzoTotaleElement = dettaglioLinee.getChild("PrezzoTotale");
                            if (prezzoTotaleElement != null) {
                                try {
                                    prezzoTotaleValue = new BigDecimal(prezzoTotaleElement.getText());
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                        }

                        Untdid5305DutyTaxFeeCategories naturaValue = null;
                        {
                            Element naturaElement = dettaglioLinee.getChild("Natura");
                            if (naturaElement != null) {
                                try {
                                    naturaValue = taxFeeCategoriesConverter.convert(naturaElement.getText());
                                } catch (NullPointerException | ConversionFailedException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            } else {
                                naturaValue = Untdid5305DutyTaxFeeCategories.S;
                            }
                        }

                        BigDecimal aliquotaIVAValue = null;
                        {
                            Element aliquotaIVAElement = dettaglioLinee.getChild("AliquotaIVA");
                            if (aliquotaIVAElement != null) {
                                try {
                                    aliquotaIVAValue = new BigDecimal(aliquotaIVAElement.getText());
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                        }

                        String descrizioneValue = null;
                        Element descrizioneElement = dettaglioLinee.getChild("Descrizione");
                        if (descrizioneElement != null) {
                            descrizioneValue = descrizioneElement.getValue();
                        }
                        Element tipoCessionePrestazione = dettaglioLinee.getChild("TipoCessionePrestazione");

                        BG0027InvoiceLineAllowances bg0027 = new BG0027InvoiceLineAllowances();
                        BG0031ItemInformation bg0031 = new BG0031ItemInformation();
                        BG0028InvoiceLineCharges bg0028 = new BG0028InvoiceLineCharges();

                        if (tipoCessionePrestazione != null && Arrays.asList("SC", "PR", "AB").contains(tipoCessionePrestazione.getText())) {
                            BT0139InvoiceLineAllowanceReason item = new BT0139InvoiceLineAllowanceReason(descrizioneValue);
                            bg0027.getBT0139InvoiceLineAllowanceReason().add(item);

                        } else if (tipoCessionePrestazione != null && Arrays.asList("AC").contains(tipoCessionePrestazione.getText())) {
                            BT0144InvoiceLineChargeReason item = new BT0144InvoiceLineChargeReason(descrizioneValue);
                            bg0028.getBT0144InvoiceLineChargeReason().add(item);

                        } else {
                            if (descrizioneValue != null) {
                                BT0153ItemName itemName = new BT0153ItemName(descrizioneValue);
                                bg0031.getBT0153ItemName().add(itemName);
                            }
                        }

                        if (tipoCessionePrestazione != null) {
                            if (prezzoTotaleValue != null && prezzoTotaleValue.signum() < 0 &&
                                    Arrays.asList("SC", "PR", "AB").contains(tipoCessionePrestazione.getText())) {

                                BG0020DocumentLevelAllowances bg0020 = new BG0020DocumentLevelAllowances();

                                BT0098DocumentLevelAllowanceReasonCode bt0098 = new BT0098DocumentLevelAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code95);
                                bg0020.getBT0098DocumentLevelAllowanceReasonCode().add(bt0098);

                                BT0092DocumentLevelAllowanceAmount bt0092 = new BT0092DocumentLevelAllowanceAmount(prezzoTotaleValue.negate());
                                bg0020.getBT0092DocumentLevelAllowanceAmount().add(bt0092);

                                if (naturaValue != null) {
                                    BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = new BT0095DocumentLevelAllowanceVatCategoryCode(naturaValue);
                                    bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().add(bt0095);
                                }

                                if (aliquotaIVAValue != null) {
                                    BT0096DocumentLevelAllowanceVatRate bt0096 = new BT0096DocumentLevelAllowanceVatRate(aliquotaIVAValue);
                                    bg0020.getBT0096DocumentLevelAllowanceVatRate().add(bt0096);
                                }

                                if (descrizioneValue != null) {
                                    BT0097DocumentLevelAllowanceReason bt0097 = new BT0097DocumentLevelAllowanceReason(descrizioneValue);
                                    bg0020.getBT0097DocumentLevelAllowanceReason().add(bt0097);
                                }

                                invoice.getBG0020DocumentLevelAllowances().add(bg0020);
                            } else if (prezzoTotaleValue != null && prezzoTotaleValue.signum() > 0 &&
                                    Arrays.asList("SC", "PR", "AB", "AC").contains(tipoCessionePrestazione.getText())) {

                                BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();

                                BT0105DocumentLevelChargeReasonCode bt0105 = new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.ABK);
                                bg0021.getBT0105DocumentLevelChargeReasonCode().add(bt0105);

                                BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(prezzoTotaleValue);
                                bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);

                                if (naturaValue != null) {
                                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(naturaValue);
                                    bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);
                                }

                                if (aliquotaIVAValue != null) {
                                    BT0103DocumentLevelChargeVatRate bt0103 = new BT0103DocumentLevelChargeVatRate(aliquotaIVAValue);
                                    bg0021.getBT0103DocumentLevelChargeVatRate().add(bt0103);
                                }

                                if (descrizioneValue != null) {
                                    BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason(descrizioneValue);
                                    bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);
                                }

                                invoice.getBG0021DocumentLevelCharges().add(bg0021);
                            }
                        } else {
                            bg0025 = new BG0025InvoiceLine();
                            Element numeroLinea = dettaglioLinee.getChild("NumeroLinea");
                            if (numeroLinea != null) {
                                BT0126InvoiceLineIdentifier invoiceLineIdentifier = new BT0126InvoiceLineIdentifier(numeroLinea.getText());
                                bg0025.getBT0126InvoiceLineIdentifier().add(invoiceLineIdentifier);
                            }

                            Element codiceArticolo = dettaglioLinee.getChild("CodiceArticolo");
                            if(codiceArticolo != null) {
                                Element codiceValore = codiceArticolo.getChild("CodiceValore");
                                String codiceTipo = codiceArticolo.getChild("CodiceTipo").getText();
                                try {
                                    // TODO add Untdid7143 and ISO6523 code lists
                                    Untdid1153ReferenceQualifierCode.valueOf(codiceTipo);
                                } catch (IllegalArgumentException e) {
                                    codiceTipo = "ZZZ";
                                }
                                BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128InvoiceLineObjectIdentifierAndSchemeIdentifier =
                                        new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier(codiceTipo,codiceValore.getText()));
                                bg0025.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128InvoiceLineObjectIdentifierAndSchemeIdentifier);
                            }

                            Element quantita = dettaglioLinee.getChild("Quantita");
                            if (quantita != null) {
                                try {
                                    BT0129InvoicedQuantity invoicedQuantity = new BT0129InvoicedQuantity(new BigDecimal(quantita.getText()));
                                    bg0025.getBT0129InvoicedQuantity().add(invoicedQuantity);
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            } else {
                                BT0129InvoicedQuantity invoicedQuantity = new BT0129InvoicedQuantity(BigDecimal.ONE);
                                bg0025.getBT0129InvoicedQuantity().add(invoicedQuantity);
                            }
                            Element unitaMisura = dettaglioLinee.getChild("UnitaMisura");
                            UnitOfMeasureCodes unitCode = null;
                            if (unitaMisura != null) {
                                final String text = unitaMisura.getText();
                                try {
                                    unitCode = strToUnitOfMeasure.convert(text);
                                    BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = new BT0130InvoicedQuantityUnitOfMeasureCode(unitCode);
                                    bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(bt0130);
                                } catch (ConversionFailedException e) {
                                    bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.C62_ONE));
                                }
                            } else {
                                bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.C62_ONE));
                            }

                            if (prezzoTotaleValue != null) {
                                BT0131InvoiceLineNetAmount invoiceLineNetAmount = new BT0131InvoiceLineNetAmount(prezzoTotaleValue);
                                bg0025.getBT0131InvoiceLineNetAmount().add(invoiceLineNetAmount);
                                invoiceLineNetAmountTotal = invoiceLineNetAmountTotal.add(prezzoTotaleValue);
                            }

                            BG0026InvoiceLinePeriod bg0026 = new BG0026InvoiceLinePeriod();
                            Element dataInizioPeriodo = dettaglioLinee.getChild("DataInizioPeriodo");
                            if (dataInizioPeriodo != null) {
                                BT0134InvoiceLinePeriodStartDate bt0134InvoiceLinePeriodStartDate = new BT0134InvoiceLinePeriodStartDate(new org.joda.time.LocalDate(dataInizioPeriodo.getText()));
                                bg0026.getBT0134InvoiceLinePeriodStartDate().add(bt0134InvoiceLinePeriodStartDate);
                            }

                            Element riferimentoAmministrazione = dettaglioLinee.getChild("RiferimentoAmministrazione");
                            if(riferimentoAmministrazione != null){
                                BT0133InvoiceLineBuyerAccountingReference bt0133InvoiceLineBuyerAccountingReference = new BT0133InvoiceLineBuyerAccountingReference(riferimentoAmministrazione.getText());
                                bg0025.getBT0133InvoiceLineBuyerAccountingReference().add(bt0133InvoiceLineBuyerAccountingReference);
                            }

                            Element dataFinePeriodo = dettaglioLinee.getChild("DataFinePeriodo");
                            if (dataFinePeriodo != null) {
                                try {
                                    BT0135InvoiceLinePeriodEndDate bt0135InvoiceLinePeriodEndDate = new BT0135InvoiceLinePeriodEndDate(new org.joda.time.LocalDate(dataFinePeriodo.getText()));
                                    bg0026.getBT0135InvoiceLinePeriodEndDate().add(bt0135InvoiceLinePeriodEndDate);
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                            if (dataInizioPeriodo != null || dataFinePeriodo != null) {
                                bg0025.getBG0026InvoiceLinePeriod().add(bg0026);
                            }

                            Element scontoMaggiorazione = dettaglioLinee.getChild("ScontoMaggiorazione");
                            if (scontoMaggiorazione != null) {
                                Element tipo = scontoMaggiorazione.getChild("Tipo");
                                if ("SC".equals(tipo.getText())) {
                                    Element percentuale = scontoMaggiorazione.getChild("Percentuale");
                                    Element importo = scontoMaggiorazione.getChild("Importo");
                                    if (importo != null) {
                                        BT0136InvoiceLineAllowanceAmount bt0136InvoiceLineAllowanceAmount = new BT0136InvoiceLineAllowanceAmount(new BigDecimal(importo.getText()));
                                        bg0027.getBT0136InvoiceLineAllowanceAmount().add(bt0136InvoiceLineAllowanceAmount);
                                    }
                                    if (percentuale != null) {
                                        BT0138InvoiceLineAllowancePercentage bt0138InvoiceLineAllowancePercentage = new BT0138InvoiceLineAllowancePercentage(new Identifier(percentuale.getText()));
                                        bg0027.getBT0138InvoiceLineAllowancePercentage().add(bt0138InvoiceLineAllowancePercentage);
                                    }
                                    bg0027.getBT0140InvoiceLineAllowanceReasonCode().add(new BT0140InvoiceLineAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code95));
                                    bg0025.getBG0027InvoiceLineAllowances().add(bg0027);
                                }

                                if ("MG".equals(tipo.getText())) {
                                    Element percentuale = scontoMaggiorazione.getChild("Percentuale");
                                    Element importo = scontoMaggiorazione.getChild("Importo");
                                    if (importo != null) {
                                        BT0141InvoiceLineChargeAmount bt0141InvoiceLineChargeAmount = new BT0141InvoiceLineChargeAmount(new BigDecimal(importo.getText()));
                                        bg0028.getBT0141InvoiceLineChargeAmount().add(bt0141InvoiceLineChargeAmount);
                                    }
                                    if (percentuale != null) {
                                        BT0143InvoiceLineChargePercentage bt0143InvoiceLineChargePercentage = new BT0143InvoiceLineChargePercentage(new BigDecimal(percentuale.getText()));
                                        bg0028.getBT0143InvoiceLineChargePercentage().add(bt0143InvoiceLineChargePercentage);
                                    }
                                    bg0028.getBT0145InvoiceLineChargeReasonCode().add(new BT0145InvoiceLineChargeReasonCode(Untdid7161SpecialServicesCodes.ABK));
                                    bg0025.getBG0028InvoiceLineCharges().add(bg0028);
                                }
                            }

                            BG0029PriceDetails bg0029 = new BG0029PriceDetails();
                            Element prezzoUnitario = dettaglioLinee.getChild("PrezzoUnitario");
                            if (prezzoUnitario != null) {
                                try {
                                    BT0146ItemNetPrice itemNetPrice = new BT0146ItemNetPrice(new BigDecimal(prezzoUnitario.getText()));
                                    bg0029.getBT0146ItemNetPrice().add(itemNetPrice);
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                            Element unitaMisuraDett = dettaglioLinee.getChild("UnitaMisura");
                            if (unitaMisuraDett != null) {
                                BT0149ItemPriceBaseQuantity itemPriceBaseQuantity = new BT0149ItemPriceBaseQuantity(BigDecimal.ONE);
                                bg0029.getBT0149ItemPriceBaseQuantity().add(itemPriceBaseQuantity);
                            }
                            if (unitCode != null) {
                                try {
                                    BT0150ItemPriceBaseQuantityUnitOfMeasureCode itemPriceBaseQuantityUnitOfMeasureCode = new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(unitCode);
                                    bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(itemPriceBaseQuantityUnitOfMeasureCode);
                                } catch (NullPointerException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.MISSING_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                            bg0025.getBG0029PriceDetails().add(bg0029);

                            BG0030LineVatInformation bg0030 = new BG0030LineVatInformation();
                            Untdid5305DutyTaxFeeCategories code = naturaValue != null ? naturaValue : Untdid5305DutyTaxFeeCategories.S;
                            BT0151InvoicedItemVatCategoryCode invoicedItemVatCategoryCode = new BT0151InvoicedItemVatCategoryCode(code);
                            bg0030.getBT0151InvoicedItemVatCategoryCode().add(invoicedItemVatCategoryCode);

                            if (aliquotaIVAValue != null) {
                                BT0152InvoicedItemVatRate invoicedItemVatRate = new BT0152InvoicedItemVatRate(aliquotaIVAValue);
                                bg0030.getBT0152InvoicedItemVatRate().add(invoicedItemVatRate);
                            }
                            bg0025.getBG0030LineVatInformation().add(bg0030);

                            BG0032ItemAttributes bg0032;
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
                            Element datiGenerali = fatturaElettronicaBody.getChild("DatiGenerali");
                            if(datiGenerali != null){
                                Element datiTrasporto = datiGenerali.getChild("DatiTrasporto");
                                if(datiTrasporto != null){
                                    Element causaleTrasporto = datiTrasporto.getChild("CausaleTrasporto");
                                    if (causaleTrasporto != null) {
                                        bg0032 = new BG0032ItemAttributes();
                                        BT0160ItemAttributeName itemAttributeName = new BT0160ItemAttributeName(causaleTrasporto.getText());
                                        bg0032.getBT0160ItemAttributeName().add(itemAttributeName);
                                        BT0161ItemAttributeValue itemAttributeValue = new BT0161ItemAttributeValue(causaleTrasporto.getText());
                                        bg0032.getBT0161ItemAttributeValue().add(itemAttributeValue);
                                        bg0031.getBG0032ItemAttributes().add(bg0032);
                                    }
                                }
                            }

                            bg0025.getBG0031ItemInformation().add(bg0031);

                            invoice.getBG0025InvoiceLine().add(bg0025);
                        }
                    }
                }
                if (invoice.getBG0022DocumentTotals() == null || invoice.getBG0022DocumentTotals().size() == 0) {
                    invoice.getBG0022DocumentTotals().add(new BG0022DocumentTotals());
                }
                invoice.getBG0022DocumentTotals(0).getBT0106SumOfInvoiceLineNetAmount().add(new BT0106SumOfInvoiceLineNetAmount(invoiceLineNetAmountTotal));

                BigDecimal sumOfBT0021 = new BigDecimal(0);
                for(int i=0; i<invoice.getBG0021DocumentLevelCharges().size(); i++){
                    sumOfBT0021 = sumOfBT0021.add(invoice.getBG0021DocumentLevelCharges().get(i).getBT0099DocumentLevelChargeAmount().get(0).getValue());
                }
                List<BG0020DocumentLevelAllowances> bg0020DocumentLevelAllowances = invoice.getBG0020DocumentLevelAllowances();
                BigDecimal sumOfBT0020 = new BigDecimal(0);
                for(int i=0; i<bg0020DocumentLevelAllowances.size(); i++){
                    sumOfBT0020 = sumOfBT0020.add(bg0020DocumentLevelAllowances.get(i).getBT0092DocumentLevelAllowanceAmount().get(0).getValue());
                }
                invoice.getBG0022DocumentTotals(0).getBT0109InvoiceTotalAmountWithoutVat().add(new BT0109InvoiceTotalAmountWithoutVat((invoiceLineNetAmountTotal.subtract(sumOfBT0020)).add(sumOfBT0021)));
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0025(document, cenInvoice, errors, callingLocation);
    }
}
