package it.infocert.eigor.api.conversion.converter;


import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;

import java.util.function.Predicate;

public class StringToUntdid5305DutyTaxFeeCategoriesConverter extends FilteringEnumConversion<String, Untdid5305DutyTaxFeeCategories> {

    public static TypeConverter<String, Untdid5305DutyTaxFeeCategories> newConverter() {
        return new StringToUntdid5305DutyTaxFeeCategoriesConverter();
    }

    private StringToUntdid5305DutyTaxFeeCategoriesConverter(){
        super(Untdid5305DutyTaxFeeCategories.class);
    }

    @Override protected Predicate<Untdid5305DutyTaxFeeCategories> buildFilter(final String value) {
        return new Predicate<Untdid5305DutyTaxFeeCategories>() {
            @Override
            public boolean test(Untdid5305DutyTaxFeeCategories c) {
                return c.getShortDescritpion().equalsIgnoreCase(value);

            }

        };
    }

    @Override
    public Class<Untdid5305DutyTaxFeeCategories> getTargetClass() {
        return Untdid5305DutyTaxFeeCategories.class;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }


}
