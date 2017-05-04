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

    /**
     * @return TRUE if error list is empty and result is valid
     */
    public Boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    /**
     * @return TRUE if atleast some result XML was generated
     */
    public Boolean hasResult() {
        return hasResult;
    }

    /**
     * @return List of exceptions caught during conversion
     */
    public List<Exception> getErrors() {
        return errors;
    }

    /**
     * @return raw XML as Byte Array
     */
    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
        hasResult = true;
    }
}
