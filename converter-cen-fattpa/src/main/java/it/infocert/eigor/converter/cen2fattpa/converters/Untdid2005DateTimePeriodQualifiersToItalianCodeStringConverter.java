package it.infocert.eigor.converter.cen2fattpa.converters;

import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;

public class Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter implements TypeConverter<Untdid2005DateTimePeriodQualifiers, String> {
    @Override
    public String convert(Untdid2005DateTimePeriodQualifiers qualifiers) {
        switch (qualifiers) {
            case Code3:
                return "I";
            case Code35:
            case Code432:
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
    public Class<Untdid2005DateTimePeriodQualifiers> getSourceClass() {
        return Untdid2005DateTimePeriodQualifiers.class;
    }

    Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter() {
    }

    public static TypeConverter<Untdid2005DateTimePeriodQualifiers, String> newConverter() {
        return new Untdid2005DateTimePeriodQualifiersToItalianCodeStringConverter();
    }
}
