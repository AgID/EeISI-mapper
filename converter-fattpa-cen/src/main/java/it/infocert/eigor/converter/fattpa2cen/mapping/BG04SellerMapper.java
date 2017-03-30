package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.AnagraficaType;
import it.infocert.eigor.converter.fattpa2cen.models.CedentePrestatoreType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiAnagraficiCedenteType;
import it.infocert.eigor.converter.fattpa2cen.models.IdFiscaleType;
import it.infocert.eigor.model.core.model.*;

class BG04SellerMapper {

    private static CedentePrestatoreType cedente;
    private static DatiAnagraficiCedenteType datiAnagrafici;

    static BG0004Seller mapSeller(CedentePrestatoreType cedentePrestatore) {
        BG0004Seller seller = new BG0004Seller();
        cedente = cedentePrestatore;
        datiAnagrafici = cedente.getDatiAnagrafici();

        seller.getBG0005SellerPostalAddress()
                .add(BG05SellerPostalAddressMapper.mapSellerPostalAddress(cedente));

        seller.getBG0006SellerContact()
                .add(BG06SellerContactMapper.mapSellerContact(cedente));

        seller.getBT0027SellerName()
                .add(new BT0027SellerName(mapBT27()));

        seller.getBT0029SellerIdentifierAndSchemeIdentifier()
                .add(new BT0029SellerIdentifierAndSchemeIdentifier(mapBT29FiscalCode()));
        seller.getBT0029SellerIdentifierAndSchemeIdentifier()
                .add(new BT0029SellerIdentifierAndSchemeIdentifier(mapBT29REANumber()));

        seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier()
                .add(new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(mapBT30()));

        seller.getBT0031SellerVatIdentifier()
                .add(new BT0031SellerVatIdentifier(mapBT31()));

        seller.getBT0032SellerTaxRegistrationIdentifier()
                .add(new BT0032SellerTaxRegistrationIdentifier(mapBT32()));

        seller.getBT0033SellerAdditionalLegalInformation()
                .add(new BT0033SellerAdditionalLegalInformation(mapBT33()));

        return seller;
    }

    private static String mapBT27() {
        AnagraficaType anagrafica = datiAnagrafici.getAnagrafica();
        if (anagrafica.getDenominazione() != null) {
            return anagrafica.getDenominazione();
        } else {
            return anagrafica.getNome() + " " + anagrafica.getCognome();
        }
    }

    private static String mapBT29FiscalCode() {
        return datiAnagrafici.getCodiceFiscale();
    }

    private static String mapBT29REANumber() {
        return cedente.getIscrizioneREA().getNumeroREA();
    }

    private static String mapBT30() {
        return datiAnagrafici.getAnagrafica().getCodEORI();
    }

    private static String mapBT31() {
        StringBuilder sb = new StringBuilder();
        IdFiscaleType idFiscaleIVA = datiAnagrafici.getIdFiscaleIVA();
        sb.append(idFiscaleIVA.getIdPaese());
        sb.append(idFiscaleIVA.getIdCodice());

        return sb.toString();
    }

    private static String mapBT32() {
        return datiAnagrafici.getRegimeFiscale().toString();
    }

    private static String mapBT33() {
        return cedente.getIscrizioneREA().getCapitaleSociale().toString();
    }
}
