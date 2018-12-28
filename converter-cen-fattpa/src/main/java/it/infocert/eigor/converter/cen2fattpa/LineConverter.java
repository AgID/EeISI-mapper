package it.infocert.eigor.converter.cen2fattpa;


import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.BigDecimalToStringConverter;
import it.infocert.eigor.api.conversion.converter.CountryNameToIso31661CountryCodeConverter;
import it.infocert.eigor.api.conversion.converter.Iso31661CountryCodesToStringConverter;
import it.infocert.eigor.api.conversion.converter.Iso4217CurrenciesFundsCodesToStringConverter;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.LocalDateToXMLGregorianCalendarConverter;
import it.infocert.eigor.api.conversion.converter.StringToBigDecimalConverter;
import it.infocert.eigor.api.conversion.converter.StringToIso4217CurrenciesFundsCodesConverter;
import it.infocert.eigor.api.conversion.converter.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.conversion.converter.StringToStringConverter;
import it.infocert.eigor.api.conversion.converter.StringToUnitOfMeasureConverter;
import it.infocert.eigor.api.conversion.converter.StringToUntdid1001InvoiceTypeCodeConverter;
import it.infocert.eigor.api.conversion.converter.StringToUntdid5305DutyTaxFeeCategoriesConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.conversion.converter.UnitOfMeasureCodesToStringConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid1001InvoiceTypeCodeToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid2005DateTimePeriodQualifiersToItalianCodeConverter;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid4461PaymentMeansCodeToItalianCodeString;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.converters.Untdid7161SpecialServicesCodesToItalianCodeStringConverter;
import it.infocert.eigor.converter.cen2fattpa.models.AltriDatiGestionaliType;
import it.infocert.eigor.converter.cen2fattpa.models.CodiceArticoloType;
import it.infocert.eigor.converter.cen2fattpa.models.DatiBeniServiziType;
import it.infocert.eigor.converter.cen2fattpa.models.DatiDocumentiCorrelatiType;
import it.infocert.eigor.converter.cen2fattpa.models.DatiGeneraliType;
import it.infocert.eigor.converter.cen2fattpa.models.DettaglioLineeType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.converter.cen2fattpa.models.NaturaType;
import it.infocert.eigor.converter.cen2fattpa.models.TipoCessionePrestazioneType;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0013DeliveryInformation;
import it.infocert.eigor.model.core.model.BG0014InvoicingPeriod;
import it.infocert.eigor.model.core.model.BG0020DocumentLevelAllowances;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import it.infocert.eigor.model.core.model.BG0023VatBreakdown;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0026InvoiceLinePeriod;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;
import it.infocert.eigor.model.core.model.BG0028InvoiceLineCharges;
import it.infocert.eigor.model.core.model.BG0029PriceDetails;
import it.infocert.eigor.model.core.model.BG0030LineVatInformation;
import it.infocert.eigor.model.core.model.BG0031ItemInformation;
import it.infocert.eigor.model.core.model.BG0032ItemAttributes;
import it.infocert.eigor.model.core.model.BT0070DeliverToPartyName;
import it.infocert.eigor.model.core.model.BT0127InvoiceLineNote;
import it.infocert.eigor.model.core.model.BT0133InvoiceLineBuyerAccountingReference;
import it.infocert.eigor.model.core.model.BT0134InvoiceLinePeriodStartDate;
import it.infocert.eigor.model.core.model.BT0135InvoiceLinePeriodEndDate;
import it.infocert.eigor.model.core.model.BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class LineConverter implements CustomMapping<FatturaElettronicaType> {
    private final static Logger log = LoggerFactory.getLogger(LineConverter.class);
    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd"),
            StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),
            StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            StringToUntdid5305DutyTaxFeeCategoriesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid5305DutyTaxFeeCategories.class),
            StringToUnitOfMeasureConverter.newConverter(),
            LookUpEnumConversion.newConverter(UnitOfMeasureCodes.class),
            StringToBigDecimalConverter.newConverter(),
            StringToStringConverter.newConverter(),
            JavaLocalDateToStringConverter.newConverter(),
            Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
            Iso31661CountryCodesToStringConverter.newConverter(),
            BigDecimalToStringConverter.newConverter("#.00"),
            UnitOfMeasureCodesToStringConverter.newConverter(),
            Untdid1001InvoiceTypeCodeToItalianCodeStringConverter.newConverter(),
            Untdid4461PaymentMeansCodeToItalianCodeString.newConverter(),
            Untdid5189ChargeAllowanceDescriptionCodesToItalianCodeStringConverter.newConverter(),
            Untdid7161SpecialServicesCodesToItalianCodeStringConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToItalianCodeConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter.newConverter(),
            LocalDateToXMLGregorianCalendarConverter.newConverter()
    );


    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            final IllegalArgumentException e = new IllegalArgumentException("Too many FatturaElettronicaBody found in current FatturaElettronica");
            errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, ErrorCode.Action.HARDCODED_MAP, ErrorCode.Error.ILLEGAL_VALUE, Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "FatturaElettronicaBody")));
        } else if (size < 1) {
            final IllegalArgumentException e = new IllegalArgumentException("No FatturaElettronicaBody found in current FatturaElettronica");
            errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, ErrorCode.Action.HARDCODED_MAP, ErrorCode.Error.MISSING_VALUE, Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "FatturaElettronicaBody")));
        } else {
            FatturaElettronicaBodyType fatturaElettronicaBody = bodies.get(0);
            if (fatturaElettronicaBody.getDatiBeniServizi() == null) {
                fatturaElettronicaBody.setDatiBeniServizi(new DatiBeniServiziType());
            }

            mapBG20(invoice, fatturaElettronicaBody, errors, callingLocation);
            mapBG21(invoice, fatturaElettronicaBody, errors, callingLocation);
            mapBG25(invoice, fatturaElettronicaBody, errors, callingLocation);
            mapBG13(invoice, fatturaElettronicaBody, errors, callingLocation);

            mapLineChargesAllowances(invoice, fatturaElettronicaBody, errors, callingLocation);
            mapDocumentChargesAllowances(invoice, fatturaElettronicaBody, errors, callingLocation);

            mapBt73and74(invoice, fatturaElettronicaBody, errors, callingLocation);
        }
    }

    private void mapBG13(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0013DeliveryInformation().isEmpty()) {

            List<DettaglioLineeType> dettaglioLineeList = fatturaElettronicaBody.getDatiBeniServizi().getDettaglioLinee();
            if (dettaglioLineeList != null && !dettaglioLineeList.isEmpty()) {
                BG0013DeliveryInformation bg0013 = invoice.getBG0013DeliveryInformation(0);

                if (!bg0013.getBT0070DeliverToPartyName().isEmpty()) {
                    BT0070DeliverToPartyName bt0070 = bg0013.getBT0070DeliverToPartyName(0);
                    AltriDatiGestionaliType altriDatiGestionali = new AltriDatiGestionaliType();
                    altriDatiGestionali.setTipoDato("BT-70");
                    altriDatiGestionali.setRiferimentoTesto(bt0070.getValue());
                    for (DettaglioLineeType dettaglioLinee : dettaglioLineeList) {
                        dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);
                    }
                }

                if (!bg0013.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().isEmpty()) {
                    Identifier bt0071 = bg0013.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier(0).getValue();
                    AltriDatiGestionaliType altriDatiGestionali = new AltriDatiGestionaliType();
                    altriDatiGestionali.setTipoDato("BT-71");
                    altriDatiGestionali.setRiferimentoTesto(bt0071.getIdentifier());
                    for (DettaglioLineeType dettaglioLinee : dettaglioLineeList) {
                        dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);
                    }

                    if (bt0071.getIdentificationSchema() != null) {
                        AltriDatiGestionaliType datiGestionali = new AltriDatiGestionaliType();
                        datiGestionali.setTipoDato("BT-71-1");
                        datiGestionali.setRiferimentoTesto(bt0071.getIdentificationSchema());
                        for (DettaglioLineeType dettaglioLinee : dettaglioLineeList) {
                            dettaglioLinee.getAltriDatiGestionali().add(datiGestionali);
                        }
                    }
                }
            }
        }
    }

    private void mapBt73and74(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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

        List<BG0026InvoiceLinePeriod> notNullPeriodsStream = invoice.getBG0025InvoiceLine()
                .stream()
                .map(bg0025InvoiceLine -> {
                    List<BG0026InvoiceLinePeriod> periods = bg0025InvoiceLine.getBG0026InvoiceLinePeriod();
                    if (!periods.isEmpty()) {
                        return periods.get(0);
                    }
                    return null;
                })
                .filter(bg0026InvoiceLinePeriod -> bg0026InvoiceLinePeriod != null)
                .collect(Collectors.toList());

        int bt134Counter = notNullPeriodsStream.stream()
                .filter(bg0026InvoiceLinePeriod -> !bg0026InvoiceLinePeriod.getBT0134InvoiceLinePeriodStartDate().isEmpty())
                .map(bg0026InvoiceLinePeriod -> bg0026InvoiceLinePeriod.getBT0134InvoiceLinePeriodStartDate(0))
                .collect(Collectors.toList()).size();

        int bt135Counter = (int) notNullPeriodsStream.stream()
                .filter(period -> !period.getBT0135InvoiceLinePeriodEndDate().isEmpty())
                .map(period -> period.getBT0135InvoiceLinePeriodEndDate(0))
                .count();

        return bt134Counter > 0 && bt135Counter > 0;
    }


    private void mapBG20(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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
                    BigDecimal value = allowances.getBT0092DocumentLevelAllowanceAmount(0).getValue();
                    dettaglioLinee.setPrezzoUnitario(value.setScale(2, RoundingMode.HALF_UP));
                    dettaglioLinee.setPrezzoTotale(value.setScale(2, RoundingMode.HALF_UP));
                    log.trace("Set BT92 as PrezzoUnitario and PrezzoTotale with value {}", value);
                }

                /*if (!allowances.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                    AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                    String value = String.valueOf(allowances.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue());
                    dati.setTipoDato("BT-93");  //???
                    dati.setRiferimentoTesto(value);
                    log.trace("Set BT93 as RiferimentoTesto with value {}", value);
                    dettaglioLinee.getAltriDatiGestionali().add(dati);
                }*/

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
                    BigDecimal value = allowances.getBT0096DocumentLevelAllowanceVatRate(0).getValue();
                    dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP));
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

                    final Untdid5189ChargeAllowanceDescriptionCodes code = allowances.getBT0098DocumentLevelAllowanceReasonCode(0).getValue();
                    final String result = conversionRegistry.convert(Untdid5189ChargeAllowanceDescriptionCodes.class, String.class, code);
                    sb.append("BT-98=");
                    if (!"".equals(result)) {
                        log.debug("BT-98 mapped to Descrizione");
                        sb.append(result);
                    } else {
                        log.debug("BT-98 has empty string");
                    }
                } else {
                    log.trace("No BT0098 found");
                }

                String des = sb.toString();
                dettaglioLinee.setDescrizione(des);
                log.debug("Set \"{}\" as Descrizione", des);
            }
        }
    }

    private void mapBG21(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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
                    BigDecimal value = charges.getBT0099DocumentLevelChargeAmount(0).getValue();
                    dettaglioLinee.setPrezzoUnitario(value.setScale(8, RoundingMode.HALF_UP));
                    dettaglioLinee.setPrezzoTotale(value.setScale(8, RoundingMode.HALF_UP));
                    log.trace("Set BT99 as PrezzoUnitario and PrezzoTotale with value {}", value);
                }

               /* if (!charges.getBT0100DocumentLevelChargeBaseAmount().isEmpty()) {
                    AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                    BigDecimal value = charges.getBT0100DocumentLevelChargeBaseAmount(0).getValue();
                    dati.setTipoDato("BT-100"); //???
                    dati.setRiferimentoNumero(value.setScale(2, RoundingMode.HALF_UP));
                    dettaglioLinee.getAltriDatiGestionali().add(dati);
                    log.trace("Set BT100 as RiferimentoNumero with value {}", value);

                }*/

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
                    BigDecimal value = charges.getBT0103DocumentLevelChargeVatRate(0).getValue();
                    dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP));
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

                    sb.append(" ").append("SC");
                    log.trace("Appended BT105 to Descrizione");
                }

                dettaglioLinee.setDescrizione(sb.toString());
                log.trace("Set {} as Descrizione", sb.toString());
            }
        }
    }


    private void mapBG25(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0025InvoiceLine().isEmpty()) {
            final DatiBeniServiziType datiBeniServizi = fatturaElettronicaBody.getDatiBeniServizi();
            final DatiGeneraliType datiGenerali = Optional.fromNullable(fatturaElettronicaBody.getDatiGenerali()).or(new DatiGeneraliType());
            fatturaElettronicaBody.setDatiGenerali(datiGenerali);
            final List<DettaglioLineeType> dettaglioLineeList = datiBeniServizi.getDettaglioLinee();
            if (dettaglioLineeList.size() < invoice.getBG0025InvoiceLine().size()) {
                int n = invoice.getBG0025InvoiceLine().size() - dettaglioLineeList.size();
                createMissingLines(dettaglioLineeList, n);
            }
            for (int i = 0; i < invoice.getBG0025InvoiceLine().size(); i++) {
                DettaglioLineeType dettaglioLinee = dettaglioLineeList.get(i);
                BG0025InvoiceLine invoiceLine = invoice.getBG0025InvoiceLine(i);
                final Optional<String> lineIdentifier;
                if (!invoiceLine.getBT0126InvoiceLineIdentifier().isEmpty()) {
                    lineIdentifier = Optional.of(invoiceLine.getBT0126InvoiceLineIdentifier(0).getValue());
                } else {
                    lineIdentifier = Optional.absent();
                }

                for (BT0127InvoiceLineNote bt0127 : invoiceLine.getBT0127InvoiceLineNote()) {
                    AltriDatiGestionaliType altriDatiGestionali = new AltriDatiGestionaliType();
                    altriDatiGestionali.setTipoDato("BT-127");
                    altriDatiGestionali.setRiferimentoTesto(bt0127.getValue());
                    dettaglioLinee.getAltriDatiGestionali().add(altriDatiGestionali);
                    log.trace("Set BT127 as RiferimentoTesto with value {}", bt0127.getValue());
                }

                if (!invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().isEmpty()) {
                    CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                    final UnaryOperator<String> getOrDefault = s -> Strings.isNullOrEmpty(s)?"ZZZ":s;

                    Identifier bt0128 = invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(0).getValue();
                    codiceArticolo.setCodiceValore(getOrDefault.apply(bt0128.getIdentifier()));
                    codiceArticolo.setCodiceTipo(getOrDefault.apply(bt0128.getIdentificationSchema()));
                    dettaglioLinee.getCodiceArticolo().add(codiceArticolo);
                    log.trace("Set BT128 as CodiceArticolo with value {}", bt0128.getIdentifier());
                }

                BigDecimal quantity = invoiceLine.getBT0129InvoicedQuantity().isEmpty() ?
                        BigDecimal.ZERO : invoiceLine.getBT0129InvoicedQuantity(0).getValue();

                dettaglioLinee.setQuantita(quantity.setScale(8, RoundingMode.HALF_UP));
                log.trace("Set BT129 as Quantita with value {}", quantity);

                if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
                    BigDecimal value = invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue();
                    dettaglioLinee.setPrezzoTotale(value.setScale(2, RoundingMode.HALF_UP));
                    log.trace("Set BT131 as PrezzoTotale with value {}", value);
                    if (quantity.compareTo(BigDecimal.ZERO) != 0) {
                        dettaglioLinee.setPrezzoUnitario(value.divide(quantity, RoundingMode.HALF_UP));
                        //PrezzoUnitario is mandatory in FatturaPA
                    }
                }

                if (!invoiceLine.getBT0132ReferencedPurchaseOrderLineReference().isEmpty()) {
                    final String purchaseOrder = invoiceLine.getBT0132ReferencedPurchaseOrderLineReference(0).getValue();
                    final DatiDocumentiCorrelatiType dati = new DatiDocumentiCorrelatiType();
                    datiGenerali.getDatiOrdineAcquisto().add(dati);
                    dati.setNumItem(purchaseOrder);
                    if (lineIdentifier.isPresent()) {
                        Integer number;
                        try {
                            number = Integer.valueOf(lineIdentifier.get());
                        } catch (NumberFormatException e) {
                            number = dettaglioLinee.getNumeroLinea();
                        }
                        dati.getRiferimentoNumeroLinea().add(number);

                    } else {
                        dati.getRiferimentoNumeroLinea().add(dettaglioLinee.getNumeroLinea());
                    }

                    if (!invoice.getBT0013PurchaseOrderReference().isEmpty()) {
                        final String orderReference = invoice.getBT0013PurchaseOrderReference(0).getValue();
                        dati.setIdDocumento(orderReference);
                    }
                }

                if (!invoiceLine.getBT0133InvoiceLineBuyerAccountingReference().isEmpty()) {
                    BT0133InvoiceLineBuyerAccountingReference invoiceLineBuyerAccountingReference = invoiceLine.getBT0133InvoiceLineBuyerAccountingReference(0);
                    dettaglioLinee.setRiferimentoAmministrazione(invoiceLineBuyerAccountingReference.getValue());
                }

                if (!invoiceLine.getBG0026InvoiceLinePeriod().isEmpty()) {
                    BG0026InvoiceLinePeriod bg0026 = invoiceLine.getBG0026InvoiceLinePeriod(0);

                    if (!bg0026.getBT0134InvoiceLinePeriodStartDate().isEmpty()) {
                        BT0134InvoiceLinePeriodStartDate bt0134 = bg0026.getBT0134InvoiceLinePeriodStartDate(0);
                        try {
                            dettaglioLinee.setDataInizioPeriodo(conversionRegistry.convert(LocalDate.class, XMLGregorianCalendar.class, bt0134.getValue()));
                        } catch (RuntimeException e) {
                            log.error("Failed converting BT-134.");
                            errors.add(ConversionIssue.newError(
                                    e,
                                    e.getMessage(),
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.ILLEGAL_VALUE
                            ));
                        }
                    }

                    if (!bg0026.getBT0135InvoiceLinePeriodEndDate().isEmpty()) {
                        BT0135InvoiceLinePeriodEndDate bt0135 = bg0026.getBT0135InvoiceLinePeriodEndDate(0);
                        try {
                            dettaglioLinee.setDataFinePeriodo(conversionRegistry.convert(LocalDate.class, XMLGregorianCalendar.class, bt0135.getValue()));
                        } catch (RuntimeException e) {
                            log.error("Failed converting BT-135.");
                            errors.add(ConversionIssue.newError(
                                    e,
                                    e.getMessage(),
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.ILLEGAL_VALUE
                            ));
                        }
                    }
                }

                if (!invoiceLine.getBG0030LineVatInformation().isEmpty()) {
                    BG0030LineVatInformation lineVatInformation = invoiceLine.getBG0030LineVatInformation(0);
                    if (!lineVatInformation.getBT0152InvoicedItemVatRate().isEmpty()) {
                        BigDecimal value = lineVatInformation.getBT0152InvoicedItemVatRate(0).getValue();
                        dettaglioLinee.setAliquotaIVA(value.setScale(8, RoundingMode.HALF_UP));
                    } else {
                        if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                            BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                            if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                BigDecimal value = vatBreakdown.getBT0119VatCategoryRate(0).getValue();
                                dettaglioLinee.setAliquotaIVA(value.setScale(8, RoundingMode.HALF_UP)); //Even if BG25 doesn't have it, FatturaPA wants it
                            }
                        }
                    }
                }

                if (!invoiceLine.getBG0027InvoiceLineAllowances().isEmpty()) {
                    for (BG0027InvoiceLineAllowances invoiceLineAllowances : invoiceLine.getBG0027InvoiceLineAllowances()) {

                        final Optional<Identifier> baseAmountOpt = Optional.fromNullable(invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount().isEmpty() ? null : invoiceLineAllowances.getBT0137InvoiceLineAllowanceBaseAmount(0).getValue());
                        final Optional<Identifier> percentageOpt = Optional.fromNullable(invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage().isEmpty() ? null : invoiceLineAllowances.getBT0138InvoiceLineAllowancePercentage(0).getValue());

                        BigDecimal discountValue = BigDecimal.ZERO;
                        BigDecimal allowanceAmount = invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().isEmpty() ? BigDecimal.ZERO : invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                        BigDecimal baseAmount = baseAmountOpt.isPresent() ? new BigDecimal(baseAmountOpt.get().getIdentifier()) : BigDecimal.ZERO;
                        BigDecimal percentage = percentageOpt.isPresent() ? new BigDecimal(percentageOpt.get().getIdentifier()) : BigDecimal.ZERO;
                        String reason = invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason().isEmpty() ? "Sconto Linea" : invoiceLineAllowances.getBT0139InvoiceLineAllowanceReason(0).getValue();

                        String code = invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode().isEmpty() ? "" : String.format(" BT-0140: %d", invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode(0).getValue().getCode());

                        if (allowanceAmount.compareTo(BigDecimal.ZERO) > 0) {
                            discountValue = allowanceAmount.negate();
                        } else if (baseAmount.compareTo(BigDecimal.ZERO) != 0 && percentage.compareTo(BigDecimal.ZERO) != 0) {
                            discountValue = baseAmount.multiply(percentage.negate());
                        }

                        if (discountValue.compareTo(BigDecimal.ZERO) < 0) {
                            String desc = String.format("%s%s", reason, code);
                            dettaglioLinee.setDescrizione(desc);
                            log.trace("Set Descrizione with value {}", desc);
                            BigDecimal quantityBd = quantity.setScale(8, RoundingMode.HALF_UP);
                            dettaglioLinee.setQuantita(quantityBd);
                            log.trace("Set Quantita with value {}", quantityBd);
                            BigDecimal unit = discountValue.setScale(2, RoundingMode.HALF_UP);
                            dettaglioLinee.setPrezzoUnitario(unit);
                            log.trace("Set PrezzoUnitario with value {}", unit);
                            BigDecimal tot = allowanceAmount.setScale(2, RoundingMode.HALF_UP);
                            dettaglioLinee.setPrezzoTotale(tot);
                            log.trace("Set PrezzoTotale with value {}", tot);

//                            ScontoMaggiorazioneType scontoMaggiorazione = new ScontoMaggiorazioneType();
//                            scontoMaggiorazione.setTipo(TipoScontoMaggiorazioneType.SC);
//                            dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione);
                        }

                        List<Identifier> data = new ArrayList<>();
                        if (baseAmountOpt.isPresent()) data.add(baseAmountOpt.get());
                        if (percentageOpt.isPresent()) data.add(percentageOpt.get());

                        for (Identifier id : data) {
                            final AltriDatiGestionaliType datum = new AltriDatiGestionaliType();
                            datum.setTipoDato(id.getSchemaVersion());
                            datum.setTipoDato(id.getIdentifier());
                            dettaglioLinee.getAltriDatiGestionali().add(datum);
                        }

                    }
                }

                if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                    BigDecimal surchargeValue = BigDecimal.ZERO;
                    BG0028InvoiceLineCharges invoiceLineCharges = invoiceLine.getBG0028InvoiceLineCharges(0);
                    BigDecimal chargeAmount = invoiceLineCharges.getBT0141InvoiceLineChargeAmount().isEmpty() ? BigDecimal.ZERO : invoiceLineCharges.getBT0141InvoiceLineChargeAmount(0).getValue();
                    BigDecimal baseAmount = invoiceLineCharges.getBT0142InvoiceLineChargeBaseAmount().isEmpty() ? BigDecimal.ZERO : invoiceLineCharges.getBT0142InvoiceLineChargeBaseAmount(0).getValue();
                    BigDecimal percentage = invoiceLineCharges.getBT0143InvoiceLineChargePercentage().isEmpty() ? BigDecimal.ZERO : invoiceLineCharges.getBT0143InvoiceLineChargePercentage(0).getValue();

                    if (chargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                        surchargeValue = chargeAmount;
                    } else if (baseAmount.compareTo(BigDecimal.ZERO) != 0 && percentage.compareTo(BigDecimal.ZERO) != 0) {
                        surchargeValue = baseAmount.multiply(percentage);
                    }

