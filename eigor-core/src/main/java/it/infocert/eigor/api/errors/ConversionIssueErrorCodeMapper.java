package it.infocert.eigor.api.errors;

import it.infocert.eigor.api.IConversionIssue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ConversionIssueErrorCodeMapper {
    private final String location;

    @Nullable
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
        Exception cause = issue.getCause();
        Class<? extends Exception> aClass;
        if (cause.getCause() == null) {
            aClass = cause.getClass();
        } else {
            aClass = (Class<? extends Exception>) cause.getCause().getClass();
        }
        String error;
        if (aClass == Exception.class) {
            error = "Exception";
        } else {
            error = aClass.getSimpleName().replace("Exception", "");
        }
//        message.updateErrorCode(location, action, error);
        return issue;
    }

    public List<IConversionIssue> mapAll(List<IConversionIssue> issues) {
        for (IConversionIssue issue : issues) {
            map(issue);
        }
        return issues;
    }
}
