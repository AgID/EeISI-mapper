package it.infocert.eigor.api.errors;

import java.util.Objects;

public class ErrorCode {

    /*
     * Where did the error occured?
     */
    private final String location;

    /*
     * What caused the error?
     */
    private final String action;

    /*
     * What error did occur?
     */
    private final String error;

    public ErrorCode(String location, String action, String error) {
        this.location = location;
        this.action = action;
        this.error = error;
    }

    public String getLocation() {
        return location;
    }

    public String getAction() {
        return action;
    }

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
