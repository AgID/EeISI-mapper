package it.infocert.eigor.rules;

import it.infocert.eigor.model.core.rules.Rule;

import java.util.List;
import java.util.Map;

public class MalformedRuleException extends RuntimeException{
    
    private Map<String, String> invalidRules;
    private List<Rule> validRules;

    public MalformedRuleException() {
    }

    public MalformedRuleException(String message) {
        super(message);
    }

    public MalformedRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedRuleException(Throwable cause) {
        super(cause);
    }

    public MalformedRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    public MalformedRuleException(String message, Map<String, String> invalidRules, List<Rule> validRules) {
        super(message);
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public MalformedRuleException(String message, Throwable cause, Map<String, String> invalidRules, List<Rule> validRules) {
        super(message, cause);
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public MalformedRuleException(Throwable cause, Map<String, String> invalidRules, List<Rule> validRules) {
        super(cause);
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public MalformedRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, String> invalidRules, List<Rule> validRules) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public Map<String, String> getInvalidRules() {
        return invalidRules;
    }

    public List<Rule> getValidRules() {
        return validRules;
    }
}