//                    ScontoMaggiorazioneType scontoMaggiorazione1 = new ScontoMaggiorazioneType();
//                    scontoMaggiorazione1.setTipo(TipoScontoMaggiorazioneType.MG);
//                    dettaglioLinee.getScontoMaggiorazione().add(scontoMaggiorazione1);
                    String bt0144 = invoiceLineCharges.getBT0144InvoiceLineChargeReason().isEmpty() ? "Maggiorazione Linea" : invoiceLineCharges.getBT0144InvoiceLineChargeReason(0).getValue();
                    String bt0145 = invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().isEmpty() ? null : invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode(0).getValue().name();
                    if (surchargeValue.compareTo(BigDecimal.ZERO) > 0) {
                        if (bt0145 != null) {
                            dettaglioLinee.setDescrizione(String.format("%s BT-0145: %s", bt0144, bt0145));
                        } else {
                            dettaglioLinee.setDescrizione(bt0144);
                        }
                        dettaglioLinee.setQuantita(quantity.setScale(8, RoundingMode.HALF_UP));
                        dettaglioLinee.setPrezzoUnitario(surchargeValue.setScale(2, RoundingMode.HALF_UP));
                        dettaglioLinee.setPrezzoTotale(chargeAmount.multiply(quantity));
                        if (!invoiceLine.getBG0030LineVatInformation().isEmpty()) {
                            BG0030LineVatInformation lineVatInformation = invoiceLine.getBG0030LineVatInformation(0);
                            if (!lineVatInformation.getBT0152InvoicedItemVatRate().isEmpty()) {
                                BigDecimal value = lineVatInformation.getBT0152InvoicedItemVatRate(0).getValue();
                                dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP));
                            } else {
                                if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                                    BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                                    if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                        BigDecimal value = vatBreakdown.getBT0119VatCategoryRate(0).getValue();
                                        dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP)); //Even if BG25 doesn't have it, FatturaPA wants it
                                    }
                                }
                            }
                        }
                    }
                }


                if (!invoiceLine.getBG0029PriceDetails().isEmpty()) {
                    BG0029PriceDetails priceDetails = invoiceLine.getBG0029PriceDetails(0);

                    BigDecimal itemNetPrice = priceDetails.getBT0146ItemNetPrice().isEmpty() ? BigDecimal.ZERO : priceDetails.getBT0146ItemNetPrice(0).getValue();
                    String quantityUnitOfMeasureCode = invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().isEmpty() ? "" : invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode(0).getValue().getCommonCode();
                    BigDecimal baseQuantity = priceDetails.getBT0149ItemPriceBaseQuantity().isEmpty() ?
                            BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP) :
                            priceDetails.getBT0149ItemPriceBaseQuantity(0).getValue().setScale(2, RoundingMode.HALF_UP);
                    String baseQuantityUnitOfMeasureCode = priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().isEmpty() ? null : priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode(0).getValue().getCommonCode();

                    try {
                        dettaglioLinee.setQuantita(quantity.divide(baseQuantity, RoundingMode.HALF_UP).setScale(8, RoundingMode.HALF_UP));
                    } catch (NumberFormatException | ArithmeticException e) {
                        ArrayList<String> zeroes = Lists.newArrayList();
                        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
                            zeroes.add("BT0129");
                        }

                        if (baseQuantity.compareTo(BigDecimal.ZERO) == 0) {
                            zeroes.add("BT0149");
                        }
                        final String message = String.format("These values cannot be 0: %s", zeroes.toString());
                        errors.add(ConversionIssue.newError(
                                e,
                                message,
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, zeroes.toString())
                        ));
                    }


                    // FIXME PrezzoTotale is defined as (BT-146*BT-129/BT-149)-SUM(BT-136)+SUM(BT-141)

