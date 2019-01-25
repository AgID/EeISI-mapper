package it.infocert.eigor.api.utils;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorMessage;

import java.util.List;
import java.util.stream.Collectors;

public class ResultUtils {

    public static final String toString(ConversionResult<?> result) {

        List<IConversionIssue> issues = result.getIssues();

        List<IConversionIssue> warns = issues.stream()
                .filter(issue -> issue.isWarning())
                .collect(Collectors.toList());

        List<IConversionIssue> errors = issues.stream()
                .filter(issue -> issue.isError())
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();

        sb.append( String.format("%3$d errors, %2$d warnings, result of type '%1$s'\n", result.getResult().getClass().getName(), warns.size(), errors.size()) );

        if(!errors.isEmpty()) {
            sb.append("\nerrors:\n");
            for(int i=0; i<errors.size(); i++) {

                IConversionIssue error = errors.get(i);

                ErrorMessage errorMessage = error.getErrorMessage();

                sb.append(String.format("%d) %s\n", i+1, errorMessage.getErrorCode()));
                sb.append(String.format("%s\n\n", errorMessage.getMessage()));

            }
        }

        if(!warns.isEmpty()) {
            sb.append("\nwarnings:\n");
            for (IConversionIssue warn : warns) {
                sb.append(String.format("%s\n", warn));
            }
        }

        return sb.toString();

    }

}
