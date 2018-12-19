package it.infocert.eigor.converter.fattpa2cen.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

import static it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode.*;

public class ItalianCodeStringToUntdid4461PaymentMeansCode implements TypeConverter<String,Untdid4461PaymentMeansCode> {

    ItalianCodeStringToUntdid4461PaymentMeansCode() {
    }

    @Override
    public Untdid4461PaymentMeansCode convert(String stringCode) {
        switch (stringCode) {
            case "MP02":
                return Code20;
            case "MP03":
                return Code21;
            case "MP06":
                return Code60;
            case "MP12":
                return Code70;
            case "MP15":
                return Code15;
            case "MP18":
                return Code50;
            case "MP22":
                return Code1;
            case "MP01":
                return Code10;
            case "MP05":
                return Code30;
            default:
                return Code1;
        }
    }

    @Override
    public Class<Untdid4461PaymentMeansCode> getTargetClass() {
        return Untdid4461PaymentMeansCode.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    public static TypeConverter<String,Untdid4461PaymentMeansCode> newConverter() {
        return new ItalianCodeStringToUntdid4461PaymentMeansCode();
    }
}
