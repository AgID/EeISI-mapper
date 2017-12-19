package it.infocert.eigor.converter.cen2fattpa;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import com.amoerie.jstreams.functions.Mapper;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.converter.cen2fattpa.converters.*;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
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
            new Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter(),
            new LocalDateToXMLGregorianCalendarConverter()
    );


    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("Too many FatturaElettronicaBody found in current FatturaElettronica")));
        } else if (size < 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No FatturaElettronicaBody found in current FatturaElettronica")));
        } else {
            FatturaElettronicaBodyType fatturaElettronicaBody = bodies.get(0);
            if (fatturaElettronicaBody.getDatiBeniServizi() == null) {
                fatturaElettronicaBody.setDatiBeniServizi(new DatiBeniServiziType());
            }

            mapBG20(invoice, fatturaElettronicaBody, errors);
            mapBG21(invoice, fatturaElettronicaBody, errors);
            mapBG25(invoice, fatturaElettronicaBody, errors);

            mapLineChargesAllowances(invoice, fatturaElettronicaBody, errors);
            mapDocumentChargesAllowances(invoice, fatturaElettronicaBody, errors);

            mapBt73and74(invoice, fatturaElettronicaBody, errors);
        }
    }

    private void mapBt73and74(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!datesAlreadyExistent(invoice)) {
            if (!invoice.getBG0013DeliveryInformation().isEmpty()) {
                BG0013DeliveryInformation bg0013 = invoice.getBG0013DeliveryInformation(0);
                if (!bg0013.getBG0014InvoicingPeriod().isEmpty()) {
                    BG0014InvoicingPeriod bg0014 = bg0013.getBG0014InvoicingPeriod(0);
                    List<DettaglioLineeType> dettaglioLinee = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee();
                    if (dettaglioLinee != null) {
                        LocalDate startDate =
                                !bg0014.getBT0073InvoicingPeriodStartDate().isEmpty()
                                        ? bg0014.getBT0073InvoicingPeriodStartDate(0).getValue()
                                        : null;

                        LocalDate endDate
                                = !bg0014.getBT0074InvoicingPeriodEndDate().isEmpty()
                                ? bg0014.getBT0074InvoicingPeriodEndDate(0).getValue()
                                : null;

                        for (DettaglioLineeType linea : dettaglioLinee) {
                            try {

                                if (startDate != null) {
                                    linea.setDataInizioPeriodo(conversionRegistry.convert(LocalDate.class, XMLGregorianCalendar.class, startDate));
                                }
                                if (endDate != null) {
                                    linea.setDataFinePeriodo(conversionRegistry.convert(LocalDate.class, XMLGregorianCalendar.class, endDate));
                                }
                            } catch (EigorRuntimeException e) {
                                errors.add(ConversionIssue.newError(e));
                            }
                        }

                    }
                }
            }
        }
    }

    private boolean datesAlreadyExistent(BG0000Invoice invoice) {
        Stream<BG0026InvoiceLinePeriod> notNullPeriodsStream = Stream.create(invoice.getBG0025InvoiceLine())
                .map(new Mapper<BG0025InvoiceLine, BG0026InvoiceLinePeriod>() {
                    @Override
                    public BG0026InvoiceLinePeriod map(BG0025InvoiceLine invoiceLine) {
                        List<BG0026InvoiceLinePeriod> periods = invoiceLine.getBG0026InvoiceLinePeriod();
                        if (!periods.isEmpty()) {
                            return periods.get(0);
                        }
                        return null;
                    }
                }).filter(new Filter<BG0026InvoiceLinePeriod>() {
                    @Override
                    public boolean apply(BG0026InvoiceLinePeriod period) {
                        return period != null;
                    }
                });

        int bt134Counter = notNullPeriodsStream
                .filter(new Filter<BG0026InvoiceLinePeriod>() {
                    @Override
                    public boolean apply(BG0026InvoiceLinePeriod period) {
                        return !period.getBT0134InvoiceLinePeriodStartDate().isEmpty();
                    }
                })
                .map(new Mapper<BG0026InvoiceLinePeriod, BT0134InvoiceLinePeriodStartDate>() {
                    @Override
                    public BT0134InvoiceLinePeriodStartDate map(BG0026InvoiceLinePeriod period) {
                        return period.getBT0134InvoiceLinePeriodStartDate(0);
                    }
                })
                .length();

        int bt135Counter = notNullPeriodsStream
                .filter(new Filter<BG0026InvoiceLinePeriod>() {
                    @Override
                    public boolean apply(BG0026InvoiceLinePeriod period) {
                        return !period.getBT0135InvoiceLinePeriodEndDate().isEmpty();
                    }
                })
                .map(new Mapper<BG0026InvoiceLinePeriod, BT0135InvoiceLinePeriodEndDate>() {
                    @Override
                    public BT0135InvoiceLinePeriodEndDate map(BG0026InvoiceLinePeriod period) {
                        return period.getBT0135InvoiceLinePeriodEndDate(0);
                    }
                })
                .length();

        return bt134Counter > 0 && bt135Counter > 0;
    }


    private void mapBG20(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi == null) {
                datiBeniServizi = new DatiBeniServiziType();
            }
            List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
            if (dettaglioLineeList.size() < invoice.getBG0020DocumentLevelAllowances().size()) {
                int n = invoice.getBG0020DocumentLevelAllowances().size() - dettaglioLineeList.size();
                createMissingLines(dettaglioLineeList, n);
            }
            for (int i = 0; i < invoice.getBG0020DocumentLevelAllowances().size(); i++) {
                BG0020DocumentLevelAllowances allowances = invoice.getBG0020DocumentLevelAllowances(i);
                DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);

                if (!allowances.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowances.getBT0092DocumentLevelAllowanceAmount(0).getValue());
                    dettaglioLinee.setPrezzoUnitario(value);
                    dettaglioLinee.setPrezzoTotale(value);
                    log.trace("Set BT92 as PrezzoUnitario and PrezzoTotale with value {}", value);
                }

                if (!allowances.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                    AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                    String value = String.valueOf(allowances.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue());
                    dati.setTipoDato("BT-93");  //???
                    dati.setRiferimentoTesto(value);
                    log.trace("Set BT93 as RiferimentoTesto with value {}", value);
                    dettaglioLinee.getAltriDatiGestionali().add(dati);
                }

                if (!allowances.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                    Untdid5305DutyTaxFeeCategories category = allowances.getBT0095DocumentLevelAllowanceVatCategoryCode(0).getValue();
                    switch (category) {
                        case Z:
8                            dettaglioLinee.setNatura(NaturaType.N_3); //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
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
                    try {
                        String converted = converter.convert(code);
                        sb.append(converted);
                        log.trace("Appended BT98 to Descrizione");
                    } catch (EigorRuntimeException e) {
                        errors.add(ConversionIssue.newError(e));
                    }
                }

                String des = sb.toString();
                dettaglioLinee.setDescrizione(des);
                log.trace("Set \"{}\" as Descrizione", des);
//                    ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
//                    scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
//                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
//                    log.trace("Set ScontoMaggiorazione with type {}", TipoScontoMaggiorazioneType.SC);
            }
        }
    }

    private void mapBG21(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0021DocumentLevelCharges().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi == null) {
                datiBeniServizi = new DatiBeniServiziType();
            }
            List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
            if (dettaglioLineeList.size() < invoice.getBG0021DocumentLevelCharges().size()) {
                int n = invoice.getBG0021DocumentLevelCharges().size() - dettaglioLineeList.size();
                createMissingLines(dettaglioLineeList, n);
            }
            for (int i = 0; i < invoice.getBG0021DocumentLevelCharges().size(); i++) {
                BG0021DocumentLevelCharges charges = invoice.getBG0021DocumentLevelCharges(i);
                DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);

                if (!charges.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0099DocumentLevelChargeAmount(0).getValue());
                    dettaglioLinee.setPrezzoUnitario(value);
                    dettaglioLinee.setPrezzoTotale(value);
                    log.trace("Set BT99 as PrezzoUnitario and PrezzoTotale with value {}", value);
                }

                if (!charges.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0100DocumentLevelChargeBaseAmount(0).getValue());
                    dati.setTipoDato("BT-100"); //???
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
//                    ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
//                    scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
//                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
//                    log.trace("Set ScontoMaggiorazione with type {}", TipoScontoMaggiorazioneType.SC);
            }
        }
    }


    private void mapBG25(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0025InvoiceLine().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
            if (dettaglioLineeList.size() < invoice.getBG0025InvoiceLine().size()) {
                int n = invoice.getBG0025InvoiceLine().size() - dettaglioLineeList.size();
                createMissingLines(dettaglioLineeList, n);
            }
            for (int i = 0; i < invoice.getBG0025InvoiceLine().size(); i++) {
                DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
                BG0025InvoiceLine invoiceLine = invoice.getBG0025InvoiceLine(i);

                Double quantity = invoiceLine.getBT0129InvoicedQuantity().isEmpty() ? 0 : invoiceLine.getBT0129InvoicedQuantity(0).getValue();

                dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(quantity, 8));
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

                if (!invoiceLine.getBG0030LineVatInformation().isEmpty()) {
                    BG0030LineVatInformation lineVatInformation = invoiceLine.getBG0030LineVatInformation(0);
                    if (!lineVatInformation.getBT0152InvoicedItemVatRate().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(lineVatInformation.getBT0152InvoicedItemVatRate(0).getValue());
                        dettaglioLinee.setAliquotaIVA(value);
                    } else {
                        if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                            BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                            if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                                dettaglioLinee.setAliquotaIVA(value); //Even if BG25 doesn't have it, FatturaPA wants it
                            }
                        }
                    }
                }

                if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                    for (BG0027InvoiceLineAllowances invoiceLineAllowances : invoiceLine.getBG0027InvoiceLineAllowances()) {

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
                            String desc = String.format("%s%s", reason, code);
                            dettaglioLinee.setDescrizione(desc);
                            log.trace("Set Descrizione with value {}", desc);
                            BigDecimal quantityBd = Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(quantity, 8);
                            dettaglioLinee.setQuantita(quantityBd);
                            log.trace("Set Quantita with value {}", quantityBd);
                            BigDecimal unit = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(discountValue);
                            dettaglioLinee.setPrezzoUnitario(unit);
                            log.trace("Set PrezzoUnitario with value {}", unit);
                            BigDecimal tot = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowanceAmount);
                            dettaglioLinee.setPrezzoTotale(tot);
                            log.trace("Set PrezzoTotale with value {}", tot);

