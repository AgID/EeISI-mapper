package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid2475PaymentTimeReference;

public class Untdid2475DateTimePeriodQualifiersToItalianCodeStringConverter implements TypeConverter<Untdid2475PaymentTimeReference, String> {
    @Override
    public String convert(Untdid2475PaymentTimeReference qualifiers) {
        switch (qualifiers) {
            case Code5:
                return "I";
            case Code29:
            case Code72:
                return "D";
            default:
                return null;
        }
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public Class<Untdid2475PaymentTimeReference> getSourceClass() {
        return Untdid2475PaymentTimeReference.class;
    }

    Untdid2475DateTimePeriodQualifiersToItalianCodeStringConverter() {
    }

    public static TypeConverter<Untdid2475PaymentTimeReference, String> newConverter() {
        return new Untdid2475DateTimePeriodQualifiersToItalianCodeStringConverter();
    }
}
