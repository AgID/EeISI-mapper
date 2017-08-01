package it.infocert.eigor.api.errors;

import it.infocert.eigor.api.IConversionIssue;

import java.util.ArrayList;
import java.util.List;

public class ConversionIssueErrorCodeMapper {
    private final String location;
    private final String action;

    public ConversionIssueErrorCodeMapper(String location) {
        this.location = location;
        action = null;
    }

    public ConversionIssueErrorCodeMapper(String location, String action) {
        this.location = location;
        this.action = action;
    }

    public IConversionIssue map(IConversionIssue issue) {
        ErrorMessage message = issue.getErrorMessage();
        Class<? extends Exception> aClass = issue.getCause().getClass();
        String error;
        if (aClass == Exception.class) {
            error = "Exception";
        } else {
            error = aClass.getSimpleName().replace("Exception", "");
        }
        message.updateErrorCode(location, action, error);
        return issue;
    }

    public List<IConversionIssue> mapAll(List<IConversionIssue> issues) {
        for (IConversionIssue issue : issues) {
            map(issue);
        }
        return issues;
    }
}
