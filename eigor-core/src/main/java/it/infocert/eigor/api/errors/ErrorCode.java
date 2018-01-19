package it.infocert.eigor.api.errors;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

public class ErrorCode implements Serializable {

    /*
     * Where did the error occur?
     */
    @Nullable
    private final String location;

    /*
     * What caused the error?
     */
    @Nullable
    private final String action;

    /*
     * What error did occur?
     */
    @Nullable
    private final String error;

    public ErrorCode(@Nullable String location, @Nullable String action, @Nullable String error) {
        this.location = location;
        this.action = action;
        this.error = error;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    @Nullable
    public String getAction() {
        return action;
    }

    @Nullable
    public String getError() {
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
}
