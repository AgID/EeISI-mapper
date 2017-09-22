package it.infocert.eigor.converter.fattpa2cen.converters;

import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

public class ItalianCodeStringToUntdid4461PaymentMeansCode implements TypeConverter<String,Untdid4461PaymentMeansCode> {
    @Override
    public Untdid4461PaymentMeansCode convert(String stringCode) {
        switch (stringCode) {
            case "MP05":
                return Untdid4461PaymentMeansCode.Code30;
            default:
                return null;
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
}
