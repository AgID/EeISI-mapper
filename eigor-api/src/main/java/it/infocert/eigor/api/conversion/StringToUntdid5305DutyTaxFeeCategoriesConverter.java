package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;

import java.util.Arrays;

public class StringToUntdid5305DutyTaxFeeCategoriesConverter implements TypeConverter<String, Untdid5305DutyTaxFeeCategories> {

    @Override public Untdid5305DutyTaxFeeCategories convert(String s) {
        try {
            return Untdid5305DutyTaxFeeCategories.valueOf(s);
        } catch (IllegalArgumentException e) {

        }

        return Arrays
                .stream(Untdid5305DutyTaxFeeCategories.values())
                .filter( c -> c.getShortDescritpion().equalsIgnoreCase(s) )
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
