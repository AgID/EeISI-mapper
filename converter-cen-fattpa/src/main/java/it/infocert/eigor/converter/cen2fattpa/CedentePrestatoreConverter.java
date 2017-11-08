package it.infocert.eigor.converter.cen2fattpa;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class CedentePrestatoreConverter implements CustomMapping<FatturaElettronicaType> {
    private static final Logger log = LoggerFactory.getLogger(CedentePrestatoreConverter.class);

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        CedentePrestatoreType cedentePrestatore = fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore();
        if (cedentePrestatore != null) {
            addRegimeFiscale(invoice, cedentePrestatore, errors);
            List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
            if (!bodies.isEmpty()) {
                FatturaElettronicaBodyType body = bodies.get(0);
                mapBt29(invoice, body, cedentePrestatore, errors);
                mapBt30(invoice, body, cedentePrestatore, errors);
            }
        } else {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No CedentePrestatore was found in current FatturaElettronicaHeader")));
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
                        }
                    }
                }
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
        String updated = content +
                identifier.denomination() +
                ": " +
                identificationSchema +
                ":" +
                identifier;
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

    private void addRegimeFiscale(BG0000Invoice invoice, CedentePrestatoreType cedentePrestatore, List<IConversionIssue> errors) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller seller = invoice.getBG0004Seller(0);

            DatiAnagraficiCedenteType datiAnagrafici = cedentePrestatore.getDatiAnagrafici();
            if (datiAnagrafici != null) {

                if (!seller.getBT0032SellerTaxRegistrationIdentifier().isEmpty()) {
                    BT0032SellerTaxRegistrationIdentifier identifier = seller.getBT0032SellerTaxRegistrationIdentifier(0);
                    if (!seller.getBT0031SellerVatIdentifier().isEmpty() && seller.getBT0031SellerVatIdentifier(0).getValue().startsWith("IT")) {
                        datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.fromValue(identifier.getValue()));
                        log.debug("Mapped BT0031 to RegimeFiscale with value {}", identifier.getValue());
                    } else {
                        datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.RF_18); //FIXME mapping says "will be by default RF00 (inknown- because it is a mandatory" but no RF_00 is present in the enum
                        log.debug("Mapped BT0031 to RegimeFiscale with default value {}", RegimeFiscaleType.RF_18);
                    }
                } else {
                    datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.RF_18); //FIXME mapping says "will be by default RF00 (inknown- because it is a mandatory" but no RF_00 is present in the enum
                    log.debug("Mapped BT0031 to RegimeFiscale with default value {}", RegimeFiscaleType.RF_18);
                }
            } else {
                errors.add(ConversionIssue.newError(new IllegalArgumentException("No DatiAnagrafici was found in current CedentePrestatore")));
            }
        } else {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No CedentePrestatore was found in current FatturaElettronicaHeader")));
        }
    }


}
