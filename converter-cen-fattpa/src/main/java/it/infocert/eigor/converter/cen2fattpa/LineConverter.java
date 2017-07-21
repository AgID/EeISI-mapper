package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LineConverter {
    private final static Logger log = LoggerFactory.getLogger(LineConverter.class);
    private final ConversionRegistry conversionRegistry;

    public LineConverter(ConversionRegistry conversionRegistry) {
        this.conversionRegistry = conversionRegistry;
    }

    public Pair<FatturaElettronicaBodyType, List<ConversionIssue>> convert(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<ConversionIssue> errors) {
        if (fatturaElettronicaBody == null) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("Missing FatturaElettronicaBody")));
            return new Pair<>(null, errors);
        }
        if (fatturaElettronicaBody.getDatiBeniServizi() == null) {
            fatturaElettronicaBody.setDatiBeniServizi(new DatiBeniServiziType());
        }
        mapBG20(invoice, fatturaElettronicaBody, errors);
        mapBG21(invoice, fatturaElettronicaBody, errors);
        mapBG25(invoice, fatturaElettronicaBody, errors);
        return new Pair<>(fatturaElettronicaBody, errors);
    }

    private void mapBG20(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<ConversionIssue> errors) {
        if (!invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
            log.info("Mapping BG20 to FattPA line");
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi != null) {
                List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
                for (BG0020DocumentLevelAllowances allowances : invoice.getBG0020DocumentLevelAllowances()) {
                    DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                    int number = datiBeniServizi.getDettaglioLinee().size() + 1;
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

    private void mapBG21(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<ConversionIssue> errors) {
        if (!invoice.getBG0021DocumentLevelCharges().isEmpty()) {
            log.info("Mapping BG21 to FattPA line");
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi != null) {
                List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
                for (BG0021DocumentLevelCharges charges : invoice.getBG0021DocumentLevelCharges()) {
                    DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                    int number = datiBeniServizi.getDettaglioLinee().size() + 1;
                    dettaglioLinee.setNumeroLinea(number);
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

                    sb.append(" ");

                    if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                        Untdid7161SpecialServicesCodes code = charges.getBT0105DocumentLevelChargeReasonCode(0).getValue();

                        sb.append("TC01"); //FIXME Placeholder, ask for better mapping!
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


    private void mapBG25(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<ConversionIssue> errors) {
        if (!invoice.getBG0025InvoiceLine().isEmpty()) {
            log.info("Mapping BG25 to FattPA line");
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            for (BG0025InvoiceLine invoiceLine : invoice.getBG0025InvoiceLine()) {
                DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                
                if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue());
                    dettaglioLinee.setPrezzoTotale(value);
                    log.trace("Set BT131 as PrezzoTotale with value {}", value);
                }
                Double quantity = invoiceLine.getBT0129InvoicedQuantity().isEmpty() ? 0 : invoiceLine.getBT0129InvoicedQuantity(0).getValue();
                dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity));
                log.trace("Set BT129 as Quantita with value {}", quantity);
                int number = datiBeniServizi.getDettaglioLinee().size() + 1;
                dettaglioLinee.setNumeroLinea(number);
                log.trace("Set NumeroLinea with value {}", number);
                ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
                scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
                dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
                log.trace("Set ScontoMaggiorazione with type {}", TipoScontoMaggiorazioneType.SC);
                datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);

                if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                    log.info("Mapping BG27 to FattPA line");
                    for (BG0027InvoiceLineAllowances invoiceLineAllowances : invoiceLine.getBG0027InvoiceLineAllowances()) {

                        Double discountValue = 0d;
                        Double allowanceAmount = invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().isEmpty() ? 0 : invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                        Double baseAmount = invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty() ? 0 : invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount(0).getValue();
                        Double percentage = invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage().isEmpty() ? 0 : invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage(0).getValue();
                        String reason = invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason().isEmpty() ? "Sconto Linea" : invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason(0).getValue();
                        String code = invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode().isEmpty() ? "" : conversionRegistry.convert(Untdid5189ChargeAllowanceDescriptionCodes.class, String.class, invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode(0).getValue());
                        if (allowanceAmount > 0) {
                            discountValue = -allowanceAmount;
                        } else if (baseAmount != 0 && percentage != 0) {
                            discountValue = baseAmount * -percentage;
                        }


                        if (discountValue < 0) {
                            DettaglioLineeType dettaglioLinee1 = new DettaglioLineeType();

                            int number2 = datiBeniServizi.getDettaglioLinee().size() + 1;
                            dettaglioLinee1.setNumeroLinea(number2);
                            log.trace("Set NumeroLinea with value {}", number2);

                            String desc = String.format("%s %s", reason, code);
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
                                    log.trace("Set BT119 as AliquotaIVA with value {}", value);
                                }
                            }
                            ScontoMaggiorazioneType scontoMaggiorazione2 = new ScontoMaggiorazioneType();
                            scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
                            dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione2);
                            datiBeniServizi.getDettaglioLinee().add(dettaglioLinee1);
                        }
                    }
                }

                if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                    log.info("Mapping BG28 to FattPA line");
                    DettaglioLineeType dettaglioLinee2 = new DettaglioLineeType();
                    dettaglioLinee2.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);
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

                    ScontoMaggiorazioneType scontoMaggiorazione3 = new ScontoMaggiorazioneType();
                    scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.MG);
                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione3);
                    String bt0144 = invoiceLineCharges.getBT0144InvoiceLineChargeReason().isEmpty() ? "Maggiorazione Linea" : invoiceLineCharges.getBT0144InvoiceLineChargeReason(0).getValue();
                    String bt0145 = invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().isEmpty() ? "" : conversionRegistry.convert(Untdid7161SpecialServicesCodes.class, String.class, invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode(0).getValue());
                    if (surchargeValue > 0) {
                        dettaglioLinee2.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);
                        dettaglioLinee2.setDescrizione(String.format("%s %s", bt0144, bt0145));
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
                    log.info("Mapping BG29 to FattPA line");
                    BG0029PriceDetails priceDetails = invoiceLine.getBG0029PriceDetails(0);

                    DettaglioLineeType dettaglioLinee3 = new DettaglioLineeType();
                    
                    dettaglioLinee3.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);
                    Double bt0146 = priceDetails.getBT0146ItemNetPrice().isEmpty() ? 0 : priceDetails.getBT0146ItemNetPrice(0).getValue();
                    String bt0130 = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty() ? "" : invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue().getCommonCode();
                    Double bt0149 = priceDetails.getBT0149ItemPriceBaseQuantity().isEmpty() ? 0 : priceDetails.getBT0149ItemPriceBaseQuantity(0).getValue();
                    String bt0150 = priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty() ? null : priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue().getCommonCode();


                    try {
                        dettaglioLinee3.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(quantity / bt0149));
                    } catch (NumberFormatException e) {
                        ArrayList<String> zeroes = Lists.newArrayList();
                        if (quantity == 0) {
                            zeroes.add("BT0129");
                        }

                        if (bt0149 == 0) {
                            zeroes.add("BT0149");
                        }
                        errors.add(ConversionIssue.newError(e, String.format("These values cannot be 0: %s", zeroes.toString())));
                    }

                    dettaglioLinee3.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(bt0146));
                    dettaglioLinee3.setUnitaMisura(bt0149.toString() + " " + bt0130);

                    AltriDatiGestionaliType altriDatiGestionaliQty = new AltriDatiGestionaliType();
                    AltriDatiGestionaliType altriDatiGestionaliUnit = new AltriDatiGestionaliType();
                    altriDatiGestionaliQty.setTipoDato(IConstants.ITEM_BASE_QTY);
                    altriDatiGestionaliUnit.setTipoDato(IConstants.ITEM_BASE_PRICE);


                    altriDatiGestionaliQty.setRiferimentoNumero(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(bt0149));
                    if (bt0150 != null) {
                        altriDatiGestionaliUnit.setRiferimentoTesto(bt0150);
                    }

                    dettaglioLinee3.getAltriDatiGestionali().add(altriDatiGestionaliUnit);
                    dettaglioLinee3.getAltriDatiGestionali().add(altriDatiGestionaliQty);

                    datiBeniServizi.getDettaglioLinee().add(dettaglioLinee3);
                }

                mapBG31(invoiceLine, datiBeniServizi);
            }
        }
    }

    private void mapBG31(BG0025InvoiceLine invoiceLine, DatiBeniServiziType datiBeniServizi) {
        if (!invoiceLine.getBG0031ItemInformation().isEmpty()) {
            log.info("Mapping BG31 to FattPA line");
            BG0031ItemInformation itemInformation = invoiceLine.getBG0031ItemInformation(0);
            DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
            dettaglioLinee.setNumeroLinea(datiBeniServizi.getDettaglioLinee().size() + 1);

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
                log.info("Mapping BG32 to FattPA line");
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
