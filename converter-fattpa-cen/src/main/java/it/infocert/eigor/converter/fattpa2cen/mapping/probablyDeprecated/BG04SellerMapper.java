package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

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

        BG0006SellerContact sellerContact = BG06SellerContactMapper.mapSellerContact(cedente);
        if (sellerContact != null) {
            seller.getBG0006SellerContact()
                    .add(sellerContact);
        }

        seller.getBT0027SellerName()
                .add(new BT0027SellerName(mapBT27()));


        String fiscalCode = mapBT29FiscalCode();
        if (fiscalCode != null) {
            seller.getBT0029SellerIdentifierAndSchemeIdentifier()
                    .add(new BT0029SellerIdentifierAndSchemeIdentifier(fiscalCode));
        }


        String reaNumber = mapBT29REANumber();
        if (reaNumber != null) {
            seller.getBT0029SellerIdentifierAndSchemeIdentifier()
                    .add(new BT0029SellerIdentifierAndSchemeIdentifier(reaNumber));
        }


        String codEori = mapBT30();
        if (codEori != null) {
            seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier()
                    .add(new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(codEori));
        }

        String vatIdentifier = mapBT31();
        if (vatIdentifier != null) {
            seller.getBT0031SellerVatIdentifier()
                    .add(new BT0031SellerVatIdentifier(vatIdentifier));
        }

        String taxId = mapBT32();
        if (taxId != null) {
            seller.getBT0032SellerTaxRegistrationIdentifier()
                    .add(new BT0032SellerTaxRegistrationIdentifier(taxId));
        }

        String legalInfos = mapBT33();
        if (legalInfos != null) {
            seller.getBT0033SellerAdditionalLegalInformation()
                    .add(new BT0033SellerAdditionalLegalInformation(legalInfos));
        }

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

        String idPaese = idFiscaleIVA.getIdPaese();
        String idCodice = idFiscaleIVA.getIdCodice();

        if (idCodice == null || idPaese == null) {
            return null;
        } else {
            sb.append(idPaese);
            sb.append(idCodice);
            return sb.toString();
        }
    }

    private static String mapBT32() {
        return datiAnagrafici.getRegimeFiscale().toString();
    }

    private static String mapBT33() {
        return cedente.getIscrizioneREA().getCapitaleSociale().toString();
    }
}
