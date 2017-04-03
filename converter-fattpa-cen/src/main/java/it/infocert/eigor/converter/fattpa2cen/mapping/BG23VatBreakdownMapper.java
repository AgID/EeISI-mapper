package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.DatiRiepilogoType;
import it.infocert.eigor.converter.fattpa2cen.models.NaturaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0023VatBreakdown;
import it.infocert.eigor.model.core.model.BT0118VatCategoryCode;
import it.infocert.eigor.model.core.model.BT0119VatCategoryRate;

import java.math.BigDecimal;

class BG23VatBreakdownMapper {

    private static DatiRiepilogoType datiRiepilogo;

    static BG0023VatBreakdown mapVatBreakdown(DatiRiepilogoType datiRiepilogoType) {
        BG0023VatBreakdown vatBreakdown = new BG0023VatBreakdown();

        datiRiepilogo = datiRiepilogoType;

        BigDecimal aliquotaIva = mapBT119();
        if (aliquotaIva != null) {
            vatBreakdown.getBT0119VatCategoryRate()
                    .add(new BT0119VatCategoryRate(aliquotaIva.doubleValue()));
        }

        NaturaType natura = mapBT118();
        if (natura != null) {
            vatBreakdown.getBT0118VatCategoryCode()
                    .add(new BT0118VatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(natura.value())));
        }

        return vatBreakdown;
    }

    private static NaturaType mapBT118() {
        return datiRiepilogo.getNatura();
    }

    private static BigDecimal mapBT119() {
        return datiRiepilogo.getAliquotaIVA();
    }
}
