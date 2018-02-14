package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

public class Untdid4461PaymentMeansCodeToString extends ToStringTypeConverter<Untdid4461PaymentMeansCode> {

    Untdid4461PaymentMeansCodeToString() {
    }

    @Override
    public String convert(Untdid4461PaymentMeansCode paymentMeansCode) {
        return String.valueOf(paymentMeansCode.getCode());
    }

    @Override
    public Class<Untdid4461PaymentMeansCode> getSourceClass() {
        return Untdid4461PaymentMeansCode.class;
    }

    public static TypeConverter<Untdid4461PaymentMeansCode, String> newConverter() {
        return new Untdid4461PaymentMeansCodeToString();
    }
}