//                    final BigDecimal prezzoTotale = itemNetPrice.multiply(
//                            quantity.divide(baseQuantity, RoundingMode.HALF_UP))
//                            .subtract(getSumOfAllowancesForLine(invoiceLine)).add(getSumOfChargesForLine(invoiceLine));

                    // FIXME  but only matches provided excel calculation like so

                    final BigDecimal prezzoTotale = itemNetPrice.multiply(quantity.divide(baseQuantity, RoundingMode.HALF_UP));

                    dettaglioLinee.setPrezzoTotale(prezzoTotale.setScale(8, RoundingMode.HALF_UP));


//                    if (!invoice.getBG0023VatBreakdown().isEmpty()) {
//                        BG0023VatBreakdown bg0023VatBreakdown = invoice.getBG0023VatBreakdown(1);
//                        if (!bg0023VatBreakdown.getBT0116VatCategoryTaxableAmount().isEmpty()) {
//                            BigDecimal bt0116 = bg0023VatBreakdown.getBT0116VatCategoryTaxableAmount(0).getValue();
//                            BigDecimal total = itemNetPrice.multiply(
//                                    quantity.divide(baseQuantity, RoundingMode.HALF_UP))
//                                    .subtract(getSumOfAllowancesForLine(invoiceLine)).add(getSumOfChargesForLine(invoiceLine));
//
//                            BigDecimal roundingDifference = bt0116.subtract(total);
//                        }
//                    }

