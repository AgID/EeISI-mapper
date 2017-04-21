package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;


import it.infocert.eigor.converter.fattpa2cen.models.CessionarioCommittenteType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;

class BG08BuyerPostalAddressMapper {

    private static IndirizzoType sede;

    static BG0008BuyerPostalAddress mapBuyerPostalAddress(CessionarioCommittenteType cessionarioCommittente) {
        BG0008BuyerPostalAddress buyerPostalAddress = new BG0008BuyerPostalAddress();
        sede = cessionarioCommittente.getSede();

        String address = mapBT50();
        if (address != null) {
            buyerPostalAddress.getBT0050BuyerAddressLine1()
                    .add(new BT0050BuyerAddressLine1(address));
        }

        String civicNumber = mapBT51();
        if (civicNumber != null) {
            buyerPostalAddress.getBT0051BuyerAddressLine2()
                    .add(new BT0051BuyerAddressLine2(civicNumber));
        }

        String city = mapBT52();
        if (city != null) {
            buyerPostalAddress.getBT0052BuyerCity()
                    .add(new BT0052BuyerCity(city));
        }

        String postCode = mapBT53();
        if (postCode != null) {
            buyerPostalAddress.getBT0053BuyerPostCode()
                    .add(new BT0053BuyerPostCode(postCode));
        }

        String subdiv = mapBT54();
        if (subdiv != null) {
            buyerPostalAddress.getBT0054BuyerCountrySubdivision()
                    .add(new BT0054BuyerCountrySubdivision(subdiv));
        }

        String countryCode = mapBT55();
        if (countryCode != null) {
            buyerPostalAddress.getBT0055BuyerCountryCode()
                    .add(new BT0055BuyerCountryCode(Iso31661CountryCodes.valueOf(countryCode)));
        }
 
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
