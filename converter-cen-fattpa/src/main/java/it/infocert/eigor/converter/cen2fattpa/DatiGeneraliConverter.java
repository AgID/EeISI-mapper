package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.Iso31661CountryCodesToStringConverter;
import it.infocert.eigor.api.conversion.converter.LocalDateToXMLGregorianCalendarConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class DatiGeneraliConverter implements CustomMapping<FatturaElettronicaType> {
    private static final Logger log = LoggerFactory.getLogger(DatiGeneraliConverter.class);

    private final TypeConverter<LocalDate, XMLGregorianCalendar> dateConverter = LocalDateToXMLGregorianCalendarConverter.newConverter();
    private final TypeConverter<Iso31661CountryCodes, String> countryCodesConverter = Iso31661CountryCodesToStringConverter.newConverter();
    private final AttachmentUtil attachmentUtil;

    public DatiGeneraliConverter() {
        attachmentUtil = new AttachmentUtil();
    }

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            final String message = "Too many FatturaElettronicaBody found in current FatturaElettronica";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.ILLEGAL_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "FatturaElettronicaBody")
            )));
        } else if (size < 1) {
            final String message = "No FatturaElettronicaBody found in current FatturaElettronica";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.MISSING_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "FatturaElettronicaBody")
            )));
        } else {
            FatturaElettronicaBodyType body = bodies.get(0);
            DatiGeneraliType datiGenerali = body.getDatiGenerali();
            addDDT(invoice, datiGenerali, errors, callingLocation);
            addCausale(invoice, datiGenerali, errors, callingLocation);
            addFattureCollegate(invoice, datiGenerali, errors, callingLocation);
            addIndirizzo(invoice, fatturaElettronica, errors);
            addDatiTrasporto(invoice, body, datiGenerali, errors, callingLocation);
            addRiferimentoNormativo(invoice, body, errors, callingLocation);
            addArrotondamento(invoice, body, errors, callingLocation);
            addDettaglioPagamento(invoice, body, errors, callingLocation);
        }
    }

    private void addDettaglioPagamento(BG0000Invoice invoice, FatturaElettronicaBodyType body, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0016PaymentInstructions().isEmpty()) {
            BG0016PaymentInstructions bg0016 = invoice.getBG0016PaymentInstructions(0);

            if (!bg0016.getBG0017CreditTransfer().isEmpty()) {

                for (BG0017CreditTransfer bg0017 : bg0016.getBG0017CreditTransfer()) {

                    if (!bg0017.getBT0085PaymentAccountName().isEmpty()) {


                        String bt0086Value = evalExpression( () -> bg0017.getBT0086PaymentServiceProviderIdentifier(0).getValue() );
                        String bt0085Value = evalExpression( () -> bg0017.getBT0085PaymentAccountName(0).getValue() );
                        String bt0084Value = evalExpression( () -> bg0017.getBT0084PaymentAccountIdentifier(0).getValue() );

                        DatiPagamentoType datiPagamento = null;
                        if (!body.getDatiPagamento().isEmpty()) {
                            datiPagamento = body.getDatiPagamento().get(0);
                        }else {
                            final String message = "No DatiPagamento was found in current FatturaElettronicaBody";
                            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                    message,
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.MISSING_VALUE,
                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DatiPagamento")
                            )));
                            return;
                        }

//                        DatiPagamentoType datiPagamento = new DatiPagamentoType();
//                        body.getDatiPagamento().add(datiPagamento);

                        DettaglioPagamentoType dettaglioPagamento = new DettaglioPagamentoType();
                        datiPagamento.getDettaglioPagamento().add(dettaglioPagamento);

                        String beneficiario = dettaglioPagamento.getBeneficiario();
                        dettaglioPagamento.setBeneficiario(String.format("%s %s", beneficiario, bt0085Value));

                        dettaglioPagamento.setModalitaPagamento(ModalitaPagamentoType.MP_02);
                        dettaglioPagamento.setDataRiferimentoTerminiPagamento(null);
                        dettaglioPagamento.setGiorniTerminiPagamento(null);
                        dettaglioPagamento.setDataScadenzaPagamento(null);

                        BigDecimal importoPagamento = evalExpression(() -> invoice.getBG0022DocumentTotals().get(0).getBT0115AmountDueForPayment().get(0).getValue());
                        dettaglioPagamento.setImportoPagamento(importoPagamento);

                        if(bt0086Value!=null) dettaglioPagamento.setBIC(bt0086Value);
                        if(bt0084Value!=null) dettaglioPagamento.setIBAN(bt0084Value);


//                        if (datiPagamento.getDettaglioPagamento().isEmpty()) {
//                            final String message = "No DettaglioPagamento was found in current FatturaElettronicaBody";
//                            errors.add(ConversionIssue.newError(new EigorRuntimeException(
//                                    message,
//                                    callingLocation,
//                                    ErrorCode.Action.HARDCODED_MAP,
//                                    ErrorCode.Error.MISSING_VALUE,
//                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
//                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DettaglioPagamento")
//                            )));
//                        } else {
//
//                        }

//                        if (!body.getDatiPagamento().isEmpty()) {
//                            DatiPagamentoType datiPagamento = body.getDatiPagamento().get(0);
//                            if (!datiPagamento.getDettaglioPagamento().isEmpty()) {
//                                DettaglioPagamentoType dettaglioPagamento = datiPagamento.getDettaglioPagamento().get(0);
//
//                                String beneficiario = dettaglioPagamento.getBeneficiario();
//                                if (beneficiario == null || beneficiario.trim().isEmpty()) {
//                                    dettaglioPagamento.setBeneficiario(bt0085Value);
//                                } else {
//                                    dettaglioPagamento.setBeneficiario(String.format("%s %s", beneficiario, bt0085Value));
//                                }
//                            } else {
//                                final String message = "No DettaglioPagamento was found in current FatturaElettronicaBody";
//                                errors.add(ConversionIssue.newError(new EigorRuntimeException(
//                                        message,
//                                        callingLocation,
//                                        ErrorCode.Action.HARDCODED_MAP,
//                                        ErrorCode.Error.MISSING_VALUE,
//                                        Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
//                                        Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DettaglioPagamento")
//                                )));
//                            }
//                        } else {
//                            final String message = "No DatiPagamento was found in current FatturaElettronicaBody";
//                            errors.add(ConversionIssue.newError(new EigorRuntimeException(
//                                    message,
//                                    callingLocation,
//                                    ErrorCode.Action.HARDCODED_MAP,
//                                    ErrorCode.Error.MISSING_VALUE,
//                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
//                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DatiPagamento")
//                            )));
//                        }
                    }

                }




            }
        }
    }

    private void addDDT(BG0000Invoice invoice, DatiGeneraliType datiGenerali, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        try {
            if (datiGenerali != null) {
                if (!invoice.getBT0016DespatchAdviceReference().isEmpty()) {
                    final BT0016DespatchAdviceReference adviceReference = invoice.getBT0016DespatchAdviceReference(0);
                    final DatiDDTType datiDDT = new DatiDDTType();
                    datiGenerali.getDatiDDT().add(datiDDT);
                    datiDDT.setNumeroDDT(adviceReference.getValue());
                    DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                    final Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
                    final GregorianCalendar gc = new GregorianCalendar();
                    gc.setTimeInMillis(parsed.getTime());
                    datiDDT.setDataDDT(datatypeFactory.newXMLGregorianCalendar(gc));
                }
            }
        } catch (DatatypeConfigurationException | ParseException e) {
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    e.getMessage(),
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.INVALID,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
            )));
        }
    }


    private void addCausale(BG0000Invoice invoice, DatiGeneraliType datiGenerali, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (datiGenerali != null) {
            DatiGeneraliDocumentoType datiGeneraliDocumento = datiGenerali.getDatiGeneraliDocumento();
            if (datiGeneraliDocumento != null) {
                if (!invoice.getBG0001InvoiceNote().isEmpty()) {
                    for (BG0001InvoiceNote invoiceNote : invoice.getBG0001InvoiceNote()) {
                        final StringBuilder sb = new StringBuilder();
                        if (!invoiceNote.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                            BT0021InvoiceNoteSubjectCode invoiceNoteSubjectCode = invoiceNote.getBT0021InvoiceNoteSubjectCode(0);
                            String note = invoiceNoteSubjectCode.getValue();
                            sb.append(note).append(" ");
                            log.info("Mapping Causale from BT-21 and BT-22 with value: '{}'.", note);
                        }
                        if (!invoiceNote.getBT0022InvoiceNote().isEmpty()) {
                            String note = invoiceNote.getBT0022InvoiceNote(0).getValue();
                            log.info("Mapping Causale from BT-22 with value: '{}'.", note);
                            sb.append(note);
                        }
                        final String note = sb.toString();
                        if (!"".equalsIgnoreCase(note) && !" ".equalsIgnoreCase(note)) {
                            manageNoteText(datiGeneraliDocumento, note);
                        }
                    }
                }
                if (!invoice.getBG0016PaymentInstructions().isEmpty()) {
                    BG0016PaymentInstructions paymentInstructions = invoice.getBG0016PaymentInstructions(0);
                    if (!paymentInstructions.getBT0082PaymentMeansText().isEmpty()) {
                        BT0082PaymentMeansText paymentMeansText = paymentInstructions.getBT0082PaymentMeansText(0);
                        datiGeneraliDocumento.getCausale().add(paymentMeansText.getValue());
                    }
                }
            } else {
                final String message = "No DatiGeneraliDocumento was found in current DatiGenerali";
                errors.add(ConversionIssue.newError(new EigorRuntimeException(
                        message,
                        callingLocation,
                        ErrorCode.Action.HARDCODED_MAP,
                        ErrorCode.Error.MISSING_VALUE,
                        Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                        Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DatiGeneraliDocumento"))));
            }
        } else {
            final String message = "No DatiGenerali was found in current FatturaElettronicaBody";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.MISSING_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DatiGenerali")
            )));
        }

    }

    private void addFattureCollegate(BG0000Invoice invoice, DatiGeneraliType datiGenerali, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0003PrecedingInvoiceReference().isEmpty()) {
            List<DatiDocumentiCorrelatiType> datiFattureCollegate = datiGenerali.getDatiFattureCollegate();
            for (BG0003PrecedingInvoiceReference precedingInvoiceReference : invoice.getBG0003PrecedingInvoiceReference()) {
                DatiDocumentiCorrelatiType fatturaCollegata = new DatiDocumentiCorrelatiType();
                datiFattureCollegate.add(fatturaCollegata);

                if (!precedingInvoiceReference.getBT0025PrecedingInvoiceReference().isEmpty()) {
                    BT0025PrecedingInvoiceReference invoiceReference = precedingInvoiceReference.getBT0025PrecedingInvoiceReference(0);
                    fatturaCollegata.setIdDocumento(invoiceReference.getValue());
                }

                if (!precedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                    final LocalDate value = precedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate(0).getValue();
                    try {
                        XMLGregorianCalendar precedingInvoiceIssueDate = dateConverter.convert(value);
                        fatturaCollegata.setData(precedingInvoiceIssueDate);
                    } catch (EigorRuntimeException | ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
                    }
                }
            }
        }

    }

    private void addIndirizzo(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        final CessionarioCommittenteType cessionarioCommittente = fatturaElettronica.getFatturaElettronicaHeader().getCessionarioCommittente();
        final List<BG0013DeliveryInformation> deliveryInformations = invoice.getBG0013DeliveryInformation();
        if (!deliveryInformations.isEmpty()) {
            final List<BG0015DeliverToAddress> addresses = deliveryInformations.get(0).getBG0015DeliverToAddress();
            if (!addresses.isEmpty()) {
                final BG0015DeliverToAddress address = addresses.get(0);

                final List<BT0080DeliverToCountryCode> countryCodes = address.getBT0080DeliverToCountryCode();
                if (!countryCodes.isEmpty() && !Iso31661CountryCodes.IT.equals(countryCodes.get(0).getValue())) {
                    log.debug("Non-italian address, applying custom mapping");
                    final List<BT0078DeliverToPostCode> postCodes = address.getBT0078DeliverToPostCode();

                    final List<BT0075DeliverToAddressLine1> addressLines1 = address.getBT0075DeliverToAddressLine1();
                    final List<BT0076DeliverToAddressLine2> addressLines2 = address.getBT0076DeliverToAddressLine2();
                    final List<BT0165DeliverToAddressLine3> addressLines3 = address.getBT0165DeliverToAddressLine3();
                    final StringBuilder sb = new StringBuilder();
                    String addressLine1Value = "";
                    String addressLine2Value = "";
                    String addressLine3Value = "";
                    if (!addressLines1.isEmpty()) {
                        final BT0075DeliverToAddressLine1 addressLine1 = addressLines1.get(0);
                        addressLine1Value = addressLine1.getValue();
                    } else {
                        log.warn("No [BT-75] DeliverToAddressLine1 was found in current [BG-15] DeliverToAddress");
                    }

                    if (!addressLines2.isEmpty()) {
                        final BT0076DeliverToAddressLine2 addressLine2 = addressLines2.get(0);
                        addressLine2Value = addressLine2.getValue();
                    } else {
                        log.warn("No [BT-76] DeliverToAddressLine2 was found in current [BG-15] DeliverToAddress");
                    }

                    if (!addressLines3.isEmpty()) {
                        final BT0076DeliverToAddressLine2 addressLine3 = addressLines2.get(0);
                        addressLine3Value = addressLine3.getValue();
                    } else {
                        log.warn("No [BT-165] DeliverToAddressLine3 was found in current [BG-15] DeliverToAddress");
                    }


                    final IndirizzoType sede;
                    final FatturaElettronicaBodyType body;
                    for (String s : Lists.newArrayList(addressLine1Value, addressLine2Value, addressLine3Value)) {
                        sb.append(s).append(IConstants.WHITESPACE);
                    }
                    final String addressIt = sb.toString().trim();
                    sede = Optional.fromNullable(cessionarioCommittente.getSede()).or(new IndirizzoType());
                    body = fatturaElettronica.getFatturaElettronicaBody().get(0);
                    if (addressIt.length() > 60) {
                        final String first = addressIt.substring(0, 59);
                        sede.setIndirizzo(first);
                        attachmentUtil.addToUnmappedValuesAttachment(body, "BT0075: " + addressLine1Value);
                        attachmentUtil.addToUnmappedValuesAttachment(body, "BT0076: " + addressLine2Value);
                        attachmentUtil.addToUnmappedValuesAttachment(body, "BT0165: " + addressLine3Value);
                        //                        errors.add(ConversionIssue.newWarning(new EigorException(new ErrorMessage("DeliverToAddress was not compliant with FatturaPA specification. " +
                        //                                "Address has been truncated to the first 60 characters. See not-mapped-values.txt in attachment for the original values"))));
                        log.warn("DeliverToAddress was not compliant with FatturaPA specification. " +
                                "Address has been truncated to the first 60 characters. See not-mapped-values.txt in attachment for the original values");
                    } else {
                        sede.setIndirizzo(addressIt);
                    }

                    if (!postCodes.isEmpty()) {
                        final String postCode = postCodes.get(0).getValue();
                        attachmentUtil.addToUnmappedValuesAttachment(body, "BT0078: " + postCode);
                        sede.setCAP("99999");
//                        errors.add(ConversionIssue.newWarning(new EigorException(new ErrorMessage("DeliverToPostalCode was not compliant with FatturaPA specification. " +
//                                "PostalCode has been replaced with placeholder. See not-mapped-values.txt in attachment for the original values"))));
                        log.warn("DeliverToPostalCode was not compliant with FatturaPA specification. " +
                                "PostalCode has been replaced with placeholder. See not-mapped-values.txt in attachment for the original values");
                    } else {
                        log.warn("No [BT-78] DeliverToPostCode was found in current [BG-15] DeliverToAddress");
                    }
                } else {
                    log.debug("Italian address, keeping normal mapping");
                }

            } else {
                log.warn("No [BG-5] SellerPostalAddress was found in current [BG-4] Seller");
            }
        } else {
            log.warn("No [BG-4] Seller was found in current Invoice");
        }
    }

    private void addDatiTrasporto(BG0000Invoice invoice, FatturaElettronicaBodyType body, DatiGeneraliType datiGenerali, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        List<BG0013DeliveryInformation> deliveryInformations = invoice.getBG0013DeliveryInformation();
        if (!deliveryInformations.isEmpty()) {
            final DatiTrasportoType datiTrasporto = Optional.fromNullable(datiGenerali.getDatiTrasporto()).or(new DatiTrasportoType());
            datiGenerali.setDatiTrasporto(datiTrasporto);
            final BG0013DeliveryInformation deliveryInformation = deliveryInformations.get(0);
            List<BT0072ActualDeliveryDate> deliveryDates = deliveryInformation.getBT0072ActualDeliveryDate();
            if (!deliveryDates.isEmpty()) {
                Optional<LocalDate> deliveryDateOpt = Optional.fromNullable(deliveryDates.get(0).getValue());
                if (deliveryDateOpt.isPresent()) {
                    LocalDate deliveryDate = deliveryDateOpt.get();
                    try {
                        XMLGregorianCalendar converted = dateConverter.convert(deliveryDate);
                        datiTrasporto.setDataOraConsegna(converted);
                    } catch (ConversionFailedException e) {
                        log.error(e.getMessage(), e);
                        errors.add(ConversionIssue.newError(
                                e,
                                "Error creating DataOraConsegna from LocalDate",
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, deliveryDate.toString())
                        ));
                    }
                }
            }
            final List<BG0015DeliverToAddress> deliverToAddress = deliveryInformation.getBG0015DeliverToAddress();
            if (!deliverToAddress.isEmpty()) {
                final BG0015DeliverToAddress address = deliverToAddress.get(0);
                final IndirizzoType indirizzoResa = Optional.fromNullable(datiTrasporto.getIndirizzoResa()).or(new IndirizzoType());
                datiTrasporto.setIndirizzoResa(indirizzoResa);

                String bt75 = evalExpression(() -> address.getBT0075DeliverToAddressLine1(0).getValue());
                String bt76 = evalExpression(() -> address.getBT0076DeliverToAddressLine2(0).getValue());
                final List<BT0077DeliverToCity> deliverToCities = address.getBT0077DeliverToCity();
                final List<BT0078DeliverToPostCode> postCodes = address.getBT0078DeliverToPostCode();
                final List<BT0079DeliverToCountrySubdivision> subdivisions = address.getBT0079DeliverToCountrySubdivision();
                final List<BT0080DeliverToCountryCode> countryCodes = address.getBT0080DeliverToCountryCode();

                if (!countryCodes.isEmpty()) {

                    if(bt75!=null) indirizzoResa.setIndirizzo( bt75 );
                    if(bt76!=null) indirizzoResa.setNumeroCivico( bt76 );

                    final Iso31661CountryCodes countryCode = countryCodes.get(0).getValue();

                    try {
                        indirizzoResa.setNazione(countryCodesConverter.convert(countryCode));
                    } catch (ConversionFailedException e) {
                        log.error(e.getMessage(), e);
                        errors.add(ConversionIssue.newError(
                                e,
                                "Error creating Nazione from Iso31661CountryCodes",
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, countryCodes.toString())
                        ));
                        return;
                    }

                    if (!deliverToCities.isEmpty()) {
                        final String city = deliverToCities.get(0).getValue();
                        final String value = (city != null && !"".equalsIgnoreCase(city.trim())) ? city : "undefined";
                        indirizzoResa.setComune(value);
                    }

                    if (!postCodes.isEmpty()) {
                        final String postCode = postCodes.get(0).getValue();
                        if (postCode.length() > 5) {
                            attachmentUtil.addToUnmappedValuesAttachment(body, "BT0078: " + postCode);
                            indirizzoResa.setCAP("99999");
                            log.warn("DeliverToPostCode was not compliant with FatturaPA specification. " +
                                    "PostalCode has been replaced with placeholder. See not-mapped-values.txt in attachment for the original values");
                        } else {
                            indirizzoResa.setCAP(postCode);
                        }
                    } else {
                        log.warn("No [BT-78] DeliverToPostCode was found in current [BG-5] DeliverToAddress");
                    }

                    if (!subdivisions.isEmpty()) {
                        final String subdivision = subdivisions.get(0).getValue();
                        if (Iso31661CountryCodes.IT.equals(countryCode)) {
                            indirizzoResa.setProvincia(subdivision);
                        } else {
                            attachmentUtil.addToUnmappedValuesAttachment(body, "BT0079: " + subdivision);
                        }
                    }

                } else {
                    final String message = "No [BT-80] DeliverToCountryCode was found in current [BG-15] SellerPostalAddress";
                    final EigorRuntimeException e = new EigorRuntimeException(
                            message,
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.MISSING_VALUE,
                            Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                            Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, countryCodes.toString())
                    );
                    log.error(e.getMessage(), e);
                    errors.add(ConversionIssue.newError(e));
                }

            }

        }

    }

    private void addRiferimentoNormativo(BG0000Invoice invoice, FatturaElettronicaBodyType body, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        DatiBeniServiziType datiBeniServizi = body.getDatiBeniServizi();
        if (datiBeniServizi != null) {
            List<DatiRiepilogoType> datiRiepilogoList = datiBeniServizi.getDatiRiepilogo();
            if (datiRiepilogoList != null) {
                List<BG0023VatBreakdown> breakdowns = invoice.getBG0023VatBreakdown();

                if (breakdowns.size() == datiRiepilogoList.size()) {
                    for (int i = 0; i < breakdowns.size(); i++) {
                        BG0023VatBreakdown bg0023 = breakdowns.get(i);
                        DatiRiepilogoType datiRiepilogo = datiRiepilogoList.get(i);

                        if (!bg0023.getBT0120VatExemptionReasonText().isEmpty()) {
                            String bt0120 = bg0023.getBT0120VatExemptionReasonText().get(0).getValue();

                            if (bg0023.getBT0121VatExemptionReasonCode().isEmpty()) {
                                datiRiepilogo.setRiferimentoNormativo(bt0120);
                            } else {
                                String bt0121 = bg0023.getBT0121VatExemptionReasonCode(0).getValue();
                                datiRiepilogo.setRiferimentoNormativo(String.format("%s %s", bt0120, bt0121));
                            }
                            body.setDatiBeniServizi(datiBeniServizi);
                        } else if (!bg0023.getBT0121VatExemptionReasonCode().isEmpty()) {
                            String bt0121 = bg0023.getBT0121VatExemptionReasonCode(0).getValue();
                            datiRiepilogo.setRiferimentoNormativo(bt0121);
                            body.setDatiBeniServizi(datiBeniServizi);
                        }
                    }
                } else {
                    final String message = "[BG-23] VatBreakdowns list is different size than number of DatiRiepilogo. [BT-120] VatExemptionReasonText were not mapped.";
                    final EigorRuntimeException e = new EigorRuntimeException(
                            message,
                            callingLocation,
                            ErrorCode.Action.HARDCODED_MAP,
                            ErrorCode.Error.ILLEGAL_VALUE
                    );
                    log.error(e.getMessage(), e);
                    errors.add(ConversionIssue.newError(e));
                }
            }
        }
    }

    private void addArrotondamento(BG0000Invoice invoice, FatturaElettronicaBodyType body, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        DatiBeniServiziType datiBeniServizi = body.getDatiBeniServizi();
        if (datiBeniServizi != null) {
            List<DatiRiepilogoType> datiRiepilogoList = datiBeniServizi.getDatiRiepilogo();
            if (datiRiepilogoList != null) {
                for (DatiRiepilogoType datiRiepilogo : datiRiepilogoList) {
                    BigDecimal imponibileImporto = datiRiepilogo.getImponibileImporto();
                    BigDecimal aliquotaIVA = datiRiepilogo.getAliquotaIVA();
                    BigDecimal imposta = datiRiepilogo.getImposta();

                    if (imponibileImporto != null && aliquotaIVA != null && imposta != null) {
                        BigDecimal arrotondamento = imposta.subtract(imponibileImporto.multiply(aliquotaIVA).scaleByPowerOfTen(-2));

                        // no point to add Arrotondamento=0.00000000 if the difference is beyond the 8 decimals
                        if (arrotondamento.setScale(8, RoundingMode.HALF_UP).signum() != 0) {
                            datiRiepilogo.setArrotondamento(arrotondamento.negate());
                        }
                    }
                }
            }
        }
    }

    private void manageNoteText(DatiGeneraliDocumentoType datiGeneraliDocumento, String noteText) {
        if (noteText.length() > 200) {
            datiGeneraliDocumento.getCausale().add(noteText.substring(0, 200));
            manageNoteText(datiGeneraliDocumento, noteText.substring(200));
            log.debug("Splitting note message because longer than 200 characters. Message: {}.", noteText);
        } else {
            datiGeneraliDocumento.getCausale().add(noteText);
        }
    }
}
