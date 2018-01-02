package it.infocert.eigor.api.conversion;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConversionFailedException extends Exception {

    private final Class sourceType;
    private final Class destinationType;
    private final Object offendingValue;

    public ConversionFailedException(Class sourceType, Class destinationType, Object offendingValue) {
        super();
        this.sourceType = checkNotNull( sourceType );
        this.destinationType = checkNotNull( destinationType );
        this.offendingValue = checkNotNull( offendingValue );
    }

    @Override public String toString() {
        return String.format("It was impossible to convert value '%s' from type '%s' to type '%s'.", offendingValue, sourceType.getName(), destinationType.getName());
    }
}
