package it.infocert.eigor.api.conversion.converter;


import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

import java.util.function.Predicate;

public class StringToUntdid4461PaymentMeansCode extends FilteringEnumConversion<String,Untdid4461PaymentMeansCode> {

    private StringToUntdid4461PaymentMeansCode() {
        super(Untdid4461PaymentMeansCode.class);
    }

    @Override
    protected Predicate<Untdid4461PaymentMeansCode> buildFilter(final String value) {
        return untdid4461PaymentMeansCode -> {
            String code = String.valueOf( untdid4461PaymentMeansCode.getCode() );
            return code.equals(value);
        };
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    public static TypeConverter<String, Untdid4461PaymentMeansCode> newConverter() {
        return new StringToUntdid4461PaymentMeansCode();
    }
}
