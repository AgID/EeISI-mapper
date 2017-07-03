package it.infocert.eigor.api.errors;

import java.util.Objects;

/**
* Enum representing the possible error codes Eigor can emit
*/
public enum ErrorCodes {

    TO_CEN_SCHEMATRON_ERROR("1", "10", "99"),
    TO_CEN_XSD_ERROR("1", "10", "77");

    private final String location;
    private final String type;
    private final String code;

    ErrorCodes(String location, String type, String code) {
        this.location = location;
        this.type = type;
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", location, type, code);
    }

    /**
    * Return the ErrorCode mathing the given parameters
    * @param location
    * @param type
    * @param code
    * @return the error code that match the given parameters
    */
    public static ErrorCodes retrieveErrorCode(String location, String type, String code) {

        for (ErrorCodes errorCodes : ErrorCodes.values()) {
            if (Objects.equals(errorCodes.code, code) && Objects.equals(errorCodes.type, type) && Objects.equals(errorCodes.location, location)) {
                return errorCodes;
            }
        }
        return null;
    }
}