//                            ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
//                            scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
//                            dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
                        }
                    }
                }

                if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
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

//                    ScontoMaggiorazioneType scontoMaggiorazione1 = new ScontoMaggiorazioneType();
//                    scontoMaggiorazione1.setTipo(TipoScontoMaggiorazioneType.MG);
//                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione1);
                    String bt0144 = invoiceLineCharges.getBT0144InvoiceLineChargeReason().isEmpty() ? "Maggiorazione Linea" : invoiceLineCharges.getBT0144InvoiceLineChargeReason(0).getValue();
                    String bt0145 = invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().isEmpty() ? "" : " " + conversionRegistry.convert(Untdid7161SpecialServicesCodes.class, String.class, invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode(0).getValue());
                    if (surchargeValue > 0) {
                        dettaglioLinee.setDescrizione(String.format("%s%s", bt0144, bt0145));
                        dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(quantity, 8));
                        dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(surchargeValue));
                        dettaglioLinee.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(chargeAmount * quantity));
                        if (!invoiceLine.getBG0030LineVatInformation().isEmpty()) {
                            BG0030LineVatInformation lineVatInformation = invoiceLine.getBG0030LineVatInformation(0);
                            if (!lineVatInformation.getBT0152InvoicedItemVatRate().isEmpty()) {
                                BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(lineVatInformation.getBT0152InvoicedItemVatRate(0).getValue());
                                dettaglioLinee.setAliquotaIVA(value);
                            } else {
                                if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                                    BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                                    if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                                        dettaglioLinee.setAliquotaIVA(value); //Even if BG25 doesn't have it, FatturaPA wants it
                                    }
                                }
                            }
                        }
                    }
                }


                if (!invoiceLine.getBG0029PriceDetails().isEmpty()) {
                    BG0029PriceDetails priceDetails = invoiceLine.getBG0029PriceDetails(0);

                    Double itemNetPrice = priceDetails.getBT0146ItemNetPrice().isEmpty() ? 0 : priceDetails.getBT0146ItemNetPrice(0).getValue();
                    String quantityUnitOfMeasureCode = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty() ? "" : invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue().getCommonCode();
                    Double baseQuantity = priceDetails.getBT0149ItemPriceBaseQuantity().isEmpty() ? 1 : priceDetails.getBT0149ItemPriceBaseQuantity(0).getValue();
                    String baseQuantityUnitOfMeasureCode = priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty() ? null : priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue().getCommonCode();

                    try {
                        dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(quantity / baseQuantity, 8));
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
                        dettaglioLinee.setPrezzoTotale(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(value));
                    }

                    dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(itemNetPrice));
                    if (baseQuantity == 0) {
                        dettaglioLinee.setUnitaMisura(quantityUnitOfMeasureCode);
                    } else {
                        dettaglioLinee.setUnitaMisura(baseQuantity.toString() + " " + quantityUnitOfMeasureCode);
                    }

                    if (baseQuantity > 1) {
                        AltriDatiGestionaliType altriDatiGestionaliQty = new AltriDatiGestionaliType();
                        altriDatiGestionaliQty.setTipoDato(IConstants.ITEM_BASE_QTY);
                        altriDatiGestionaliQty.setRiferimentoNumero(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(baseQuantity));
                        dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliQty);


                        AltriDatiGestionaliType altriDatiGestionaliUnit = new AltriDatiGestionaliType();
                        altriDatiGestionaliUnit.setTipoDato(IConstants.ITEM_BASE_PRICE);


                        if (baseQuantityUnitOfMeasureCode != null) {
                            altriDatiGestionaliUnit.setRiferimentoTesto(baseQuantityUnitOfMeasureCode);
                        }

                        dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliUnit);
                    }

                }

                mapBG31(invoice, invoiceLine, dettaglioLinee);
            }
        }
    }

    private void mapBG31(BG0000Invoice invoice, BG0025InvoiceLine invoiceLine, DettaglioLineeType dettaglioLinee) {
        if (!invoiceLine.getBG0031ItemInformation().isEmpty()) {
            BG0031ItemInformation itemInformation = invoiceLine.getBG0031ItemInformation(0);

//            //Just so that FatturaPA doesn't complain about missing elements
//            Double quantity = invoiceLine.getBT0129InvoicedQuantity().isEmpty() ? 0 : invoiceLine.getBT0129InvoicedQuantity(0).getValue();
//            dettaglioLinee.setQuantita(Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(quantity, 8));
//
//            if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
//                Double dValue = invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue();
//                BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(dValue);
//                dettaglioLinee.setPrezzoTotale(value);
//                if (quantity != 0) {
//                    dettaglioLinee.setPrezzoUnitario(Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(dValue / quantity));
//                    //PrezzoUnitario is mandatory in FatturaPA
//                }

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
                codiceArticolo.setCodiceValore(itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier(0).getValue().getIdentifier());
                dettaglioLinee.getCodiceArticolo().add(codiceArticolo);

            }

            if (!itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().isEmpty()) {
                for (BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier identifier : itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier()) {
                    CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                    codiceArticolo.setCodiceValore(identifier.getValue().getIdentifier());
                    dettaglioLinee.getCodiceArticolo().add(codiceArticolo);

                }
            }

            if (!itemInformation.getBT0159ItemCountryOfOrigin().isEmpty()) {
                AltriDatiGestionaliType altriDati = new AltriDatiGestionaliType();
                altriDati.setRiferimentoTesto(itemInformation.getBT0159ItemCountryOfOrigin(0).getValue().getIso2charCode());
                altriDati.setTipoDato("BT-159");
                dettaglioLinee.getAltriDatiGestionali().add(altriDati);

            }

            if (!itemInformation.getBG0032ItemAttributes().isEmpty()) {
                log.debug("Mapping BG32 to FattPA line");
                for (BG0032ItemAttributes itemAttributes : itemInformation.getBG0032ItemAttributes()) {
                    AltriDatiGestionaliType altriDati = new AltriDatiGestionaliType();
                    if (!itemAttributes.getBT0160ItemAttributeName().isEmpty()) {
                        altriDati.setTipoDato(itemAttributes.getBT0160ItemAttributeName(0).getValue());
                    }
                    if (!itemAttributes.getBT0161ItemAttributeValue().isEmpty()) {
                        altriDati.setRiferimentoTesto(itemAttributes.getBT0161ItemAttributeValue(0).getValue());
                    }
                    dettaglioLinee.getAltriDatiGestionali().add(altriDati);
                }
            }
        }
    }

    @SuppressWarnings({"Duplicates"})
    private void mapLineChargesAllowances(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        if (!invoice.getBG0025InvoiceLine().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
            if (dettaglioLineeList.size() < invoice.getBG0025InvoiceLine().size()) {
                int n = invoice.getBG0025InvoiceLine().size() - dettaglioLineeList.size();
                createMissingLines(dettaglioLineeList, n);
            }
            for (int i = 0; i < invoice.getBG0025InvoiceLine().size(); i++) {
                DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
                BG0025InvoiceLine invoiceLine = invoice.getBG0025InvoiceLine(i);

                BigDecimal vatLine = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(0.0);
                if (!invoiceLine.getBG0030LineVatInformation().isEmpty()) {
                    BG0030LineVatInformation lineVatInformation = invoiceLine.getBG0030LineVatInformation(0);
                    if (!lineVatInformation.getBT0151InvoicedItemVatCategoryCode().isEmpty()) {
                        Untdid5305DutyTaxFeeCategories category = lineVatInformation.getBT0151InvoicedItemVatCategoryCode(0).getValue();
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
                    }

                    if (!lineVatInformation.getBT0152InvoicedItemVatRate().isEmpty()) {
                        BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(lineVatInformation.getBT0152InvoicedItemVatRate(0).getValue());
                        dettaglioLinee.setAliquotaIVA(value);
                        vatLine = value;
                    } else {
                        if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                            BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                            if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(vatBreakdown.getBT0119VatCategoryRate(0).getValue());
                                dettaglioLinee.setAliquotaIVA(value); //Even if BG25 doesn't have it, FatturaPA wants it
                                vatLine = value;
                            }
                        }
                    }
                }
                //ALLOWANCES
                if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                    for (BG0027InvoiceLineAllowances invoiceLineAllowances : invoiceLine.getBG0027InvoiceLineAllowances()) {
                        DettaglioLineeType lineaSconto = new DettaglioLineeType();
                        lineaSconto.setNumeroLinea(dettaglioLinee.getNumeroLinea());
                        lineaSconto.setTipoCessionePrestazione(TipoCessionePrestazioneType.SC);
                        String descrizione = invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason(0).getValue();
                        if (descrizione != null) {
                            lineaSconto.setDescrizione(descrizione);
                        } else {
                            Untdid5189ChargeAllowanceDescriptionCodes code = invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode(0).getValue();
                            Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter converter = new Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter();
                            descrizione = converter.convert(code);
                            if (descrizione != null) {
                                lineaSconto.setDescrizione(descrizione);
                            }
                        }
                        Double allowanceAmount = invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().isEmpty() ? 0 : invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                        allowanceAmount *= -1.0;
                        BigDecimal prezzo = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowanceAmount);
                        lineaSconto.setPrezzoUnitario(prezzo);
                        lineaSconto.setPrezzoTotale(prezzo);
                        lineaSconto.setAliquotaIVA(vatLine);
                        if (!"0.0".equals(vatLine.toString()) && !invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
                            BG0020DocumentLevelAllowances allowances = invoice.getBG0020DocumentLevelAllowances(0);
                            if (!allowances.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                                Untdid5305DutyTaxFeeCategories category = allowances.getBT0095DocumentLevelAllowanceVatCategoryCode(0).getValue();
                                switch (category) {
                                    case Z:
                                        lineaSconto.setNatura(NaturaType.N_3); //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
                                        break;
                                    case E:
                                        lineaSconto.setNatura(NaturaType.N_4);
                                        break;
                                    case G:
                                        lineaSconto.setNatura(NaturaType.N_2);
                                        break;
                                    case O:
                                        lineaSconto.setNatura(NaturaType.N_2); //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
                                        break;
                                    default:
                                        lineaSconto.setNatura(null);
                                }
                            }
                        }
                        if (!invoiceLine.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                            BT0133InvoiceLineBuyerAccountingReference invoiceLineBuyerAccountingReference = invoiceLine.getBT0133InvoiceLineBuyerAccountingReference(0);
                            lineaSconto.setRiferimentoAmministrazione(invoiceLineBuyerAccountingReference.getValue());
                        }
                        datiBeniServizi.getDettaglioLinee().add(lineaSconto);
                    }
                }
                //CHARGES
                if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                    for (BG0028InvoiceLineCharges invoiceLineCharges : invoiceLine.getBG0028InvoiceLineCharges()) {
                        DettaglioLineeType lineaMaggiorazione = new DettaglioLineeType();
                        lineaMaggiorazione.setNumeroLinea(dettaglioLinee.getNumeroLinea());
                        lineaMaggiorazione.setTipoCessionePrestazione(TipoCessionePrestazioneType.SC);
                        String descrizione = invoiceLineCharges.getBT0144InvoiceLineChargeReason(0).getValue();
                        if (descrizione != null) {
                            lineaMaggiorazione.setDescrizione(descrizione);
                        } else {
                            Untdid7161SpecialServicesCodes code = invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode(0).getValue();
                            Untdid7161SpecialServicesCodesToItalianCodeStringConverter converter = new Untdid7161SpecialServicesCodesToItalianCodeStringConverter();
                            descrizione = converter.convert(code);
                            if (descrizione != null) {
                                lineaMaggiorazione.setDescrizione(descrizione);
                            }
                        }
                        Double chargeAmount = invoiceLineCharges.getBT0141InvoiceLineChargeAmount().isEmpty() ? 0 : invoiceLineCharges.getBT0141InvoiceLineChargeAmount(0).getValue();
                        BigDecimal prezzo = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(chargeAmount);
                        lineaMaggiorazione.setPrezzoUnitario(prezzo);
                        lineaMaggiorazione.setPrezzoTotale(prezzo);
                        lineaMaggiorazione.setAliquotaIVA(vatLine);
                        if (!vatLine.toString().equals("0.0") && !invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
                            BG0020DocumentLevelAllowances allowances = invoice.getBG0020DocumentLevelAllowances(0);
                            if (!allowances.getBT0095DocumentLevelAllowanceVatCategoryCode().isEmpty()) {
                                Untdid5305DutyTaxFeeCategories category = allowances.getBT0095DocumentLevelAllowanceVatCategoryCode(0).getValue();
                                switch (category) {
                                    case Z:
                                        lineaMaggiorazione.setNatura(NaturaType.N_3); //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
                                        break;
                                    case E:
                                        lineaMaggiorazione.setNatura(NaturaType.N_4);
                                        break;
                                    case G:
                                        lineaMaggiorazione.setNatura(NaturaType.N_2);
                                        break;
                                    case O:
                                        lineaMaggiorazione.setNatura(NaturaType.N_2); //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
                                        break;
                                    default:
                                        lineaMaggiorazione.setNatura(null);
                                }
                            }
                        }
                        if (!invoiceLine.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                            BT0133InvoiceLineBuyerAccountingReference invoiceLineBuyerAccountingReference = invoiceLine.getBT0133InvoiceLineBuyerAccountingReference(0);
                            lineaMaggiorazione.setRiferimentoAmministrazione(invoiceLineBuyerAccountingReference.getValue());
                        }
                        datiBeniServizi.getDettaglioLinee().add(lineaMaggiorazione);
                    }
                }
            }
        }
    }

    private void mapDocumentChargesAllowances(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors) {
        //ALLOWANCES
        if (!invoice.getBG0020DocumentLevelAllowances().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi == null) {
                datiBeniServizi = new DatiBeniServiziType();
            }
            for (int i = 0; i < invoice.getBG0020DocumentLevelAllowances().size(); i++) {
                BG0020DocumentLevelAllowances allowances = invoice.getBG0020DocumentLevelAllowances(i);
                DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                log.trace("Processing DettaglioLinee for BG0020");
                dettaglioLinee.setNumeroLinea(9999);
                dettaglioLinee.setTipoCessionePrestazione(TipoCessionePrestazioneType.SC);
                String reason = "Sconto Documento";
                String baseAmount = "N/A";
                String percentage = "N/A";
                String converted = "";
                if (!allowances.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                    Untdid5189ChargeAllowanceDescriptionCodes code = allowances.getBT0098DocumentLevelAllowanceReasonCode(0).getValue();
                    Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter converter = new Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter();
                    try {
                        converted = converter.convert(code);
                        dettaglioLinee.setRiferimentoAmministrazione(converted);
                    } catch (EigorRuntimeException e) {
                        errors.add(ConversionIssue.newError(e));
                    }
                } else {
                    log.trace("No BT0098 found");
                }

                if (!allowances.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                    reason = allowances.getBT0097DocumentLevelAllowanceReason(0).getValue();
                    if (!allowances.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                        baseAmount = String.valueOf(allowances.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue());
                    } else {
                        log.trace("No BT0093 found");
                    }

                    if (!allowances.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                        percentage = String.valueOf(allowances.getBT0094DocumentLevelAllowancePercentage(0).getValue());
                    } else {
                        log.trace("No BT0094 found");
                    }

                    dettaglioLinee.setDescrizione(reason + " - Base Amount: " + baseAmount + " Percentage " + percentage + "%");
                } else if (!allowances.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                    dettaglioLinee.setRiferimentoAmministrazione(converted);
                } else {
                    log.trace("No BT0097 found");
                }

                BigDecimal quantitaCedute = Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(1.0, 8);
                dettaglioLinee.setQuantita(quantitaCedute);
                if (!allowances.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {

                    Double allowanceAmount = allowances.getBT0092DocumentLevelAllowanceAmount(0).getValue();
                    allowanceAmount *= -1.0;
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowanceAmount);

                    dettaglioLinee.setPrezzoUnitario(value);
                    dettaglioLinee.setPrezzoTotale(value);
                } else {
                    log.trace("No BT0092 found");
                }

                if (!allowances.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(allowances.getBT0096DocumentLevelAllowanceVatRate(0).getValue());
                    dettaglioLinee.setAliquotaIVA(value);
                } else {
                    log.trace("No BT0096 found");
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
                } else {
                    log.trace("No BT0095 found");
                }

                log.trace("Processing AltriDatiGestionali for BG0020");
                //altridatigestionali
                AltriDatiGestionaliType altriDatiGestionaliType;
                if (!allowances.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-93");
                    altriDatiGestionaliType.setRiferimentoTesto(String.valueOf(allowances.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue()));
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0093 found");
                }

                if (!allowances.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-94");
                    altriDatiGestionaliType.setRiferimentoTesto(String.valueOf(allowances.getBT0094DocumentLevelAllowancePercentage(0).getValue()));
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0094 found");
                }

                if (!allowances.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-97");
                    altriDatiGestionaliType.setRiferimentoTesto(allowances.getBT0097DocumentLevelAllowanceReason(0).getValue());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0097 found");
                }

                if (!allowances.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-98");
                    altriDatiGestionaliType.setRiferimentoTesto(converted);
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0098 found");
                }

                datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
            }
        } else {
            log.trace("No BG0020 found");
        }
        //CHARGES
        if (!invoice.getBG0021DocumentLevelCharges().isEmpty()) {
            DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            if (datiBeniServizi == null) {
                datiBeniServizi = new DatiBeniServiziType();
            }
            for (int i = 0; i < invoice.getBG0021DocumentLevelCharges().size(); i++) {
                BG0021DocumentLevelCharges charges = invoice.getBG0021DocumentLevelCharges(i);
                DettaglioLineeType dettaglioLinee = new DettaglioLineeType();
                log.trace("Processing DettaglioLinee for BG0021");
                dettaglioLinee.setNumeroLinea(9999);
                dettaglioLinee.setTipoCessionePrestazione(TipoCessionePrestazioneType.SC);
                String reason;
                String baseAmount = "N/A";
                String percentage = "N/A";
                String converted = "";
                if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                    Untdid7161SpecialServicesCodes code = charges.getBT0105DocumentLevelChargeReasonCode(0).getValue();
                    Untdid7161SpecialServicesCodesToItalianCodeStringConverter converter = new Untdid7161SpecialServicesCodesToItalianCodeStringConverter();
                    try {
                        converted = converter.convert(code);
                        dettaglioLinee.setRiferimentoAmministrazione(converted);
                    } catch (EigorRuntimeException e) {
                        errors.add(ConversionIssue.newError(e));
                    }
                } else {
                    log.trace("No BT0105 found");
                }

                if (!charges.getBT0104DocumentLevelChargeReason().isEmpty()) {
                    reason = charges.getBT0104DocumentLevelChargeReason(0).getValue();
                    if (!charges.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                        baseAmount = String.valueOf(charges.getBT0100DocumentLevelChargeBaseAmount(0).getValue());
                    } else {
                        log.trace("No BT0100 found");
                    }

                    if (!charges.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                        percentage = String.valueOf(charges.getBT0101DocumentLevelChargePercentage(0).getValue());
                    } else {
                        log.trace("No BT0101 found");
                    }

                    dettaglioLinee.setDescrizione(reason + " - Base Amount: " + baseAmount + " Percentage " + percentage + "%");
                } else if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                    dettaglioLinee.setRiferimentoAmministrazione(converted);
                } else {
                    log.trace("No BT0104 found");
                }

                BigDecimal quantitaCedute = Cen2FattPAConverterUtils.doubleToBigDecimalWithDecimals(1.0, 8);
                dettaglioLinee.setQuantita(quantitaCedute);
                if (!charges.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0099DocumentLevelChargeAmount(0).getValue());
                    dettaglioLinee.setPrezzoUnitario(value);
                    dettaglioLinee.setPrezzoTotale(value);
                } else {
                    log.trace("No BT0099 found");
                }

                if (!charges.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
                    BigDecimal value = Cen2FattPAConverterUtils.doubleToBigDecimalWith2Decimals(charges.getBT0103DocumentLevelChargeVatRate(0).getValue());
                    dettaglioLinee.setAliquotaIVA(value);
                } else {
                    log.trace("No BT0103 found");
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
                } else {
                    log.trace("No BT0102 found");
                }

                //altridatigestionali
                log.trace("Processing AltriDatiGestionali for BG0021");
                AltriDatiGestionaliType altriDatiGestionaliType = null;
                if (!charges.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-100");
                    altriDatiGestionaliType.setRiferimentoTesto(String.valueOf(charges.getBT0100DocumentLevelChargeBaseAmount(0).getValue()));
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0100 found");
                }

                if (!charges.getBT0101DocumentLevelChargePercentage().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-101");
                    altriDatiGestionaliType.setRiferimentoTesto(String.valueOf(charges.getBT0101DocumentLevelChargePercentage(0).getValue()));
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0101 found");
                }

                if (!charges.getBT0104DocumentLevelChargeReason().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-104");
                    altriDatiGestionaliType.setRiferimentoTesto(charges.getBT0104DocumentLevelChargeReason(0).getValue());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0104 found");
                }

                if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty()) {
                    altriDatiGestionaliType = new AltriDatiGestionaliType();
                    altriDatiGestionaliType.setTipoDato("BT-105");
                    altriDatiGestionaliType.setRiferimentoTesto(converted);
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionaliType);
                } else {
                    log.trace("No BT0105 found");
                }

                datiBeniServizi.getDettaglioLinee().add(dettaglioLinee);
            }
        } else {
            log.trace("No BG0021 found");
        }
    }

    private void createMissingLines(List<DettaglioLineeType> dettaglioLineeList, int missing) {
        log.debug("Found {} DettaglioLinee, {} missing", dettaglioLineeList.size(), missing);
        for (int i = 0; i < missing; i++) {
            DettaglioLineeType l = new DettaglioLineeType();
            int ln = dettaglioLineeList.size() + 1;
            l.setNumeroLinea(ln);
            dettaglioLineeList.add(l);
            log.trace("Set NumeroLinea with value {}", ln);
        }
    }
}