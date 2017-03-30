package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.CedentePrestatoreType;
import it.infocert.eigor.converter.fattpa2cen.models.ContattiType;
import it.infocert.eigor.model.core.model.BG0006SellerContact;
import it.infocert.eigor.model.core.model.BT0042SellerContactTelephoneNumber;
import it.infocert.eigor.model.core.model.BT0043SellerContactEmailAddress;

public class BG06SellerContactMapper {

    private static ContattiType contatti;

    static BG0006SellerContact mapSellerContact(CedentePrestatoreType cedentePrestatore) {
        BG0006SellerContact sellerContact = new BG0006SellerContact();

        contatti = cedentePrestatore.getContatti();

        sellerContact.getBT0042SellerContactTelephoneNumber()
                .add(new BT0042SellerContactTelephoneNumber(mapBT42()));

        sellerContact.getBT0043SellerContactEmailAddress()
                .add(new BT0043SellerContactEmailAddress(mapBT43()));

        return sellerContact;
    }

    private static String mapBT42() {
        return contatti.getTelefono();
    }

    private static String mapBT43() {
        return contatti.getEmail();
    }
}
