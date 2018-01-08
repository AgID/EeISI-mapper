package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;

public class StringToUntdid4461PaymentMeansCode extends FilteringEnumConversion<String,Untdid4461PaymentMeansCode> {

    StringToUntdid4461PaymentMeansCode() {
        super(Untdid4461PaymentMeansCode.class);
    }

    @Override
    protected Filter<Untdid4461PaymentMeansCode> buildFilter(final String value) {
        return new Filter<Untdid4461PaymentMeansCode>() {
            @Override
            public boolean apply(Untdid4461PaymentMeansCode untdid4461PaymentMeansCode) {
                String code = String.valueOf( untdid4461PaymentMeansCode.getCode() );
                return code.equals(value);
            }
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
