package it.infocert.eigor.api.conversion;

import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;

public class StringToUntdid5305DutyTaxFeeCategoriesConverter extends FilteringEnumConversion<String, Untdid5305DutyTaxFeeCategories> {

    public StringToUntdid5305DutyTaxFeeCategoriesConverter(){
        super(Untdid5305DutyTaxFeeCategories.class);
    }

    @Override protected Filter<Untdid5305DutyTaxFeeCategories> buildFilter(final String value) {
        return new Filter<Untdid5305DutyTaxFeeCategories>() {
            @Override public boolean apply(Untdid5305DutyTaxFeeCategories c) {
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
