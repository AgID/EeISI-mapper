package it.infocert.eigor.converter.cen2fattpa;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CedentePrestatoreConverter implements CustomMapping<FatturaElettronicaType> {
    private static final Logger log = LoggerFactory.getLogger(CedentePrestatoreConverter.class);
    private final AttachmentUtil attachmentUtil;

    public CedentePrestatoreConverter() {
        attachmentUtil = new AttachmentUtil();
    }

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        CedentePrestatoreType cedentePrestatore = fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore();
        if (cedentePrestatore != null) {
            List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
            if (!bodies.isEmpty()) {
                FatturaElettronicaBodyType body = bodies.get(0);
                addRegimeFiscale(invoice, body, cedentePrestatore, errors, callingLocation);
                mapBt29(invoice, body, cedentePrestatore, errors);
                mapBt30(invoice, body, cedentePrestatore, errors);
                addIndirizzo(invoice, fatturaElettronica, errors);
            }
        } else {
            final IllegalArgumentException e = new IllegalArgumentException("No CedentePrestatore was found in current FatturaElettronicaHeader");
            errors.add(ConversionIssue.newError(
                    e,
                    e.getMessage(),
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.ILLEGAL_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
            ));
        }
    }


    private void mapBt30(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CedentePrestatoreType cedentePrestatore, List<IConversionIssue> errors) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller seller = invoice.getBG0004Seller(0);

            if (!seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier identifier = seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(0);
                Identifier id = identifier.getValue();
                final String code = id.getIdentifier();
                final String identificationSchema = id.getIdentificationSchema() != null ? id.getIdentificationSchema() : "null";
                if (!seller.getBG0005SellerPostalAddress().isEmpty()) {
                    BG0005SellerPostalAddress postalAddress = seller.getBG0005SellerPostalAddress(0);
                    if (!postalAddress.getBT0040SellerCountryCode().isEmpty()) {
                        Iso31661CountryCodes countryCode = postalAddress.getBT0040SellerCountryCode(0).getValue();
                        if (Iso31661CountryCodes.IT.equals(countryCode)) {
                            if (code.startsWith("IT:REA")) {
                                String[] slices = code.split(":");
                                if (slices.length > 3) {
                                    IscrizioneREAType iscrizioneREA;
                                    if ((iscrizioneREA = cedentePrestatore.getIscrizioneREA()) == null) {
                                        iscrizioneREA = new IscrizioneREAType();
                                        cedentePrestatore.setIscrizioneREA(iscrizioneREA);
                                    }
                                    iscrizioneREA.setUfficio(slices[2]);
                                    iscrizioneREA.setNumeroREA(slices[3]);
                                    iscrizioneREA.setStatoLiquidazione(StatoLiquidazioneType.LN);
                                } else {
                                    setAllegato(fatturaElettronicaBody, identifier, identificationSchema);
                                }
                            } else {
                                setAllegato(fatturaElettronicaBody, identifier, identificationSchema);
                            }
                        }
                    }
                }
                log.info("Mapping BT0030 {} with identification schema {}.", code, identificationSchema);
                switch (identificationSchema) {
                    case "IT:REA":
                        IscrizioneREAType iscrizioneREA;
                        if ((iscrizioneREA = cedentePrestatore.getIscrizioneREA()) == null) {
                            iscrizioneREA = new IscrizioneREAType();
                            cedentePrestatore.setIscrizioneREA(iscrizioneREA);
                        }
                        String[] slices = code.split(":");
                        iscrizioneREA.setUfficio(slices[0]);
                        iscrizioneREA.setNumeroREA(slices[2]);
                        iscrizioneREA.setStatoLiquidazione(StatoLiquidazioneType.LN);
                        break;
//                        case "IT:ALBO":
//                            DatiAnagraficiCedenteType datiAnagrafici;
//                            if ((datiAnagrafici = cedentePrestatore.getDatiAnagrafici()) != null) {
//                                datiAnagrafici.setNumeroIscrizioneAlbo(code);
//                            }
//                            break;
                    default:
                        setAllegato(fatturaElettronicaBody, identifier, identificationSchema);
                }
            }
        }
    }

    private void setAllegato(FatturaElettronicaBodyType fatturaElettronicaBody, AbstractBT identifier, String identificationSchema) {
        List<AllegatiType> allegati = fatturaElettronicaBody.getAllegati();
        String content = "";
        AllegatiType allegato;
        if (allegati.isEmpty()) {
            allegato = new AllegatiType();
            allegato.setNomeAttachment("not-mapped-values");
            allegato.setFormatoAttachment("txt");
            allegati.add(allegato);
        } else {
            allegato = Stream.of(allegati).filter(new Filter<AllegatiType>() {
                @Override
                public boolean apply(AllegatiType allegato) {
                    return "not-mapped-values".equals(allegato.getNomeAttachment());
                }
            }).first();
            content = new String(allegato.getAttachment());
        }
        StringBuilder sb = new StringBuilder(content + System.lineSeparator())
                .append(identifier.denomination())
                .append(": ");

        if (identificationSchema != null && !"null".equals(identificationSchema)) {
            sb.append(identificationSchema);
        }

        String updated = sb.append(":").append(identifier.getValue()).append(System.lineSeparator()).toString();
        log.info("Added {} to Allegati.", updated);
        allegato.setAttachment(updated.getBytes());
    }

    private void mapBt29(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CedentePrestatoreType cedentePrestatore, List<IConversionIssue> errors) {

        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller seller = invoice.getBG0004Seller(0);
            List<BT0029SellerIdentifierAndSchemeIdentifier> sellerIdentifiers = seller.getBT0029SellerIdentifierAndSchemeIdentifier();
            if (!sellerIdentifiers.isEmpty()) {
                for (BT0029SellerIdentifierAndSchemeIdentifier sellerIdentifier : sellerIdentifiers) {
                    Identifier value = sellerIdentifier.getValue();
                    if (value != null) {
                        String identificationSchema = value.getIdentificationSchema() != null ? value.getIdentificationSchema() : "null";
                        String identifier = value.getIdentifier();

                        DatiAnagraficiCedenteType datiAnagrafici = cedentePrestatore.getDatiAnagrafici();
                        if (datiAnagrafici == null) {
                            datiAnagrafici = new DatiAnagraficiCedenteType();
                            cedentePrestatore.setDatiAnagrafici(datiAnagrafici);
                        }

                        AnagraficaType anagrafica = datiAnagrafici.getAnagrafica();
                        if (anagrafica == null) {
                            anagrafica = new AnagraficaType();
                            datiAnagrafici.setAnagrafica(anagrafica);
                        }

                        if ("null".equals(identificationSchema) || "".equals(identificationSchema) || identifier.matches("IT:\\w*:.+")) {
                            final Pattern pattern = Pattern.compile("(IT:\\w*):(.+)");
                            final Matcher matcher = pattern.matcher(identifier);
                            if (matcher.matches()) {
                                identificationSchema = matcher.group(1);
                                identifier = matcher.group(2);
                            }
                        }

                        switch (identificationSchema) {

                            case "IT:EORI":
                                anagrafica.setCodEORI(identifier);
                                break;

                            case "IT:CF":
                                datiAnagrafici.setCodiceFiscale(identifier);
                                break;

                            case "IT:ALBO":
                                String[] slices = identifier.split(":");
                                if (slices.length == 2) {
                                    datiAnagrafici.setAlboProfessionale(slices[0]);
                                    datiAnagrafici.setNumeroIscrizioneAlbo(slices[1]);
                                }
                                break;
                            default: {
                                List<AllegatiType> allegati = fatturaElettronicaBody.getAllegati();
                                String content = "";
                                AllegatiType allegato;
                                if (allegati.isEmpty()) {
                                    allegato = new AllegatiType();
                                    allegato.setNomeAttachment("not-mapped-values");
                                    allegato.setFormatoAttachment("txt");
                                    allegati.add(allegato);
                                } else {
                                    allegato = Stream.of(allegati).filter(new Filter<AllegatiType>() {
                                        @Override
                                        public boolean apply(AllegatiType allegato) {
                                            return "not-mapped-values".equals(allegato.getNomeAttachment());
                                        }
                                    }).first();
                                    content = new String(allegato.getAttachment());
                                }
                                String updated = content +
                                        sellerIdentifier.denomination() +
                                        ": " +
                                        identificationSchema +
                                        ":" +
                                        identifier;
                                allegato.setAttachment(updated.getBytes());
                            }
                        }

                    }
                }

            }
        }

    }

    private void addIndirizzo(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        final CedentePrestatoreType cedentePrestatore = fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore();
        final List<BG0004Seller> sellers = invoice.getBG0004Seller();
        if (!sellers.isEmpty()) {
            final List<BG0005SellerPostalAddress> addresses = sellers.get(0).getBG0005SellerPostalAddress();
            if (!addresses.isEmpty()) {
                final BG0005SellerPostalAddress address = addresses.get(0);
                Optional<String> subdivision = Optional.absent();
                if (!address.getBT0039SellerCountrySubdivision().isEmpty()) {
                    subdivision = Optional.fromNullable(address.getBT0039SellerCountrySubdivision(0).getValue());
                }

                final List<BT0040SellerCountryCode> countryCodes = address.getBT0040SellerCountryCode();
                if (!countryCodes.isEmpty() && !Iso31661CountryCodes.IT.equals(countryCodes.get(0).getValue())) {
                    log.debug("Non-italian address, applying custom mapping");
                    final List<BT0038SellerPostCode> postCodes = address.getBT0038SellerPostCode();

                    final List<BT0035SellerAddressLine1> addressLines1 = address.getBT0035SellerAddressLine1();
                    final List<BT0036SellerAddressLine2> addressLines2 = address.getBT0036SellerAddressLine2();
                    final List<BT0162SellerAddressLine3> addressLines3 = address.getBT0162SellerAddressLine3();
                    final StringBuilder sb = new StringBuilder();
                    String addressLine1Value = "";
                    String addressLine2Value = "";
                    String addressLine3Value = "";
                    if (!addressLines1.isEmpty()) {
                        final BT0035SellerAddressLine1 addressLine1 = addressLines1.get(0);
                        addressLine1Value = addressLine1.getValue();
                    } else {
                        log.warn("No [BT-35] SellerAddressLine1 was found in current [BG-5] SellerPostalAddress");
                    }

                    if (!addressLines2.isEmpty()) {
                        final BT0036SellerAddressLine2 addressLine2 = addressLines2.get(0);
                        addressLine2Value = addressLine2.getValue();
                    } else {
                        log.warn("No [BT-36] SellerAddressLine2 was found in current [BG-5] SellerPostalAddress");
                    }

                    if (!addressLines3.isEmpty()) {
                        final BT0162SellerAddressLine3 addressLine3 = addressLines3.get(0);
                        addressLine3Value = addressLine3.getValue();
                    } else {
                        log.warn("No [BT-162] SellerAddressLine3 was found in current [BG-5] SellerPostalAddress");
                    }

                    final IndirizzoType sede = Optional.fromNullable(cedentePrestatore.getSede()).or(new IndirizzoType());
                    for (String s : Lists.newArrayList(addressLine1Value, addressLine2Value, addressLine3Value)) {
                        sb.append(s).append(IConstants.WHITESPACE);
                    }
                    final String addressIt = sb.toString().trim();
                    final FatturaElettronicaBodyType body = fatturaElettronica.getFatturaElettronicaBody().get(0);
                    if (addressIt.length() > 60) {
                        final String first = addressIt.substring(0, 59);
                        sede.setIndirizzo(first);
                        attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronica.getFatturaElettronicaBody().get(0), "BT0035: " + addressLine1Value);
                        attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronica.getFatturaElettronicaBody().get(0), "BT0036: " + addressLine2Value);
                        attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronica.getFatturaElettronicaBody().get(0), "BT0162: " + addressLine3Value);
//                        errors.add(ConversionIssue.newWarning(new EigorException(new ErrorMessage("SellerAddress was not compliant with FatturaPA specification. " +
//                                "Address has been truncated to the first 60 characters. See not-mapped-values.txt in attachment for the original values"))));
                        log.warn("SellerAddress was not compliant with FatturaPA specification. " +
                                "Address has been truncated to the first 60 characters. See not-mapped-values.txt in attachment for the original values");
                    } else {
                        sede.setIndirizzo(addressIt);
                    }

                    if (!postCodes.isEmpty()) {
                        final String postCode = postCodes.get(0).getValue();
                        if (postCode.length() > 5) {
                            attachmentUtil.addToUnmappedValuesAttachment(body, "BT0038: " + postCode);
                            sede.setCAP("99999");
//                        errors.add(ConversionIssue.newWarning(new EigorException(new ErrorMessage("SellerPostalCode was not compliant with FatturaPA specification. " +
//                                "PostalCode has been replaced with placeholder. See not-mapped-values.txt in attachment for the original values"))));
                            log.warn("SellerPostalCode was not compliant with FatturaPA specification. " +
                                    "PostalCode has been replaced with placeholder. See not-mapped-values.txt in attachment for the original values");
                        } else {
                            sede.setCAP(postCode);
                        }
                    } else {
                        log.warn("No [BT-38] SellerPostCode was found in current [BG-5] SellerPostalAddress");
                    }
                    if (subdivision.isPresent())
                        attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronica.getFatturaElettronicaBody().get(0), "BT0039: " + subdivision.get());
                } else {
                    log.debug("Italian address");
                    final IndirizzoType sede = Optional.fromNullable(cedentePrestatore.getSede()).or(new IndirizzoType());
                    if (subdivision.isPresent()) sede.setProvincia(subdivision.get());
                }

            } else {
                log.warn("No [BG-5] SellerPostalAddress was found in current [BG-4] Seller");
            }
        } else {
            log.warn("No [BG-4] Seller was found in current Invoice");
        }
    }

    private void addRegimeFiscale(BG0000Invoice invoice, FatturaElettronicaBodyType body, CedentePrestatoreType cedentePrestatore, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller seller = invoice.getBG0004Seller(0);

            DatiAnagraficiCedenteType datiAnagrafici = cedentePrestatore.getDatiAnagrafici();
            if (datiAnagrafici != null) {

                if (!seller.getBT0032SellerTaxRegistrationIdentifier().isEmpty()) {
                    BT0032SellerTaxRegistrationIdentifier identifier = seller.getBT0032SellerTaxRegistrationIdentifier(0);
                    final String value = identifier.getValue();
                    if (!seller.getBT0031SellerVatIdentifier().isEmpty() && seller.getBT0031SellerVatIdentifier(0).getValue().startsWith("IT")) {
                        try {
                            datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.fromValue(value));
                            log.debug("Mapped BT0032 to RegimeFiscale with value {}", value);
                        } catch (IllegalArgumentException e) {
                            log.error(e.getMessage());
                            final String message = String.format("BT-32 value '%s' is not a valid RegimeFiscale code", value);
                            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                    message,
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.ILLEGAL_VALUE,
                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value)
                            )));
                        }
                    } else {
                        datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.RF_18);
                        attachmentUtil.addToUnmappedValuesAttachment(body, String.format("BT-32: %s", value));
                        log.debug("Mapped BT0032 to RegimeFiscale with default value {}", RegimeFiscaleType.RF_18);
                    }
                } else {
                    datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.RF_18);
                    log.debug("Mapped RegimeFiscale with default value {} since BT-32 is empty", RegimeFiscaleType.RF_18);
                }
            } else {
                final String message = "No DatiAnagrafici was found in current CedentePrestatore";
                errors.add(ConversionIssue.newError(new EigorRuntimeException(
                        message,
                        callingLocation,
                        ErrorCode.Action.HARDCODED_MAP,
                        ErrorCode.Error.MISSING_VALUE,
                        Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                        Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "DatiAnagrafici")
                )));
            }
        } else {
            final String message = "No [BG-4] Seller was found in current Invoice";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.MISSING_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "BG-4")
            )));
        }
    }


}
