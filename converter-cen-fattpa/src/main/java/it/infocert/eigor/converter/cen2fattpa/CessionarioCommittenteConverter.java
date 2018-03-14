package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.base.Optional;
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

public class CessionarioCommittenteConverter implements CustomMapping<FatturaElettronicaType> {

    private final static Logger log = LoggerFactory.getLogger(CessionarioCommittenteConverter.class);
    private final AttachmentUtil attachmentUtil;

    public CessionarioCommittenteConverter() {
        attachmentUtil = new AttachmentUtil();
    }

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        CessionarioCommittenteType cessionarioCommittente = fatturaElettronica.getFatturaElettronicaHeader().getCessionarioCommittente();
        if (cessionarioCommittente != null) {
            FatturaElettronicaBodyType fatturaElettronicaBody = fatturaElettronica.getFatturaElettronicaBody().get(0);
            addCodiceFiscale(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addCodiceEori(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addCAP(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addProvincia(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
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


    private void addCodiceEori(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        if (!invoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer buyer = invoice.getBG0007Buyer(0);
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
                    AnagraficaType anagrafica = datiAnagrafici.getAnagrafica();
                    if (anagrafica == null) {
                        anagrafica = new AnagraficaType();
                        datiAnagrafici.setAnagrafica(anagrafica);
                    }


                    final String eori = "IT:EORI";
                    if ((eori.equals(identificationSchema) || identifier.startsWith(eori)) && italian) {
                        if (identifier.startsWith(eori)) {
                            final String replaced = identifier.replace(eori + ":", "");
                            anagrafica.setCodEORI(replaced);
                        } else {
                            anagrafica.setCodEORI(identifier);
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
                    final IndirizzoType sede = Optional.fromNullable(cessionarioCommittente.getSede()).or(new IndirizzoType());
                    cessionarioCommittente.setSede(sede);
                    if (Iso31661CountryCodes.IT.equals(countryCode)) {
                        sede.setProvincia(subdivision);
                    } else {
                        attachmentUtil.addToUnmappedValuesAttachment(body, "BT0054: " + subdivision);
                    }

                }
            }
        }
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
