package it.infocert.eigor.api.conversion.converter;

import it.infocert.eigor.api.conversion.ConversionBetweenTypesFailedException;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;
import it.infocert.eigor.model.core.enums.Untdid2475PaymentTimeReference;

public class Untdid2475PaymentTimeReferenceConverterToUntdid2005DateTimePeriodQualifiers implements TypeConverter<Untdid2475PaymentTimeReference, Untdid2005DateTimePeriodQualifiers> {

    private Untdid2475PaymentTimeReferenceConverterToUntdid2005DateTimePeriodQualifiers() {
    }

    @Override
    public Untdid2005DateTimePeriodQualifiers convert(Untdid2475PaymentTimeReference qualifier) throws ConversionFailedException {

        switch (qualifier) {
            case Code5:
                return Untdid2005DateTimePeriodQualifiers.Code3;
            case Code29:
                return Untdid2005DateTimePeriodQualifiers.Code35;
            case Code72:
                return Untdid2005DateTimePeriodQualifiers.Code432;
            default:
                throw new ConversionBetweenTypesFailedException(Untdid2475PaymentTimeReference.class, Untdid2005DateTimePeriodQualifiers.class, qualifier);
        }
    }

    @Override
    public Class<Untdid2005DateTimePeriodQualifiers> getTargetClass() {
        return Untdid2005DateTimePeriodQualifiers.class;
    }

    @Override
    public Class<Untdid2475PaymentTimeReference> getSourceClass() {
        return Untdid2475PaymentTimeReference.class;
    }

    public static TypeConverter<Untdid2475PaymentTimeReference, Untdid2005DateTimePeriodQualifiers> newConverter() {
        return new Untdid2475PaymentTimeReferenceConverterToUntdid2005DateTimePeriodQualifiers();
    }
}
