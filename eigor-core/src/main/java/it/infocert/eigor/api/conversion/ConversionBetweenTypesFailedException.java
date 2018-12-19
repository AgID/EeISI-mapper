package it.infocert.eigor.api.conversion;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConversionBetweenTypesFailedException extends ConversionFailedException {

    private final Class sourceType;
    private final Class destinationType;
    private final Object offendingValue;

    public ConversionBetweenTypesFailedException(Class sourceType, Class destinationType, Object offendingValue) {
        super();
        this.sourceType = checkNotNull( sourceType );
        this.destinationType = checkNotNull( destinationType );
        this.offendingValue = offendingValue;
    }

    @Override public String toString() {
        return String.format("It was impossible to convert value %s from type '%s' to type '%s'.", offendingValue!=null ? offendingValue : "<null>", sourceType.getName(), destinationType.getName());
    }

}
