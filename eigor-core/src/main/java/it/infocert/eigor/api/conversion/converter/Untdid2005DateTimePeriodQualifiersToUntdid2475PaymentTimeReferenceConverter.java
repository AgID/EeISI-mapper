package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionBetweenTypesFailedException;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;
import it.infocert.eigor.model.core.enums.Untdid2475PaymentTimeReference;

public class Untdid2005DateTimePeriodQualifiersToUntdid2475PaymentTimeReferenceConverter implements TypeConverter<Untdid2005DateTimePeriodQualifiers, Untdid2475PaymentTimeReference> {

    private Untdid2005DateTimePeriodQualifiersToUntdid2475PaymentTimeReferenceConverter() {
    }

    @Override
    public Untdid2475PaymentTimeReference convert(Untdid2005DateTimePeriodQualifiers qualifier) throws ConversionFailedException {

        switch (qualifier) {
            case Code3:
                return Untdid2475PaymentTimeReference.Code5;
            case Code35:
                return Untdid2475PaymentTimeReference.Code29;
            case Code432:
                return Untdid2475PaymentTimeReference.Code72;
            default:
                throw new ConversionBetweenTypesFailedException(Untdid2005DateTimePeriodQualifiers.class, Untdid2475PaymentTimeReference.class, qualifier);
        }
    }

    @Override
    public Class<Untdid2475PaymentTimeReference> getTargetClass() {
        return Untdid2475PaymentTimeReference.class;
    }

    @Override
    public Class<Untdid2005DateTimePeriodQualifiers> getSourceClass() {
        return Untdid2005DateTimePeriodQualifiers.class;
    }

    public static TypeConverter<Untdid2005DateTimePeriodQualifiers, Untdid2475PaymentTimeReference> newConverter() {
        return new Untdid2005DateTimePeriodQualifiersToUntdid2475PaymentTimeReferenceConverter();
    }
}
