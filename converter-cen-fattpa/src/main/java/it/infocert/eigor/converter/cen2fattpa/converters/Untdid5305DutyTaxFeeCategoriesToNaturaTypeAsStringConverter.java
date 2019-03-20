package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.ToStringTypeConverter;
import it.infocert.eigor.fattpa.commons.models.NaturaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;


public class Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverter extends ToStringTypeConverter<Untdid5305DutyTaxFeeCategories> {

    private Untdid5305DutyTaxFeeCategoriesToNaturaType mainConverter = Untdid5305DutyTaxFeeCategoriesToNaturaType.newConverter();

    @Override
    public String convert(Untdid5305DutyTaxFeeCategories code) {
        NaturaType convert = mainConverter.convert(code);
        return convert != null ? convert.value() : null;
    }

    @Override
    public Class<Untdid5305DutyTaxFeeCategories> getSourceClass() {
        return Untdid5305DutyTaxFeeCategories.class;
    }

    Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverter() {
    }

    public static Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverter newConverter() {
        return new Untdid5305DutyTaxFeeCategoriesToNaturaTypeAsStringConverter();
    }
}
