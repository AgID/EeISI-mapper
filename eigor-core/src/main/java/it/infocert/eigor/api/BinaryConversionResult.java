package it.infocert.eigor.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link ConversionResult result of a conversion} that provides a binary result.
 */
public class BinaryConversionResult extends ConversionResult<byte[]> {

    /**
     * A successfull conversion.
     */
    public BinaryConversionResult(byte[] result) {
        this(result, new ArrayList<IConversionIssue>());
    }

    /**
     * Immutable object constructed with data result and not null but possible empty array of issues
     * The other flags, successful and hasResult are set automatically based on the result and issues parameters
     */
    public BinaryConversionResult(byte[] result, List<IConversionIssue> errors) {
        super(errors, result);
        Objects.requireNonNull(errors);
    }

    @Override
    public boolean hasResult() {
        return (result != null && result.length > 0);
    }
}
