package it.infocert.eigor.api.configuration;

import static java.lang.String.valueOf;

public class MissingMandatoryPropertyException extends RuntimeException {

    private MissingMandatoryPropertyException(String message) {
        super(message);
    }

    public static MissingMandatoryPropertyException missingProperty(String missingProperty){
        return new MissingMandatoryPropertyException( String.format("Mandatory configuration property '%s' is missing.", valueOf(missingProperty)) );
    }

}
