package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.fattpa.commons.models.NaturaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;

import static it.infocert.eigor.fattpa.commons.models.NaturaType.*;

/**
 * Instantiate it with Untdid5305DutyTaxFeeCategoriesToNaturaType{@link #newConverter()}
 */
public class Untdid5305DutyTaxFeeCategoriesToNaturaType implements TypeConverter<Untdid5305DutyTaxFeeCategories, NaturaType> {

    Untdid5305DutyTaxFeeCategoriesToNaturaType() {
    }

    @Override
    public NaturaType convert(Untdid5305DutyTaxFeeCategories code) {
        switch (code) {
            case E:
                return N_4;
            case G:
            case K:
                return N_3;
            case AE:
                return N_6;
            case O:
                return N_2;
            default:
                return null;
        }
    }

    @Override
    public Class<NaturaType> getTargetClass() {
        return NaturaType.class;
    }

    @Override
    public Class<Untdid5305DutyTaxFeeCategories> getSourceClass() {
        return Untdid5305DutyTaxFeeCategories.class;
    }

    public static Untdid5305DutyTaxFeeCategoriesToNaturaType newConverter() {
        return new Untdid5305DutyTaxFeeCategoriesToNaturaType();
    }
}
