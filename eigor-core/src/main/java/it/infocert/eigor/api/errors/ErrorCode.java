package it.infocert.eigor.api.errors;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.Objects;

public class ErrorCode implements Serializable {

    /*
     * Where did the error occur?
     */
    private final Location location;

    /*
     * What caused the error?
     */
    private final Action action;

    /*
     * What error did occur?
     */
    private final Error error;

    public ErrorCode(Location location, Action action, Error error) {
        this.location = Preconditions.checkNotNull(location, "Cannot build an error code without the Location tag");
        this.action = Preconditions.checkNotNull(action, "Cannot build an error code without the Action tag");
        this.error = Preconditions.checkNotNull(error, "Cannot build an error code without the Error tag");
    }

    public Location getLocation() {
        return location;
    }

    public Action getAction() {
        return action;
    }

    public Error getError() {
        return error;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", location.name(), action.name(), error.name());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ErrorCode && Objects.equals(this.toString(), o.toString());
    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    public enum Location {
        UBL_IN,
        UBL_OUT,
        FATTPA_IN,
        FATTPA_OUT,
        CII_IN,
        CII_OUT,
        UBLCN_IN,
        UBLCN_OUT,
        CSVCEN_IN,
        XMLCEN_IN,
        XMLCEN_OUT
    }

    public enum Action {
        XSD_VALIDATION,
        SCH_VALIDATION,
        CIUS_SCH_VALIDATION,
        CONFIG_VALIDATION,
        CONFIGURED_MAP,
        HARDCODED_MAP,
        XML_PARSING,
        GENERIC
    }

    public enum Error {
        INVALID,
        ILLEGAL_VALUE,
        MISSING_VALUE
    }
}
