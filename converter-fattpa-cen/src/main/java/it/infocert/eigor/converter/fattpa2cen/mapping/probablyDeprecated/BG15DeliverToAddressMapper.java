package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;

class BG15DeliverToAddressMapper {

    private static IndirizzoType indirizzo;

    static BG0015DeliverToAddress mapDeliverToAddress(IndirizzoType indirizzoResa) {
        BG0015DeliverToAddress address = new BG0015DeliverToAddress();

        indirizzo = indirizzoResa;

        String indirizzo = mapBT75();
        if (indirizzo != null) {
            address.getBT0075DeliverToAddressLine1()
                    .add(new BT0075DeliverToAddressLine1(indirizzo));
        }

        if (indirizzo != null) {
            address.getBT0076DeliverToAddressLine2()
                    .add(new BT0076DeliverToAddressLine2(mapBT76()));
        }

        if (indirizzo != null) {
            address.getBT0077DeliverToCity()
                    .add(new BT0077DeliverToCity(mapBT77()));
        }

        if (indirizzo != null) {
            address.getBT0078DeliverToPostCode()
                    .add(new BT0078DeliverToPostCode(mapBT78()));
        }

        if (indirizzo != null) {
            address.getBT0079DeliverToCountrySubdivision()
                    .add(new BT0079DeliverToCountrySubdivision(mapBT79()));
        }

        if (indirizzo != null) {
            address.getBT0080DeliverToCountryCode()
                    .add(new BT0080DeliverToCountryCode(Iso31661CountryCodes.valueOf(mapBT80())));
        }

        return address;
    }

    private static String mapBT75() {
        return indirizzo.getIndirizzo();
    }

    private static String mapBT76() {
        return indirizzo.getNumeroCivico();
    }

    private static String mapBT77() {
        return indirizzo.getComune();
    }

    private static String mapBT78() {
        return indirizzo.getCAP();
    }

    private static String mapBT79() {
        return indirizzo.getProvincia();
    }

    private static String mapBT80() {
        return indirizzo.getNazione();
    }


}
