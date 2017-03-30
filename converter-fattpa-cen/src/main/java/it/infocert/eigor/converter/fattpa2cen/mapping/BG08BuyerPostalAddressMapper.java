package it.infocert.eigor.converter.fattpa2cen.mapping;


import it.infocert.eigor.converter.fattpa2cen.models.CessionarioCommittenteType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;

class BG08BuyerPostalAddressMapper {

    private static IndirizzoType sede;

    static BG0008BuyerPostalAddress mapBuyerPostalAddress(CessionarioCommittenteType cessionarioCommittente) {
        BG0008BuyerPostalAddress buyerPostalAddress = new BG0008BuyerPostalAddress();
        sede = cessionarioCommittente.getSede();

        buyerPostalAddress.getBT0050BuyerAddressLine1()
                .add(new BT0050BuyerAddressLine1(mapBT50()));

        buyerPostalAddress.getBT0051BuyerAddressLine2()
                .add(new BT0051BuyerAddressLine2(mapBT51()));

        buyerPostalAddress.getBT0052BuyerCity()
                .add(new BT0052BuyerCity(mapBT52()));

        buyerPostalAddress.getBT0053BuyerPostCode()
                .add(new BT0053BuyerPostCode(mapBT53()));

        buyerPostalAddress.getBT0054BuyerCountrySubdivision()
                .add(new BT0054BuyerCountrySubdivision(mapBT54()));

        buyerPostalAddress.getBT0055BuyerCountryCode()
                .add(new BT0055BuyerCountryCode(Iso31661CountryCodes.valueOf(mapBT55())));

        return buyerPostalAddress;
    }

    private static String mapBT50() {
        return sede.getIndirizzo();
    }

    private static String mapBT51() {
        return sede.getNumeroCivico();
    }

    private static String mapBT52() {
        return sede.getComune();
    }

    private static String mapBT53() {
        return sede.getCAP();
    }

    private static String mapBT54() {
        return sede.getProvincia();
    }

    private static String mapBT55() {
        return sede.getNazione();
    }
}
