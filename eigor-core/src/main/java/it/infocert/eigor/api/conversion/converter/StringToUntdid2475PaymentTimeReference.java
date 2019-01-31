package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.model.core.enums.Untdid2475PaymentTimeReference;

public class StringToUntdid2475PaymentTimeReference extends FromStringTypeConverter<Untdid2475PaymentTimeReference> {

    private StringToUntdid2475PaymentTimeReference() {
    }

    @Override
    public Untdid2475PaymentTimeReference convert(String s) {
        return Untdid2475PaymentTimeReference.fromCode(s);
    }

    @Override
    public Class<Untdid2475PaymentTimeReference> getTargetClass() {
        return Untdid2475PaymentTimeReference.class;
    }

    public static TypeConverter<String, Untdid2475PaymentTimeReference> newConverter() {
        return new StringToUntdid2475PaymentTimeReference();
    }
}