//                    if (!invoiceLine.getBT0131InvoiceLineNetAmount().isEmpty()) {
//                        final BigDecimal value = invoiceLine.getBT0131InvoiceLineNetAmount(0).getValue();
//                        dettaglioLinee.setPrezzoTotale(value.setScale(2, RoundingMode.HALF_UP));
//                    }

                    if (!priceDetails.getBT0147ItemPriceDiscount().isEmpty()) {
                        final BigDecimal itemPriceDiscount = priceDetails.getBT0147ItemPriceDiscount(0).getValue();
                        final AltriDatiGestionaliType dati = new AltriDatiGestionaliType();
                        dati.setTipoDato("BT-147");
                        dati.setRiferimentoNumero(itemPriceDiscount.setScale(8, RoundingMode.HALF_UP));
                        dettaglioLinee.getAltriDatiGestionali().add(dati);
                    }

                    dettaglioLinee.setPrezzoUnitario(itemNetPrice.setScale(8, RoundingMode.HALF_UP));
                    if (baseQuantity.compareTo(BigDecimal.ZERO) == 0) {
                        dettaglioLinee.setUnitaMisura(quantityUnitOfMeasureCode);
                    } else {
                        dettaglioLinee.setUnitaMisura(baseQuantity.toString() + " " + quantityUnitOfMeasureCode);
                    }

                    if (baseQuantity.compareTo(BigDecimal.ONE) > 0) {
                        AltriDatiGestionaliType altriDatiGestionaliQty = new AltriDatiGestionaliType();
                        altriDatiGestionaliQty.setTipoDato(IConstants.ITEM_BASE_QTY);
                        altriDatiGestionaliQty.setRiferimentoNumero(baseQuantity);
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

            if (!itemInformation.getBT0154ItemDescription().isEmpty()) {
                String bt154 = itemInformation.getBT0154ItemDescription(0).getValue();
                String currentDesc = dettaglioLinee.getDescrizione();
                if (currentDesc == null || currentDesc.isEmpty()) {
                    dettaglioLinee.setDescrizione(bt154);
                } else {
                    dettaglioLinee.setDescrizione(String.format("%s %s", currentDesc, bt154));
                }
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
                Identifier bt0157 = itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier(0).getValue();
                codiceArticolo.setCodiceValore(bt0157.getIdentifier());
                codiceArticolo.setCodiceTipo(bt0157.getIdentificationSchema());
                dettaglioLinee.getCodiceArticolo().add(codiceArticolo);

            }

            if (!itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().isEmpty()) {
                for (BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier identifier : itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier()) {
                    Identifier bt158 = identifier.getValue();
                    CodiceArticoloType codiceArticolo = new CodiceArticoloType();
                    codiceArticolo.setCodiceValore(bt158.getIdentifier());

                    String bt158_1 = bt158.getIdentificationSchema();
                    String bt158_2 = bt158.getSchemaVersion();

                    if (bt158_1 != null && bt158_2 != null) {
                        codiceArticolo.setCodiceTipo(String.format("%s %s", bt158_1, bt158_2));
                    } else if (bt158_1 != null) {
                        codiceArticolo.setCodiceTipo(bt158_1);
                    } else if (bt158_2 != null) {
                        codiceArticolo.setCodiceTipo(bt158_2);
                    }
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
    private void mapLineChargesAllowances(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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

                BigDecimal vatLine = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
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
                        BigDecimal value = lineVatInformation.getBT0152InvoicedItemVatRate(0).getValue();
                        dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP));
                        vatLine = value;
                    } else {
                        if (!invoice.getBG0023VatBreakdown().isEmpty()) {
                            BG0023VatBreakdown vatBreakdown = invoice.getBG0023VatBreakdown(0);

                            if (!vatBreakdown.getBT0119VatCategoryRate().isEmpty()) {
                                BigDecimal value = vatBreakdown.getBT0119VatCategoryRate(0).getValue();
                                dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP)); //Even if BG25 doesn't have it, FatturaPA wants it
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
                            if (invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode().isEmpty()) {
                                lineaSconto.setDescrizione(descrizione);
                            } else {
                                Untdid5189ChargeAllowanceDescriptionCodes code = invoiceLineAllowances.getBT0140InvoiceLineAllowanceReasonCode(0).getValue();
                                lineaSconto.setDescrizione(String.format("%s BT-0140: %d", descrizione, code.getCode()));
                            }
                        }
                        BigDecimal allowanceAmount = invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().isEmpty() ?
                                BigDecimal.ZERO : invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount(0).getValue();
                        allowanceAmount = allowanceAmount.negate(); //allowanceAmount *= -1.0;
                        BigDecimal prezzo = allowanceAmount.setScale(2, RoundingMode.HALF_UP);
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

                        datiBeniServizi.getDettaglioLinee().add(lineaSconto);
                    }
                }
                //CHARGES
                if (!invoiceLine.getBG0028InvoiceLineCharges().isEmpty()) {
                    for (BG0028InvoiceLineCharges invoiceLineCharges : invoiceLine.getBG0028InvoiceLineCharges()) {
                        DettaglioLineeType lineaMaggiorazione = new DettaglioLineeType();
                        lineaMaggiorazione.setNumeroLinea(dettaglioLinee.getNumeroLinea());
                        lineaMaggiorazione.setTipoCessionePrestazione(TipoCessionePrestazioneType.SC);
                        String descrizione = invoiceLineCharges.getBT0144InvoiceLineChargeReason().isEmpty() ? "Maggiorazione linea" : invoiceLineCharges.getBT0144InvoiceLineChargeReason(0).getValue();
                        if (invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode().isEmpty()) {
                            lineaMaggiorazione.setDescrizione(descrizione);
                        } else {
                            Untdid7161SpecialServicesCodes code = invoiceLineCharges.getBT0145InvoiceLineChargeReasonCode(0).getValue();
                            lineaMaggiorazione.setDescrizione(String.format("%s BT-0145: %s", descrizione, code.name()));
                        }
                        BigDecimal chargeAmount = invoiceLineCharges.getBT0141InvoiceLineChargeAmount().isEmpty() ? BigDecimal.ZERO : invoiceLineCharges.getBT0141InvoiceLineChargeAmount(0).getValue();
                        BigDecimal prezzo = chargeAmount.setScale(2, RoundingMode.HALF_UP);
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
                        datiBeniServizi.getDettaglioLinee().add(lineaMaggiorazione);
                    }
                }
            }
        }
    }

    private void mapDocumentChargesAllowances(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
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
                String reason;
                String converted;
                if (!allowances.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                    Untdid5189ChargeAllowanceDescriptionCodes code = allowances.getBT0098DocumentLevelAllowanceReasonCode(0).getValue();
                    try {
                        converted = conversionRegistry.convert(Untdid5189ChargeAllowanceDescriptionCodes.class, String.class, code);
                        if (!converted.trim().isEmpty()) {
                            dettaglioLinee.setRiferimentoAmministrazione(converted);
                        }
                    } catch (EigorRuntimeException | IllegalArgumentException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, code.toString())
                        ));
                    }
                } else {
                    log.trace("No BT0098 found");
                }

                if (!allowances.getBT0097DocumentLevelAllowanceReason().isEmpty()) {
                    final StringBuilder sb = new StringBuilder();
                    reason = allowances.getBT0097DocumentLevelAllowanceReason(0).getValue();
                    sb.append(reason);

                    if (!allowances.getBT0098DocumentLevelAllowanceReasonCode().isEmpty()) {
                        sb.append(" BT-98=");
                        final Untdid5189ChargeAllowanceDescriptionCodes code = allowances.getBT0098DocumentLevelAllowanceReasonCode(0).getValue();
                        log.error("BT-98: {}", code);
                        try {
                            final String convertedValue = conversionRegistry.convert(Untdid5189ChargeAllowanceDescriptionCodes.class, String.class, code);
                            if (!"".equals(convertedValue)) {
                                sb.append(convertedValue);
                            } else {
                                sb.append(code.getCode());
                            }
                        } catch (IllegalArgumentException e) {
                            log.error("Failed converting BT-98.");
                            errors.add(ConversionIssue.newError(
                                    e,
                                    e.getMessage(),
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.ILLEGAL_VALUE,
                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, code.toString())
                            ));
                        }

                    } else {
                        log.debug("No BT0098 found");
                    }

                    sb.append(" - ");
                    sb.append("Base Amount: ");
                    if (!allowances.getBT0093DocumentLevelAllowanceBaseAmount().isEmpty()) {
                        final String ba = String.valueOf(allowances.getBT0093DocumentLevelAllowanceBaseAmount(0).getValue());
                        sb.append(ba);
                    } else {
                        sb.append("N/A");
                        log.trace("No BT0093 found");
                    }

                    sb.append(" Percentage: ");
                    if (!allowances.getBT0094DocumentLevelAllowancePercentage().isEmpty()) {
                        final String p = String.valueOf(allowances.getBT0094DocumentLevelAllowancePercentage(0).getValue());
                        sb.append(p).append("%");
                    } else {
                        sb.append("N/A");
                        log.trace("No BT0094 found");
                    }
                    final String desc = sb.toString();
                    log.debug("Set {} as Descrizione", desc);
                    dettaglioLinee.setDescrizione(desc);
                } else {
                    log.trace("No BT0097 found");
                }

                BigDecimal quantitaCedute = BigDecimal.ONE.setScale(8, RoundingMode.HALF_UP);
                dettaglioLinee.setQuantita(quantitaCedute);
                if (!allowances.getBT0092DocumentLevelAllowanceAmount().isEmpty()) {

                    BigDecimal allowanceAmount = allowances.getBT0092DocumentLevelAllowanceAmount(0).getValue();
                    BigDecimal value = allowanceAmount.negate();

                    dettaglioLinee.setPrezzoUnitario(value.setScale(2, RoundingMode.HALF_UP));
                    dettaglioLinee.setPrezzoTotale(value.setScale(2, RoundingMode.HALF_UP));
                } else {
                    log.trace("No BT0092 found");
                }

                if (!allowances.getBT0096DocumentLevelAllowanceVatRate().isEmpty()) {
                    BigDecimal value = allowances.getBT0096DocumentLevelAllowanceVatRate(0).getValue();
                    dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP));
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
                    TypeConverter<Untdid7161SpecialServicesCodes, String> converter = Untdid7161SpecialServicesCodesToItalianCodeStringConverter.newConverter();
                    try {
                        converted = converter.convert(code);
                        dettaglioLinee.setRiferimentoAmministrazione(converted);
                    } catch (EigorRuntimeException | ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, code.toString())
                        ));
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
                } else if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty() && !"".equals(converted)) {
                    dettaglioLinee.setRiferimentoAmministrazione(converted);
                } else {
                    log.trace("No BT0104 found");
                }

                BigDecimal quantitaCedute = BigDecimal.ONE.setScale(8, RoundingMode.HALF_UP);
                dettaglioLinee.setQuantita(quantitaCedute);
                if (!charges.getBT0099DocumentLevelChargeAmount().isEmpty()) {
                    BigDecimal value = charges.getBT0099DocumentLevelChargeAmount(0).getValue();
                    dettaglioLinee.setPrezzoUnitario(value.setScale(2, RoundingMode.HALF_UP));
                    dettaglioLinee.setPrezzoTotale(value.setScale(2, RoundingMode.HALF_UP));
                } else {
                    log.trace("No BT0099 found");
                }

                if (!charges.getBT0103DocumentLevelChargeVatRate().isEmpty()) {
                    BigDecimal value = charges.getBT0103DocumentLevelChargeVatRate(0).getValue();
                    dettaglioLinee.setAliquotaIVA(value.setScale(2, RoundingMode.HALF_UP));
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
                AltriDatiGestionaliType altriDatiGestionaliType;
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

                if (!charges.getBT0105DocumentLevelChargeReasonCode().isEmpty() && !"".equals(converted)) {
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

    private BigDecimal getSumOfAllowancesForLine(BG0025InvoiceLine invoiceLine) {
        BigDecimal allowancesSum = BigDecimal.ZERO;

        for (BG0027InvoiceLineAllowances invoiceLineAllowances : invoiceLine.getBG0027InvoiceLineAllowances()) {
            BigDecimal allowanceAmount = invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount().isEmpty() ? BigDecimal.ZERO : invoiceLineAllowances.getBT0136InvoiceLineAllowanceAmount(0).getValue();
            allowancesSum = allowancesSum.add(allowanceAmount);
        }

        return allowancesSum;
    }

    private BigDecimal getSumOfChargesForLine(BG0025InvoiceLine invoiceLine) {
        BigDecimal chargesSum = BigDecimal.ZERO;

        for (BG0028InvoiceLineCharges invoiceLineCharges : invoiceLine.getBG0028InvoiceLineCharges()) {
            BigDecimal chargeAmount = invoiceLineCharges.getBT0141InvoiceLineChargeAmount().isEmpty() ? BigDecimal.ZERO : invoiceLineCharges.getBT0141InvoiceLineChargeAmount(0).getValue();
            chargesSum = chargesSum.add(chargeAmount);
        }

        return chargesSum;
    }
}
