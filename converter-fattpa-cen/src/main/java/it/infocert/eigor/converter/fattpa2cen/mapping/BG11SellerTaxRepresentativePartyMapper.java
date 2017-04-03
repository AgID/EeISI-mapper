package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.AnagraficaType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiAnagraficiRappresentanteType;
import it.infocert.eigor.converter.fattpa2cen.models.IdFiscaleType;
import it.infocert.eigor.converter.fattpa2cen.models.RappresentanteFiscaleType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.BG0011SellerTaxRepresentativeParty;
import it.infocert.eigor.model.core.model.BT0062SellerTaxRepresentativeName;
import it.infocert.eigor.model.core.model.BT0063SellerTaxRepresentativeVatIdentifier;

class BG11SellerTaxRepresentativePartyMapper {

    private static DatiAnagraficiRappresentanteType datiAnagrafici;

    static BG0011SellerTaxRepresentativeParty mapSellerTaxRepresentativeParty(RappresentanteFiscaleType rappresentanteFiscale) {
        BG0011SellerTaxRepresentativeParty sellerTaxRepresentativeParty = new BG0011SellerTaxRepresentativeParty();
        datiAnagrafici = rappresentanteFiscale.getDatiAnagrafici();

        sellerTaxRepresentativeParty.getBT0062SellerTaxRepresentativeName()
                .add(new BT0062SellerTaxRepresentativeName(mapBT62()));

        sellerTaxRepresentativeParty.getBT0063SellerTaxRepresentativeVatIdentifier()
                .add(new BT0063SellerTaxRepresentativeVatIdentifier(mapBT63()));

        return sellerTaxRepresentativeParty;
    }

    private static String mapBT62() {
        AnagraficaType anagrafica = datiAnagrafici.getAnagrafica();
        if (anagrafica.getDenominazione() != null) {
            return anagrafica.getDenominazione();
        } else {
            return anagrafica.getNome() + " " + anagrafica.getCognome();
        }
    }

    private static String mapBT63() {
        IdFiscaleType idFiscaleIVA = datiAnagrafici.getIdFiscaleIVA();

        return Iso31661CountryCodes.valueOf(idFiscaleIVA.getIdPaese()).toString() +
                idFiscaleIVA.getIdCodice();
    }
}
