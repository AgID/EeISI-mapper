package it.infocert.eigor.converter.fattpa2cen.mapping;


import it.infocert.eigor.converter.fattpa2cen.models.AnagraficaType;
import it.infocert.eigor.converter.fattpa2cen.models.CessionarioCommittenteType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiAnagraficiCessionarioType;
import it.infocert.eigor.converter.fattpa2cen.models.IdFiscaleType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;

class BG07BuyerMapper {

    private static DatiAnagraficiCessionarioType datiAnagrafici;

    static BG0007Buyer mapBuyer(CessionarioCommittenteType cessionarioCommittente) {
        BG0007Buyer buyer = new BG0007Buyer();
        datiAnagrafici = cessionarioCommittente.getDatiAnagrafici();

        buyer.getBG0008BuyerPostalAddress()
                .add(BG08BuyerPostalAddressMapper.mapBuyerPostalAddress(cessionarioCommittente));

        buyer.getBT0044BuyerName()
                .add(new BT0044BuyerName(mapBT44()));


        String id = mapBT46();
        if (id != null) {
            buyer.getBT0046BuyerIdentifierAndSchemeIdentifier()
                    .add(new BT0046BuyerIdentifierAndSchemeIdentifier(id));
        }


        String legalRegId = mapBT47();
        if (legalRegId != null) {
            buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier()
                    .add(new BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(legalRegId));
        }

        String vatId = mapBT48();
        if (vatId != null) {
            buyer.getBT0048BuyerVatIdentifier()
                    .add(new BT0048BuyerVatIdentifier(vatId));
        }

        return buyer;
    }

    private static String mapBT44() {
        AnagraficaType anagrafica = datiAnagrafici.getAnagrafica();
        if (anagrafica.getDenominazione() != null) {
            return anagrafica.getDenominazione();
        } else {
            return anagrafica.getNome() + " " + anagrafica.getCognome();
        }
    }

    private static String mapBT46() {
        return datiAnagrafici.getCodiceFiscale();
    }

    private static String mapBT47() {
        return datiAnagrafici.getAnagrafica().getCodEORI();
    }

    private static String mapBT48() {
        IdFiscaleType idFiscaleIVA = datiAnagrafici.getIdFiscaleIVA();

        if (idFiscaleIVA == null) {
            return null;
        } else {
            String idPaese = idFiscaleIVA.getIdPaese();
            String idCodice = idFiscaleIVA.getIdCodice();

            if (idCodice == null || idPaese == null) {
                return null;
            } else {
                return Iso31661CountryCodes.valueOf(idPaese).toString() +
                        idCodice;
            }
        }
    }


}
