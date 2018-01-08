package it.infocert.eigor.converter.fattpa2cen.converters;

import it.infocert.eigor.api.conversion.FromStringTypeConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.converter.fattpa2cen.models.NaturaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;

public class ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter extends FromStringTypeConverter<Untdid5305DutyTaxFeeCategories>{

    private ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter() {
    }

    @Override
    public Untdid5305DutyTaxFeeCategories convert(String natura) {

        NaturaType natura1 = getNatura(natura);
        if (natura1 != null) {
            switch (natura1) {
                case N_3:
                    return Untdid5305DutyTaxFeeCategories.Z;
                case N_4:
                    return Untdid5305DutyTaxFeeCategories.E;
                case N_2:
                    return Untdid5305DutyTaxFeeCategories.G;
                default:
                    return Untdid5305DutyTaxFeeCategories.S;
            }
        }
        return null;
    }

    @Override
    public Class<Untdid5305DutyTaxFeeCategories> getTargetClass() {
        return Untdid5305DutyTaxFeeCategories.class;
    }

    private NaturaType getNatura(String natura) {
        try {
            return NaturaType.fromValue(natura);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static TypeConverter<String, Untdid5305DutyTaxFeeCategories> newConverter() {
        return new ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter();
    }
}
