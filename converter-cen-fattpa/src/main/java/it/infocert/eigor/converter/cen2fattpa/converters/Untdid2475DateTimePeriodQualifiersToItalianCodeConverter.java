package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.converter.cen2fattpa.models.EsigibilitaIVAType;
import it.infocert.eigor.model.core.enums.Untdid2475PaymentTimeReference;

public class Untdid2475DateTimePeriodQualifiersToItalianCodeConverter implements TypeConverter<Untdid2475PaymentTimeReference, EsigibilitaIVAType> {
    @Override
    public EsigibilitaIVAType convert(Untdid2475PaymentTimeReference qualifiers) {
        switch (qualifiers) {
            case Code5:
            case Code29:
                return EsigibilitaIVAType.I;
            case Code72:
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
    public Class<Untdid2475PaymentTimeReference> getSourceClass() {
        return Untdid2475PaymentTimeReference.class;
    }

    Untdid2475DateTimePeriodQualifiersToItalianCodeConverter() {
    }

    public static TypeConverter<Untdid2475PaymentTimeReference, EsigibilitaIVAType> newConverter() {
        return new Untdid2475DateTimePeriodQualifiersToItalianCodeConverter();
    }
}
