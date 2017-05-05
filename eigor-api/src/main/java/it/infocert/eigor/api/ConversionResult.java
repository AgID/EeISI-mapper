package it.infocert.eigor.api;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ConversionResult {

    private boolean successful;
    private boolean hasResult;
    private List<Exception> errors;
    private final byte[] result;

    /**
     * Immutable object constructed with data result and not null but possible empty array of errors
     * The other flags, successful and hasResult are set automatically based on the result and errors parameters
     *
     * @param result
     * @param errors
     */
    public ConversionResult(byte[] result, List<Exception> errors) {
        Objects.requireNonNull(errors);

        this.result = result;
        this.errors = errors;
        if (result != null && result.length > 0) {
            hasResult = true;
            if (errors.isEmpty()) {
                successful = true;
            }
        }
    }

    /**
     * @return TRUE if error list is empty and result is valid
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * @return TRUE if atleast some result XML was generated
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     * @return List of exceptions caught during conversion
     */
    public List<Exception> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * @return raw XML as Byte Array
     */
    public byte[] getResult() {
        return result;
    }
}
