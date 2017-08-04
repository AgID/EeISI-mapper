package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.converter.cen2fattpa.converters.*;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LineConverter implements CustomMapping<FatturaElettronicaType> {
    private final static Logger log = LoggerFactory.getLogger(LineConverter.class);
    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            new CountryNameToIso31661CountryCodeConverter(),
            new LookUpEnumConversion(Iso31661CountryCodes.class),
            new StringToJavaLocalDateConverter("yyyy-MM-dd"),
            new StringToUntdid1001InvoiceTypeCodeConverter(),
            new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),
            new StringToIso4217CurrenciesFundsCodesConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
            new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
            new LookUpEnumConversion(Untdid5305DutyTaxFeeCategories.class),
            new StringToUnitOfMeasureConverter(),
            new LookUpEnumConversion(UnitOfMeasureCodes.class),
            new StringToDoubleConverter(),
            new StringToStringConverter(),
            new JavaLocalDateToStringConverter(),
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new Iso31661CountryCodesToStringConverter(),
            new DoubleToStringConverter("#.00"),
            new UnitOfMeasureCodesToStringConverter(),
            new Untdid1001InvoiceTypeCodeToItalianCodeStringConverter(),
            new Untdid4461PaymentMeansCodeToItalianCodeString(),
            new Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter(),
            new Untdid7161SpecialServicesCodesToItalianCodeStringConverter(),
            new Untdid2005DateTimePeriodQualifiersToItalianCodeConverter(),
            new Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter()
    );


    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("Too many FatturaElettronicaBody found in current FatturaElettronica")));
        } else if (size < 1){
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No FatturaElettronicaBody found in current FatturaElettronica")));
        } else {
            FatturaElettronicaBodyType fatturaElettronicaBody = bodies.get(0);
            if (fatturaElettronicaBody.getDatiBeniServizi() == null) {
                fatturaElettronicaBody.setDatiBeniServizi(new DatiBeniServiziType());
            }
            mapBG20(invoice, fatturaElettronicaBody, errors);
            mapBG21(invoice, fatturaElettronicaBody, errors);
            mapBG25(invoice, fatturaElettronicaBody, errors);
        }
    }


    private void mapBG20(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi != null) {
                List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
                for (BG0020DocumentLevelAllowances allowances : invoice.getBG0020DocumentLevelAllowances()) {
                    DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                    int number = datiBeniServizi.getDettaglioLinee().size() + 1;
                    log.info("Mapping BG20 to FattPA line number {}", number);
                    dettaglioLinee.setNumeroLinea(number);
                    log.trace("Set NumeroLinea with value {}", number);

                    if (!allowances.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowances.getBT0092DocumentLevelAllowanceAmount(0).getValue());
                        dettaglioLinee.setPrezzoUnitario(value);
                        dettaglioLinee.setPrezzoTotale(value);
                        log.trace("Set BT92 as PrezzoUnitario and PrezzoTotale with value {}", value);
                    }

                    if (!allowances.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                        AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                        String value = String.valueOf(allowances.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue());
                        dati.setRiferimentoTesto(value);
                        log.trace("Set BT93 as RiferimentoTesto with value {}", value);
                        dettaglioLinee.getAltriDatiGestionali().add(dati);
                    }

                    if (!allowances.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                        Untdid5305DutyTaxFeeCategories category = allowances.getBT0095DocumentLevelAllowanceVatCategoryCode(0).getValue();
                        switch (category) {
                            case Z:
                                dettaglioLinee.setNatura(NaturaType.N_3); //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
                                break;
                            case E:
                                dettaglioLinee.setNatura(NaturaType.N_4);
                                break;
                            case G:
                                dettaglioLinee.setNatura(NaturaType.N_2);
                                break;
                            case O:
                                dettaglioLinee.setNatura(NaturaType.N_2); //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
                                break;
                            default:
                                dettaglioLinee.setNatura(null);
                        }
                        log.trace("Set BT95 as Natura with value {}", dettaglioLinee.getNatura());
                    }

                    if (!allowances.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowances.getBT0096DocumentLevelAllowanceVatRate(0).getValue());
                        dettaglioLinee.setAliquotaIVA(value);
                        log.trace("Set BT96 as AliquotaIVA with value {}", value);
                    }

                    StringBuilder sb = new StringBuilder();
                    if (!allowances.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                        sb.append(allowances.getBT0097DocumentLevelAllowanceReason(0).getValue());
                        log.trace("Appended BT97 to Descrizione");
                    } else {
                        sb.append("Sconto Documento");
                        log.trace("Appended \"Sconto Documento\" to Descrizione");
                    }

                    sb.append(" ");

                    if (!allowances.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                        Untdid5189ChargeAllowanceDescriptionCodes code = allowances.getBT0098DocumentLevelAllowanceReasonCode(0).getValue();
                        Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter converter = new Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter();
                        String converted = converter.convert(code);
                        sb.append(converted);
                        log.trace("Appended BT98 to Descrizione");
                    }

                    String des = sb.toString();
                    dettaglioLinee.setDescrizione(des);
                    log.trace("Set \"{}\" as Descrizione", des);
                    ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                    scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
                    log.trace("Set ScontoMaggiorazione with type {}", TipoScontoMaggiorazioneType.SC);
                    dettaglioLineeList.add(dettaglioLinee);
                }
            }
        }
    }

    private void mapBG21(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0021DocumentLevelCharges().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi != null) {
                List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
                for (BG0021DocumentLevelCharges charges : invoice.getBG0021DocumentLevelCharges()) {
                    DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                    int number = datiBeniServizi.getDettaglioLinee().size() + 1;
                    log.info("Mapping BG21 to FattPA line number {}", number);
                    dettaglioLinee.setNumeroLinea(number);
                    dettaglioLinee.setDescrizione("Descrizione"); //FIXME HOW????
                    log.trace("Set NumeroLinea with value {}", number);

                    if (!charges.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0099DocumentLevelChargeAmount(0).getValue());
                        dettaglioLinee.setPrezzoUnitario(value);
                        dettaglioLinee.setPrezzoTotale(value);
                        log.trace("Set BT99 as PrezzoUnitario and PrezzoTotale with value {}", value);
                    }

                    if (!charges.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                        AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0100DocumentLevelChargeBaseAmount(0).getValue());
                        dati.setRiferimentoNumero(value);
                        dettaglioLinee.getAltriDatiGestionali().add(dati);
                        log.trace("Set BT100 as RiferimentoNumero with value {}", value);

                    }

                    if (!charges.getBT0102DocumentLevelChargeVatCategoryCode().isEmpty()) {
                        Untdid5305DutyTaxFeeCategories category = charges.getBT0102DocumentLevelChargeVatCategoryCode(0).getValue();
                        switch (category) {
                            case Z:
                                dettaglioLinee.setNatura(NaturaType.N_3); //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
                                break;
                            case E:
                                dettaglioLinee.setNatura(NaturaType.N_4);
                                break;
                            case G:
                                dettaglioLinee.setNatura(NaturaType.N_2);
                                break;
                            case O:
                                dettaglioLinee.setNatura(NaturaType.N_2); //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
                                break;
                            default:
                                dettaglioLinee.setNatura(null);
                        }
                        log.trace("Set BT102 as Natura with value {}", dettaglioLinee.getNatura());
                    }

                    if (!charges.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0103DocumentLevelChargeVatRate(0).getValue());
                        dettaglioLinee.setAliquotaIVA(value);
                        log.trace("Set BT103 as AliquotaIVA with value {}", value);
                    }

                    StringBuilder sb = new StringBuilder();
                    if (!charges.getBT0104DocumentLevelChargeReason().isEmpty()) {
                        sb.append(charges.getBT0104DocumentLevelChargeReason(0).getValue());
                        log.trace("Appended BT104 to Descrizione");
                    } else {
                        sb.append("Sconto Documento");
                        log.trace("Appended \"Sconto Documento\" to Descrizione");
                    }


                    if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                        Untdid7161SpecialServicesCodes code = charges.getBT0105DocumentLevelChargeReasonCode(0).getValue();

                        sb.append(" ").append("TC01"); //FIXME Placeholder, ask for better mapping!
                        log.trace("Appended BT105 to Descrizione");
                    }

                    dettaglioLinee.setDescrizione(sb.toString());
                    log.trace("Set {} as Descrizione", sb.toString());
                    ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                    scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
                    log.trace("Set ScontoMaggiorazione with type {}", TipoScontoMaggiorazioneType.SC);
                    dettaglioLineeList.add(dettaglioLinee);
                }
            }
        }
    }


    private void mapBG25(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0025InvoiceLine().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            for (BG0025InvoiceLine invoiceLine : invoice.getBG0025InvoiceLine()) {
                DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                dettaglioLinee.setDescrizione("Descrizione"); //FIXME Again, how?

                int number = datiBeniServizi.getDettaglioLinee().size() + 1;
                dettaglioLinee.setNumeroLinea(number);
                log.info("Mapping BG25 to FattPA line number {}", number);
                Double quantity = invoiceLine.getBT0129InvoicedQuantity().isEmpty() ? 0 : invoiceLine.getBT0129InvoicedQuantity(0).getValue();
                dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity));
                log.trace("Set BT129 as Quantita with value {}", quantity);

                if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
                    Double dValue = invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue();
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(dValue);
                    dettaglioLinee.setPrezzoTotale(value);
                    log.trace("Set BT131 as PrezzoTotale with value {}", value);
                    if (quantity != 0) {
                        dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(dValue / quantity));
                        //PrezzoUnitario is mandatory in FatturaPA
                    }
                }

                if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                    BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                    if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                        dettaglioLinee.setAliquotaIVA(value); //Even if BG25 doesn't have it, FatturaPA wants it
                    }
                }

                datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);

                if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                    for (BG0027InvoiceLineAllowances invoiceLineAllowances : invoiceLine.getBG0027InvoiceLineAllowances()) {

                        ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                        Double discountValue = 0d;
                        Double allowanceAmount = invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().isEmpty() ? 0 : invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                        Double baseAmount = invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty() ? 0 : invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount(0).getValue();
                        Double percentage = invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage().isEmpty() ? 0 : invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage(0).getValue();
                        String reason = invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason().isEmpty() ? "Sconto Linea" : invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason(0).getValue();
                        String code = invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode().isEmpty() ? "" : " " + conversionRegistry.convert(Untdid5189ChargeAllowanceDescriptionCodes.class, String.class, invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode(0).getValue());
                        if (allowanceAmount > 0) {
                            discountValue = -allowanceAmount;
                        } else if (baseAmount != 0 && percentage != 0) {
                            discountValue = baseAmount * -percentage;
                        }


                        if (discountValue < 0) {
                            DettaglioLineeType dettaglioLinee1 = new DettaglioLineeType();

                            int number2 = datiBeniServizi.getDettaglioLinee().size() + 1;
                            dettaglioLinee1.setNumeroLinea(number2);
                            log.info("Mapping BG27 to FattPA line number {}", number2);

                            String desc = String.format("%s%s", reason, code);
                            dettaglioLinee1.setDescrizione(desc);
                            log.trace("Set Descrizione with value {}", desc);
                            BigDecimal quantityBd = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity);
                            dettaglioLinee1.setQuantita(quantityBd);
                            log.trace("Set Quantita with value {}", quantityBd);
                            BigDecimal unit = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(discountValue);
                            dettaglioLinee1.setPrezzoUnitario(unit);
                            log.trace("Set PrezzoUnitario with value {}", unit);
                            BigDecimal tot = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowanceAmount);
                            dettaglioLinee1.setPrezzoTotale(tot);
                            log.trace("Set PrezzoTotale with value {}", tot);

                            if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                                BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                                if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                                    dettaglioLinee1.setAliquotaIVA(value);
                                    dettaglioLinee.setAliquotaIVA(value); //Even if BG25 doesn't have it, FatturaPA wants it
                                    log.trace("Set BT119 as AliquotaIVA with value {}", value);
                                }
                            }
                            scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
                            dettaglioLinee1.getScontoMaggiorazione().add(scontoMaggiorazione);
                            datiBeniServizi.getDettaglioLinee().add(dettaglioLinee1);
                        }
                    }
                }

                if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                    DettaglioLineeType dettaglioLinee2 = new DettaglioLineeType();
                    int number28 = datiBeniServizi.getDettaglioLinee().size() + 1;
                    dettaglioLinee2.setNumeroLinea(number28);
                    log.info("Mapping BG28 to FattPA line number {}", number28);
                    Double surchargeValue = 0d;
                    BG0028InvoiceLineCharges invoiceLineCharges = invoiceLine.getBG0028InvoiceLineCharges(0);
                    Double chargeAmount = invoiceLineCharges.getBT0141InvoiceLineChargeAmount().isEmpty() ? 0 : invoiceLineCharges.getBT0141InvoiceLineChargeAmount(0).getValue();
                    Double baseAmount = invoiceLineCharges.getBT0142InvoiceLineChargeBaseAmount().isEmpty() ? 0 : invoiceLineCharges.getBT0142InvoiceLineChargeBaseAmount(0).getValue();
                    Double percentage = invoiceLineCharges.getBT0143InvoiceLineChargePercentage().isEmpty() ? 0 : invoiceLineCharges.getBT0143InvoiceLineChargePercentage(0).getValue();

                    if (chargeAmount > 0) {
                        surchargeValue = chargeAmount;
                    } else if (baseAmount != 0 && percentage != 0) {
                        surchargeValue = baseAmount * percentage;
                    }

                    ScontoMaggiorazioneType scontoMaggiorazione1 = new ScontoMaggiorazioneType();
                    scontoMaggiorazione1.setTipo(TipoScontoMaggiorazioneType.MG);
                    dettaglioLinee2.getScontoMaggiorazione().add(scontoMaggiorazione1);
                    String bt0144 = invoiceLineCharges.getBT0144InvoiceLineChargeReason().isEmpty() ? "Maggiorazione Linea" : invoiceLineCharges.getBT0144InvoiceLineChargeReason(0).getValue();
                    String bt0145 = invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().isEmpty() ? "" : " " + conversionRegistry.convert(Untdid7161SpecialServicesCodes.class, String.class, invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode(0).getValue());
                    if (surchargeValue > 0) {
                        dettaglioLinee2.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);
                        dettaglioLinee2.setDescrizione(String.format("%s%s", bt0144, bt0145));
                        dettaglioLinee2.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity));
                        dettaglioLinee2.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(surchargeValue));
                        dettaglioLinee2.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(chargeAmount * quantity));
                        if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                            BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);
                            if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                dettaglioLinee2.setAliquotaIVA(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue()));
                            }
                        }

                        datiBeniServizi.getDettaglioLinee().add(dettaglioLinee2);
                    }
                }


                if (!invoiceLine.getBG0029PriceDetails().isEmpty()) {
                    BG0029PriceDetails priceDetails = invoiceLine.getBG0029PriceDetails(0);

                    DettaglioLineeType dettaglioLinee3 = new DettaglioLineeType();

                    int number29 = datiBeniServizi.getDettaglioLinee().size() + 1;
                    dettaglioLinee3.setNumeroLinea(number29);
                    log.info("Mapping BG29 to FattPA line number {}", number29);

                    dettaglioLinee3.setDescrizione("Dettaglio Prezzi"); //So FatturaPA doesn't complain

                    Double itemNetPrice = priceDetails.getBT0146ItemNetPrice().isEmpty() ? 0 : priceDetails.getBT0146ItemNetPrice(0).getValue();
                    String quantityUnitOfMeasureCode = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty() ? "" : invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue().getCommonCode();
                    Double baseQuantity = priceDetails.getBT0149ItemPriceBaseQuantity().isEmpty() ? 0 : priceDetails.getBT0149ItemPriceBaseQuantity(0).getValue();
                    String baseQuantityUnitOfMeasureCode = priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty() ? null : priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue().getCommonCode();


                    try {
                        dettaglioLinee3.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity / baseQuantity));
                    } catch (NumberFormatException e) {
                        ArrayList<String> zeroes = Lists.newArrayList();
                        if (quantity == 0) {
                            zeroes.add("BT0129");
                        }

                        if (baseQuantity == 0) {
                            zeroes.add("BT0149");
                        }
                        errors.add(ConversionIssue.newError(e, String.format("These values cannot be 0: %s", zeroes.toString())));
                    }
                    if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
                        Double value = invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue();
                        dettaglioLinee3.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(value));
                    }

                    if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                        BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);
                        if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                            BigDecimal vat = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                            dettaglioLinee3.setAliquotaIVA(vat);
                        }
                    }

                    dettaglioLinee3.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(itemNetPrice));
                    dettaglioLinee3.setUnitaMisura(baseQuantity.toString() + " " + quantityUnitOfMeasureCode);

                    AltriDatiGestionaliType altriDatiGestionaliQty = new AltriDatiGestionaliType();
                    AltriDatiGestionaliType altriDatiGestionaliUnit = new AltriDatiGestionaliType();
                    altriDatiGestionaliQty.setTipoDato(IConstants.ITEM_BASE_QTY);
                    altriDatiGestionaliUnit.setTipoDato(IConstants.ITEM_BASE_PRICE);


                    altriDatiGestionaliQty.setRiferimentoNumero(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(baseQuantity));
                    if (baseQuantityUnitOfMeasureCode != null) {
                        altriDatiGestionaliUnit.setRiferimentoTesto(baseQuantityUnitOfMeasureCode);
                    }

                    dettaglioLinee3.getAltriDatiGestionali().add(altriDatiGestionaliUnit);
                    dettaglioLinee3.getAltriDatiGestionali().add(altriDatiGestionaliQty);

                    datiBeniServizi.getDettaglioLinee().add(dettaglioLinee3);
                }

                mapBG31(invoice, invoiceLine, datiBeniServizi);
            }
        }
    }

    private void mapBG31(BG0000Invoice invoice, BG0025InvoiceLine invoiceLine, DatiBeniServiziType datiBeniServizi) {
        if (!invoiceLine.getBG0031ItemInformation().isEmpty()) {
            BG0031ItemInformation itemInformation = invoiceLine.getBG0031ItemInformation(0);
            DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
            int number = datiBeniServizi.getDettaglioLinee().size() + 1;
            dettaglioLinee.setNumeroLinea(number);
            log.info("Mapping BG31 to FattPA line number {}", number);

            //Just so that FatturaPA doesn't complain about missing elements
            Double quantity = invoiceLine.getBT0129InvoicedQuantity().isEmpty() ? 0 : invoiceLine.getBT0129InvoicedQuantity(0).getValue();
            dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity));

            if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
                Double dValue = invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue();
                BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(dValue);
                dettaglioLinee.setPrezzoTotale(value);
                if (quantity != 0) {
                    dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(dValue / quantity));
                    //PrezzoUnitario is mandatory in FatturaPA
                }
            }
            if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);
                if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                    BigDecimal vat = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                    dettaglioLinee.setAliquotaIVA(vat);
                }
            }

            if (!itemInformation.getBT0153ItemName().isEmpty()) {
                dettaglioLinee.setDescrizione(itemInformation.getBT0153ItemName(0).getValue());
            }

            if (!itemInformation.getBT0155ItemSellerSIdentifier().isEmpty()) {
                CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                codiceArticolo.setCodiceTipo("Seller");
                codiceArticolo.setCodiceValore(itemInformation.getBT0155ItemSellerSIdentifier(0).getValue());
                dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
            }

            if (!itemInformation.getBT0156ItemBuyerSIdentifier().isEmpty()) {
                CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                codiceArticolo.setCodiceTipo("Buyer");
                codiceArticolo.setCodiceValore(itemInformation.getBT0156ItemBuyerSIdentifier(0).getValue());
                dettaglioLinee.getCodiceArticolo().add(codiceArticolo);

            }

            if (!itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier().isEmpty()) {
                CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                codiceArticolo.setCodiceValore(itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier(0).getValue());
                dettaglioLinee.getCodiceArticolo().add(codiceArticolo);

            }

            if (!itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().isEmpty()) {
                for (BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier identifier : itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier()) {
                    CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                    codiceArticolo.setCodiceValore(identifier.getValue());
                    dettaglioLinee.getCodiceArticolo().add(codiceArticolo);

                }
            }

            if (!itemInformation.getBT0159ItemCountryOfOrigin().isEmpty()) {
                AltriDatiGestionaliType altriDati = new AltriDatiGestionaliType();
                altriDati.setRiferimentoTesto(itemInformation.getBT0159ItemCountryOfOrigin(0).getValue().getCountryNameInEnglish()); //FIXME WTH does this mapping means? (see excel)
                dettaglioLinee.getAltriDatiGestionali().add(altriDati);

            }

            if (!itemInformation.getBG0032ItemAttributes().isEmpty()) {
                log.debug("Mapping BG32 to FattPA line");
                for (BG0032ItemAttributes itemAttributes : itemInformation.getBG0032ItemAttributes()) {
                    AltriDatiGestionaliType altriDati = new AltriDatiGestionaliType();
                    altriDati.setTipoDato(itemAttributes.getBT0160ItemAttributeName(0).getValue());
                    altriDati.setRiferimentoTesto(itemAttributes.getBT0161ItemAttributeValue(0).getValue());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDati);
                }
            }
            datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
        }
    }

}
