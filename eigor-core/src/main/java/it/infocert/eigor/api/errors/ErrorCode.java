package it.infocert.eigor.api.errors;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

public class ErrorCode implements Serializable {

    /*
     * Where did the error occur?
     */
    @Nullable
    private final Location location;

    /*
     * What caused the error?
     */
    @Nullable
    private final Action action;

    /*
     * What error did occur?
     */
    @Nullable
    private final Error error;

    public ErrorCode(@Nullable Location location, @Nullable Action action, @Nullable Error error) {
        this.location = location;
        this.action = action;
        this.error = error;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @Nullable
    public Action getAction() {
        return action;
    }

    @Nullable
    public Error getError() {
        return error;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", location, action, error);
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
        CII_OUT
    }

    public enum Action {
        XSD_VALIDATION,
        SCH_VALIDATION,
        CIUS_SCH_VALIDATION,
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
