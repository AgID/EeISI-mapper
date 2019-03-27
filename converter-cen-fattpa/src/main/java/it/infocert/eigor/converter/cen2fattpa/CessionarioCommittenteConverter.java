package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.fattpa.commons.models.*;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CessionarioCommittenteConverter implements CustomMapping<FatturaElettronicaType> {

    private final static Logger log = LoggerFactory.getLogger(CessionarioCommittenteConverter.class);
    private final AttachmentUtil attachmentUtil;

    public CessionarioCommittenteConverter() {
        attachmentUtil = new AttachmentUtil();
    }

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        CessionarioCommittenteType cessionarioCommittente = fatturaElettronica.getFatturaElettronicaHeader().getCessionarioCommittente();

        if (cessionarioCommittente != null) {
            FatturaElettronicaBodyType fatturaElettronicaBody = fatturaElettronica.getFatturaElettronicaBody().get(0);
            addCodiceFiscale(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addCodiceEori(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addIndirizzo(invoice, fatturaElettronica, cessionarioCommittente, errors);
            addProvincia(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addCAP(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);

            // fix for https://gitlab.com/tgi-infocert-eigor/eigor/issues/269
            // sometimes idPaese and idCodice are messed.
            if (cessionarioCommittente.getDatiAnagrafici() != null &&
                    cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA() != null &&
                    cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese() != null &&
                    cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice() != null) {
                String idPaese = cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
                String idCodice = cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
                if (idCodice.startsWith(":") && idCodice.length() >= 3) {
                    idPaese = idCodice.substring(1, 3);
                    idCodice = idCodice.substring(3);
                    cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA().setIdPaese(idPaese);
                    cessionarioCommittente.getDatiAnagrafici().getIdFiscaleIVA().setIdCodice(idCodice);
                }
            }

        } else {
            final String message = "No CessionarioCommittente was found in current FatturaElettronicaHeader";
            errors.add(ConversionIssue.newError(new EigorRuntimeException(
                    message,
                    callingLocation,
                    ErrorCode.Action.HARDCODED_MAP,
                    ErrorCode.Error.MISSING_VALUE,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, "CessionarioCommittente")
            )));
        }
    }

    private void addCodiceFiscale(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        if (!invoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer buyer = invoice.getBG0007Buyer(0);
            if (!buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                BT0046BuyerIdentifierAndSchemeIdentifier buyerIdentifier = buyer.getBT0046BuyerIdentifierAndSchemeIdentifier(0);
                final List<BG0008BuyerPostalAddress> addresses = buyer.getBG0008BuyerPostalAddress();
                if (!addresses.isEmpty()) {
                    final BG0008BuyerPostalAddress address = buyer.getBG0008BuyerPostalAddress(0);
                    final List<BT0055BuyerCountryCode> countryCodes = address.getBT0055BuyerCountryCode();
                    if (!countryCodes.isEmpty()) {
                        final Iso31661CountryCodes countryCode = countryCodes.get(0).getValue();
                        Identifier identifier = buyerIdentifier.getValue();
                        if (identifier != null) {
                            String identificationSchema = identifier.getIdentificationSchema();
                            if (Iso31661CountryCodes.IT.equals(countryCode) && "IT:CF".equals(identificationSchema)) {

                                DatiAnagraficiCessionarioType datiAnagrafici = cessionarioCommittente.getDatiAnagrafici();
                                if (datiAnagrafici == null) {
                                    datiAnagrafici = new DatiAnagraficiCessionarioType();
                                }
                                datiAnagrafici.setCodiceFiscale(identifier.getIdentifier());
                                cessionarioCommittente.setDatiAnagrafici(datiAnagrafici);
                            } else {
                                attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronicaBody, String.format("%s: %s:%s", buyerIdentifier.denomination(), identificationSchema, identifier));
                            }
                        }
                    } else {
                        log.error("No Country Code found in Buyer");
                    }
                }
            }
        }
    }

    private void addIndirizzo(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        final List<BG0007Buyer> buyer = invoice.getBG0007Buyer();
        if (!buyer.isEmpty()) {
            final List<BG0008BuyerPostalAddress> addresses = buyer.get(0).getBG0008BuyerPostalAddress();
            if (!addresses.isEmpty()) {
                final BG0008BuyerPostalAddress address = addresses.get(0);
                Optional<String> subdivision = Optional.absent();
                if (!address.getBT0054BuyerCountrySubdivision().isEmpty()) {
                    subdivision = Optional.fromNullable(address.getBT0054BuyerCountrySubdivision(0).getValue());
                }

                final List<BT0055BuyerCountryCode> countryCodes = address.getBT0055BuyerCountryCode();
                if (!countryCodes.isEmpty() && !Iso31661CountryCodes.IT.equals(countryCodes.get(0).getValue())) {
                    log.debug("Non-italian address, applying custom mapping");
                    final List<BT0053BuyerPostCode> postCodes = address.getBT0053BuyerPostCode();

                    final List<BT0050BuyerAddressLine1> addressLines1 = address.getBT0050BuyerAddressLine1();
                    final List<BT0051BuyerAddressLine2> addressLines2 = address.getBT0051BuyerAddressLine2();
                    final List<BT0163BuyerAddressLine3> addressLines3 = address.getBT0163BuyerAddressLine3();
                    final List<BT0052BuyerCity> buyerCity = address.getBT0052BuyerCity();
                    final StringBuilder sb = new StringBuilder();
                    String addressLine1Value = "";
                    String addressLine2Value = "";
                    String addressLine3Value = "";
                    if (!addressLines1.isEmpty()) {
                        final BT0050BuyerAddressLine1 addressLine1 = addressLines1.get(0);
                        addressLine1Value = addressLine1.getValue();
                    } else {
                        log.warn("No [BT-50] BuyerAddressLine1 was found in current [BG-5] BuyerPostalAddress");
                    }

                    if (!addressLines2.isEmpty()) {
                        final BT0051BuyerAddressLine2 addressLine2 = addressLines2.get(0);
                        addressLine2Value = addressLine2.getValue();
                    } else {
                        log.warn("No [BT-51] BuyerAddressLine2 was found in current [BG-5] BuyerPostalAddress");
                    }

                    if (!addressLines3.isEmpty()) {
                        final BT0163BuyerAddressLine3 addressLine3 = addressLines3.get(0);
                        addressLine3Value = addressLine3.getValue();
                    } else {
                        log.warn("No [BT-163] BuyerAddressLine3 was found in current [BG-5] BuyerPostalAddress");
                    }

                    final IndirizzoType sede = Optional.fromNullable(cessionarioCommittente.getSede()).or(new IndirizzoType());
                    for (String s : Lists.newArrayList(addressLine1Value, addressLine2Value, addressLine3Value)) {
                        sb.append(s).append(IConstants.WHITESPACE);
                    }
                    String addressIt = sb.toString().trim();
                        addressIt = addressIt.isEmpty() ? "undefined" : addressIt;
                        sede.setIndirizzo(addressIt);

                    if (!buyerCity.isEmpty()) {
                        String city = buyerCity.get(0).getValue();
                        city = (city.isEmpty()) ? "undefined" : city;
                        sede.setComune(city);
                    }

                    if (subdivision.isPresent())
                        attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronica.getFatturaElettronicaBody().get(0), "BT0054: " + subdivision.get());
                } else {
                    log.debug("Italian address");
                    final IndirizzoType sede = Optional.fromNullable(cessionarioCommittente.getSede()).or(new IndirizzoType());
                    if (subdivision.isPresent()) sede.setProvincia(subdivision.get());
                }

            } else {
                log.warn("No [BG-8] BuyerPostalAddress was found in current [BG-7] Buyer");
            }
        } else {
            log.warn("No [BG-7] Buyer was found in current Invoice");
        }
    }

    private void addCodiceEori(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {

        BG0007Buyer buyer = InvoiceUtils.evalExpression(() -> invoice.getBG0007Buyer(0));
        if (buyer == null) return;

        if (!buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
            BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier registrationIdentifier = buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(0);
            Identifier identifierI = registrationIdentifier.getValue();

            if (identifierI != null) {
                boolean italian = false;
                if (!buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                    if (!buyer.getBG0008BuyerPostalAddress().isEmpty()) {
                        final List<BT0055BuyerCountryCode> countryCodes = buyer.getBG0008BuyerPostalAddress(0).getBT0055BuyerCountryCode();
                        if (!countryCodes.isEmpty()) {
                            final Iso31661CountryCodes countryCode = countryCodes.get(0).getValue();
                            italian = Iso31661CountryCodes.IT.equals(countryCode);
                        }
                    }


                    String identificationSchema = identifierI.getIdentificationSchema();
                    String identifier = identifierI.getIdentifier();

                    DatiAnagraficiCessionarioType datiAnagrafici = cessionarioCommittente.getDatiAnagrafici();
                    if (datiAnagrafici == null) {
                        datiAnagrafici = new DatiAnagraficiCessionarioType();
                        cessionarioCommittente.setDatiAnagrafici(datiAnagrafici);
                    }
                    AnagraficaType anagraficaXml = datiAnagrafici.getAnagrafica();
                    if (anagraficaXml == null) {
                        anagraficaXml = new AnagraficaType();
                        datiAnagrafici.setAnagrafica(anagraficaXml);
                    }


//                    String name = InvoiceUtils.evalExpression(() -> buyer.getBG0009BuyerContact(0).getBT0056BuyerContactPoint(0).getValue());
//                    if(name != null) anagraficaXml.setDenominazione( name );


                    final String eori = "IT:EORI";
                    if ((eori.equals(identificationSchema) || identifier.startsWith(eori)) && italian) {
                        if (identifier.startsWith(eori)) {
                            final String replaced = identifier.replace(eori + ":", "");
                            anagraficaXml.setCodEORI(replaced);
                        } else {
                            anagraficaXml.setCodEORI(identifier);
                        }
                    } else {
                        attachmentUtil.addToUnmappedValuesAttachment(fatturaElettronicaBody, String.format("BT0047: %s:%s", identificationSchema != null ? identificationSchema.trim() : "", identifier));
                    }

                }

            }
        }
    }

    private void addProvincia(BG0000Invoice invoice, FatturaElettronicaBodyType body, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        final List<BG0007Buyer> buyers = invoice.getBG0007Buyer();
        final IndirizzoType sede = Optional.fromNullable(cessionarioCommittente.getSede()).or(new IndirizzoType());
        if (!buyers.isEmpty()) {
            final BG0007Buyer buyer = buyers.get(0);
            final List<BG0008BuyerPostalAddress> addresses = buyer.getBG0008BuyerPostalAddress();
            if (!addresses.isEmpty()) {
                final BG0008BuyerPostalAddress address = buyer.getBG0008BuyerPostalAddress(0);
                final List<BT0054BuyerCountrySubdivision> subdivisions = address.getBT0054BuyerCountrySubdivision();
                final List<BT0055BuyerCountryCode> countryCodes = address.getBT0055BuyerCountryCode();
                if (!subdivisions.isEmpty() && !countryCodes.isEmpty()) {
                    final String subdivision = subdivisions.get(0).getValue();
                    final Iso31661CountryCodes countryCode = countryCodes.get(0).getValue();
                    cessionarioCommittente.setSede(sede);
                    if (Iso31661CountryCodes.IT.equals(countryCode)) {
                        sede.setProvincia(subdivision);
                    } else {
                        attachmentUtil.addToUnmappedValuesAttachment(body, "BT0054: " + subdivision);
                    }
                }
            }
        }
//        if (sede.getProvincia() == null || !sede.getProvincia().isEmpty()) {
//            sede.setProvincia("XX");
//        }
    }

    private void addCAP(BG0000Invoice invoice, FatturaElettronicaBodyType body, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        final List<BG0007Buyer> buyers = invoice.getBG0007Buyer();
        if (!buyers.isEmpty()) {
            final BG0007Buyer buyer = buyers.get(0);
            final List<BG0008BuyerPostalAddress> addresses = buyer.getBG0008BuyerPostalAddress();
            if (!addresses.isEmpty()) {
                final BG0008BuyerPostalAddress address = buyer.getBG0008BuyerPostalAddress(0);
                final List<BT0053BuyerPostCode> postCodes = address.getBT0053BuyerPostCode();
                final List<BT0055BuyerCountryCode> countryCodes = address.getBT0055BuyerCountryCode();
                if (!postCodes.isEmpty() && !countryCodes.isEmpty()) {
                    final String postCode = postCodes.get(0).getValue();
                    final Iso31661CountryCodes countryCode = countryCodes.get(0).getValue();
                    final IndirizzoType sede = Optional.fromNullable(cessionarioCommittente.getSede()).or(new IndirizzoType());
                    cessionarioCommittente.setSede(sede);
                    if (Iso31661CountryCodes.IT.equals(countryCode)) {
                        sede.setCAP(postCode);
                    } else {
                        if (postCode.length() > 5) {
                            sede.setCAP("99999");
                            attachmentUtil.addToUnmappedValuesAttachment(body, "BT0053: " + postCode);
                        } else {
                            sede.setCAP(postCode);
                        }
                    }

                }
            }
        }

    }
}
