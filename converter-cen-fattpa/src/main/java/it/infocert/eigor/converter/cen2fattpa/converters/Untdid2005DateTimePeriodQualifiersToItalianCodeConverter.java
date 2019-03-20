package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.fattpa.commons.models.EsigibilitaIVAType;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;

public class Untdid2005DateTimePeriodQualifiersToItalianCodeConverter implements TypeConverter<Untdid2005DateTimePeriodQualifiers, EsigibilitaIVAType> {
    @Override
    public EsigibilitaIVAType convert(Untdid2005DateTimePeriodQualifiers qualifiers) {
        switch (qualifiers) {
            case Code3:
            case Code35:
                return EsigibilitaIVAType.I;
            case Code432:
                return EsigibilitaIVAType.D;
            default:
                return null;
        }
    }

    @Override
    public Class<EsigibilitaIVAType> getTargetClass() {
        return EsigibilitaIVAType.class;
    }

    @Override
    public Class<Untdid2005DateTimePeriodQualifiers> getSourceClass() {
        return Untdid2005DateTimePeriodQualifiers.class;
    }

    Untdid2005DateTimePeriodQualifiersToItalianCodeConverter() {
    }

    public static TypeConverter<Untdid2005DateTimePeriodQualifiers, EsigibilitaIVAType> newConverter() {
        return new Untdid2005DateTimePeriodQualifiersToItalianCodeConverter();
    }
}
