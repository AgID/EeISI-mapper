package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;

public class Untdid2005DateTimePeriodQualifiersToStringConverter implements TypeConverter<Untdid2005DateTimePeriodQualifiers, String> {

    Untdid2005DateTimePeriodQualifiersToStringConverter() {
    }

    @Override
    public String convert(Untdid2005DateTimePeriodQualifiers qualifiers) {
        return String.valueOf(qualifiers.getCode());
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public Class<Untdid2005DateTimePeriodQualifiers> getSourceClass() {
        return Untdid2005DateTimePeriodQualifiers.class;
    }

    public static TypeConverter<Untdid2005DateTimePeriodQualifiers, String> newConverter() {
        return new Untdid2005DateTimePeriodQualifiersToStringConverter();
    }
}
