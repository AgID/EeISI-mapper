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
    protected boolean hasErrors;
    protected List<IConversionIssue> issues;

    /**
     * Immutable object constructed with data result and not null but possible empty array of issues
     * The other flags, successful and hasResult are set automatically based on the result and issues parameters
     *
     * @param result
     * @param issues
     */
    public ConversionResult(List<IConversionIssue> issues, R result) {
        this.issues = Collections.unmodifiableList(issues);
        this.result = result;
        for (IConversionIssue issue : issues) {
            if (issue.isError()) {
                hasErrors = true;
                break;
            }
        }
    }

    /**
     * A conversion without issues.
     */
    public ConversionResult(R result) {
        this.result = result;
        this.issues = Collections.unmodifiableList(new ArrayList<IConversionIssue>());
    }

    /**
     * @return TRUE if conversion completed successfully, meaning issue list is empty and result (if any) is valid.
     */
    public boolean isSuccessful() {
        return issues.isEmpty();
    }

    /**
     * @return TRUE if the issue list is contains one or more ConversionIssue that is an error.
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * @return List of issues caught during conversion.
     */
    public List<IConversionIssue> getIssues() {
        return issues;
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
