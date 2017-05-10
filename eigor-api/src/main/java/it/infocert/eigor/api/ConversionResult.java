package it.infocert.eigor.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of a conversion executed by {@link FromCenConversion cen => xxx} converters
 * and {@link ToCenConversion xxx => cen} converters.
 */
public class ConversionResult<R> {

    protected final R result;
    protected boolean successful;
    protected List<Exception> errors;


    public ConversionResult(List<Exception> errors, R result) {
        this.errors = Collections.unmodifiableList(errors);
        this.result = result;
    }

    public ConversionResult(R result) {
        this.result = result;
        this.errors = Collections.unmodifiableList(new ArrayList<>());
    }

    /**
     * @return TRUE if conversion completed successfully, meaning error list is empty and result (if any) is valid.
     */
    public boolean isSuccessful() {
        return errors.isEmpty();
    }

    /**
     * @return List of exceptions caught during conversion.
     */
    public List<Exception> getErrors() {
        return errors;
    }

    /**
     * @return TRUE if the conversion was able to produce some result.
     */
    public boolean hasResult() {
        return result != null;
    }

    /**
     * @return The possibly invalid result.
     */
    public R getResult() {
        return result;
    }
}
