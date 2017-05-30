package it.infocert.eigor.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link ConversionResult result of a conversion} that provides a binary result.
 */
public class BinaryConversionResult extends ConversionResult<byte[]> {

    protected final boolean hasResult;

    /**
     * A successfull conversion.
     */
    public BinaryConversionResult(byte[] result) {
        this(result, new ArrayList<Exception>());
    }

    /**
     * Immutable object constructed with data result and not null but possible empty array of errors
     * The other flags, successful and hasResult are set automatically based on the result and errors parameters
     */
    public BinaryConversionResult(byte[] result, List<Exception> errors) {
        super(errors, result);
        if (result != null && result.length > 0) {
            hasResult = true;
            if (errors.isEmpty()) {
                successful = true;
            }
        }else{
            hasResult = false;
        }
        Objects.requireNonNull(errors);
    }

    @Override
    public boolean hasResult() {
        return hasResult;
    }
}
