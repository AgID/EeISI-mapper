package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.CedentePrestatoreType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
 class BG05SellerPostalAddressMapper {

    private static IndirizzoType sede;

    static BG0005SellerPostalAddress mapSellerPostalAddress(CedentePrestatoreType cedentePrestatore) {
        BG0005SellerPostalAddress sellerPostalAddress = new BG0005SellerPostalAddress();
        sede = cedentePrestatore.getSede();

        sellerPostalAddress.getBT0035SellerAddressLine1()
                .add(new BT0035SellerAddressLine1(mapBT35()));

        sellerPostalAddress.getBT0036SellerAddressLine2()
                .add(new BT0036SellerAddressLine2(mapBT36()));

        sellerPostalAddress.getBT0037SellerCity()
                .add(new BT0037SellerCity(maptBT37()));

        sellerPostalAddress.getBT0038SellerPostCode()
                .add(new BT0038SellerPostCode(mapBT38()));

        sellerPostalAddress.getBT0039SellerCountrySubdivision()
                .add(new BT0039SellerCountrySubdivision(mapBT39()));

        sellerPostalAddress.getBT0040SellerCountryCode()
                .add(new BT0040SellerCountryCode(Iso31661CountryCodes.valueOf(mapBT40())));

        return sellerPostalAddress;
    }

    private static String mapBT35() {
        return sede.getIndirizzo();
    }

    private static String mapBT36() {
        return sede.getNumeroCivico();
    }

    private static String maptBT37() {
        return sede.getComune();
    }

    private static String mapBT38() {
        return sede.getCAP();
    }

    private static String mapBT39() {
        return sede.getProvincia();
    }

    private static String mapBT40() {
        return sede.getNazione();
    }
}
