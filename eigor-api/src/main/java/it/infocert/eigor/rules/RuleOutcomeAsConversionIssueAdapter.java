package it.infocert.eigor.rules;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class RuleOutcomeAsConversionIssueAdapter implements IConversionIssue {

    private final RuleOutcome ruleOutcome;

    public RuleOutcomeAsConversionIssueAdapter(RuleOutcome ruleOutcome) {
        RuleOutcome.Outcome outcome = checkNotNull( ruleOutcome ).outcome();
        checkArgument(outcome == RuleOutcome.Outcome.FAILED || outcome == RuleOutcome.Outcome.ERROR,
                "The provided outcome '%s' cannot be a successfull outcome if you want it to be treated as an issue.", ruleOutcome);
        this.ruleOutcome = checkNotNull(ruleOutcome);
    }

    @Override
    public String getMessage() {
        return ruleOutcome.description();
    }

    @Override
    public Exception getCause() {
        return null;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isWarning() {
        return false;
    }
    
}
