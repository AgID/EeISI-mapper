package it.infocert.eigor.api.utils;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorMessage;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Various utility methods that alows you to handle {@link it.infocert.eigor.api.ConversionResult results}. */
public final class ResultUtils {

    public static final String toString(ConversionResult<?> result) {

        List<IConversionIssue> warns = getWarningIssues(result);
        List<IConversionIssue> errors = getErrorIssues(result);


        StringBuilder sb = new StringBuilder();
        sb.append( String.format("%3$d errors, %2$d warnings, result of type '%1$s'\n", result.getResult().getClass().getName(), warns.size(), errors.size()) );
        if(!errors.isEmpty()) {
            sb.append("\nerrors:\n");
            sb.append(prettyPrintIssues(errors));
        }
        if(!warns.isEmpty()) {
            sb.append("\nwarnings:\n");
            sb.append(prettyPrintIssues(warns));
        }
        return sb.toString();

    }

    public static String prettyPrintIssues(List<IConversionIssue> issues) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<issues.size(); i++) {
            IConversionIssue error = issues.get(i);
            ErrorMessage errorMessage = error.getErrorMessage();
            sb.append(String.format("%d) %s\n", i+1, errorMessage.getErrorCode()));
            sb.append(String.format("%s\n\n", errorMessage.getMessage()));
        }
        return sb.toString();
    }

    public static List<IConversionIssue> getWarningIssues(ConversionResult<?> result) {
        return findIssues(result, issue -> issue.isWarning());
    }

    public static List<IConversionIssue> getErrorIssues(ConversionResult<?> result) {
        return findIssues(result, issue -> issue.isError());
    }

    public static List<IConversionIssue> findIssues(ConversionResult<?> result, Predicate<IConversionIssue> iConversionIssuePredicate) {
        return result.getIssues().stream()
                    .filter(iConversionIssuePredicate)
                    .collect(Collectors.toList());
    }

    private ResultUtils() {
        throw new UnsupportedOperationException("Should not be instantiated");
    }

}
