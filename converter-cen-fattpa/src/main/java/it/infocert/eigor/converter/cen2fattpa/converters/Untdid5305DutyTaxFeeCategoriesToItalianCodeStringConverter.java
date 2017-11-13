package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.ToStringTypeConverter;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;


public class Untdid5305DutyTaxFeeCategoriesToItalianCodeStringConverter extends ToStringTypeConverter<Untdid5305DutyTaxFeeCategories> {
    @Override
    public String convert(Untdid5305DutyTaxFeeCategories code) {
        switch (code) {
            case Z:
                return "N3"; //TODO assert in which case this must be N_3 or N_7 (see code list mapping)
            case E:
                return "N4";
            case G:
                return "N2";
            case O:
                return "N2"; //TODO assert in which case this must be N_2 or N_1 (see code list mapping)
            default:
                return "";
        }
    }

    @Override
    public Class<Untdid5305DutyTaxFeeCategories> getSourceClass() {
        return Untdid5305DutyTaxFeeCategories.class;
    }
}
