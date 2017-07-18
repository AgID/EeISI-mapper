package it.infocert.eigor.api;

/**
 * Represent a problem occurred during invoice conversion and transformation.
 */
public interface IConversionIssue {

    /**
     * The human readable description of this issue.
     */
    String getMessage();

    /**
     * The exception that caused this issue, if any.
     * @return The exception that caused this isssue if available, {@code null} otherwise.
     */
    Exception getCause();

    /**
     * Whether this issue should be considered as a terminal error, meaning an
     * issue that prevents the conversion to be completed.
     */
    boolean isError();

    /**
     * Whether this issue should be considered as a mere warning, meaning an
     * issue that does not prevent the completion of the conversion.
     */
    boolean isWarning();
}
