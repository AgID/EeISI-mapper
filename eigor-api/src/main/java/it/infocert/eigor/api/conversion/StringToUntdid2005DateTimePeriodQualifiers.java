package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;

public class StringToUntdid2005DateTimePeriodQualifiers extends FromStringTypeConverter<Untdid2005DateTimePeriodQualifiers> {

    @Override
    public Untdid2005DateTimePeriodQualifiers convert(String s) {
        return Untdid2005DateTimePeriodQualifiers.fromCode(s);
    }

    @Override
    public Class<Untdid2005DateTimePeriodQualifiers> getTargetClass() {
        return Untdid2005DateTimePeriodQualifiers.class;
    }
}
