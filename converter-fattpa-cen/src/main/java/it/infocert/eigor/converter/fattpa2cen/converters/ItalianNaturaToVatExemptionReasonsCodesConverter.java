package it.infocert.eigor.converter.fattpa2cen.converters;

import it.infocert.eigor.api.conversion.converter.FromStringTypeConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.fattpa.commons.models.NaturaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.VatExemptionReasonsCodes;

public class ItalianNaturaToVatExemptionReasonsCodesConverter extends FromStringTypeConverter<VatExemptionReasonsCodes>{

    private ItalianNaturaToVatExemptionReasonsCodesConverter() {
    }

    @Override
    public VatExemptionReasonsCodes convert(String natura) {

        NaturaType natura1 = getNatura(natura);
        if (natura1 != null) {
            switch (natura1) {
                case N_1:
                case N_2:
                case N_5:
                    return VatExemptionReasonsCodes.vatex_eu_g;
                case N_3:
                    return VatExemptionReasonsCodes.vatex_eu_g;
                case N_4:
                    return VatExemptionReasonsCodes.vatex_eu_g;
                case N_6:
                    return VatExemptionReasonsCodes.vatex_eu_g;
                case N_7:
                    return VatExemptionReasonsCodes.vatex_eu_g;
                default:
                    return VatExemptionReasonsCodes.vatex_eu_g;
            }
        }
        return null;
    }

    @Override
    public Class<VatExemptionReasonsCodes> getTargetClass() {
        return VatExemptionReasonsCodes.class;
    }

    private NaturaType getNatura(String natura) {
        try {
            return NaturaType.fromValue(natura);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static TypeConverter<String, VatExemptionReasonsCodes> newConverter() {
        return new ItalianNaturaToVatExemptionReasonsCodesConverter();
    }
}
