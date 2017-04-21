package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.DatiTrasportoType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.model.BG0013DeliveryInformation;
import it.infocert.eigor.model.core.model.BT0072ActualDeliveryDate;

import java.time.LocalDate;

class BG13DeliveryInformationMapper {

    private static DatiTrasportoType datiTrasporto;

    static BG0013DeliveryInformation mapDeliveryInformation(DatiTrasportoType datiTrasportoType) {
        BG0013DeliveryInformation deliveryInformation = new BG0013DeliveryInformation();

        datiTrasporto = datiTrasportoType;

        IndirizzoType indirizzoResa = datiTrasporto.getIndirizzoResa();
        if (indirizzoResa != null) {
            deliveryInformation.getBG0015DeliverToAddress()
                    .add(BG15DeliverToAddressMapper.mapDeliverToAddress(indirizzoResa));
        }

        LocalDate deliveryTime = mapBT72();
        if (deliveryTime != null) {
            deliveryInformation.getBT0072ActualDeliveryDate()
                    .add(new BT0072ActualDeliveryDate(deliveryTime));
        }
        return deliveryInformation;
    }

    private static LocalDate mapBT72() {
        return datiTrasporto.getDataOraConsegna().toGregorianCalendar().toZonedDateTime().toLocalDate();
    }
}
