package it.infocert.eigor.api;

import java.util.ArrayList;
import java.util.List;

public class ConversionResult {

    private Boolean successful;
    private Boolean hasResult;
    private List<Exception> errors;
    private byte[] result;

    public ConversionResult() {
        successful = false;
        hasResult = false;
        errors = new ArrayList<>();
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public Boolean hasResult() {
        return hasResult;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
        hasResult = true;
    }
}
