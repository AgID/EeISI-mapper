package it.infocert.eigor.converter.cen2ubl.converters;

import it.infocert.eigor.api.conversion.ToStringTypeConverter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

public class Untdid4461PaymentMeansCodeToItalianCodeString extends ToStringTypeConverter<Untdid4461PaymentMeansCode>{
    @Override
    public String convert(Untdid4461PaymentMeansCode paymentMeansCode) {
        switch (paymentMeansCode) {
            case Code30:
                return "MP05";
            case Code20:
            case Code92:
                return "MP02";
            case Code10:
                return "MP01";
            case Code21:
            case Code22:
            case Code23:
            case Code91:
                return "MP03";
            case Code60:
                return "MP06";
            case Code70:
                return "MP12";
            case Code15:
                return "MP15";
            case Code50:
                return "MP18";
            case Code1:
                return "MP22";
            default:
                return "";
        }
    }

    @Override
    public Class<Untdid4461PaymentMeansCode> getSourceClass() {
        return Untdid4461PaymentMeansCode.class;
    }
}
