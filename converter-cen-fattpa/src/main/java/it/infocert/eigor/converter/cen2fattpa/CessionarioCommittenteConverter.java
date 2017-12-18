package it.infocert.eigor.converter.cen2fattpa;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CessionarioCommittenteConverter implements CustomMapping<FatturaElettronicaType> {

    private final static Logger log = LoggerFactory.getLogger(CessionarioCommittenteConverter.class);

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        CessionarioCommittenteType cessionarioCommittente = fatturaElettronica.getFatturaElettronicaHeader().getCessionarioCommittente();
        if (cessionarioCommittente != null) {
            FatturaElettronicaBodyType fatturaElettronicaBody = fatturaElettronica.getFatturaElettronicaBody().get(0);
            addCodiceFiscale(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
            addCodiceEori(invoice, fatturaElettronicaBody, cessionarioCommittente, errors);
        } else {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No CessionarioCommittente was found in current FatturaElettronicaHeader")));
        }
    }

    private void addCodiceFiscale(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        if (!invoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer buyer = invoice.getBG0007Buyer(0);
            if (!buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                BT0046BuyerIdentifierAndSchemeIdentifier buyerIdentifier = buyer.getBT0046BuyerIdentifierAndSchemeIdentifier(0);
                Identifier identifier = buyerIdentifier.getValue();
                if (identifier != null) {
                    String identificationSchema = identifier.getIdentificationSchema();
                    if ("IT:CF".equals(identificationSchema)) {

                        DatiAnagraficiCessionarioType datiAnagrafici = cessionarioCommittente.getDatiAnagrafici();
                        if (datiAnagrafici == null) {
                            datiAnagrafici = new DatiAnagraficiCessionarioType();
                        }
                        datiAnagrafici.setCodiceFiscale(identifier.getIdentifier());
                        cessionarioCommittente.setDatiAnagrafici(datiAnagrafici);
                    } else {
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
                                buyerIdentifier.denomination() +
                                ": " +
                                identifier.getIdentificationSchema() +
                                ":" +
                                identifier.getIdentifier();
                        allegato.setAttachment(updated.getBytes());
                    }
                }
            }
        }
    }



    private void addCodiceEori(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody, CessionarioCommittenteType cessionarioCommittente, List<IConversionIssue> errors) {
        if (!invoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer buyer = invoice.getBG0007Buyer(0);
            if (!buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().isEmpty()) {
                BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier registrationIdentifier = buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(0);
                Identifier identifierI = registrationIdentifier.getValue();
                if (identifierI != null) {
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

                    if ("IT:EORI".equals(identificationSchema)) {
                        anagrafica.setCodEORI(identifier);
                    } else {
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
                                registrationIdentifier.denomination() +
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
