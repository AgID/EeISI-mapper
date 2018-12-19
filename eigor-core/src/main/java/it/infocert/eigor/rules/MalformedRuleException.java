package it.infocert.eigor.rules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.rules.Rule;

import java.util.List;
import java.util.Map;

public class MalformedRuleException extends EigorRuntimeException {

    private Map<String, String> invalidRules = Maps.newHashMap();
    private List<Rule> validRules = Lists.newArrayList();
    private final static ErrorCode.Error NAME = ErrorCode.Error.INVALID;

    public MalformedRuleException(String message) {
        super(ErrorMessage.builder().message(message).error(NAME).build());
    }

    public MalformedRuleException(String message, Throwable cause) {
        super(ErrorMessage.builder().message(message).error(NAME).build(), cause);
    }

    public MalformedRuleException(Throwable cause) {
        super(ErrorMessage.builder().message(cause.getMessage()).error(NAME).build(), cause);
    }

    public MalformedRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(ErrorMessage.builder().message(message).error(NAME).build(), cause, enableSuppression, writableStackTrace);
    }

    public MalformedRuleException(String message, Map<String, String> invalidRules, List<Rule> validRules) {
        super(ErrorMessage.builder().message(message).error(NAME).build());
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public MalformedRuleException(String message, Throwable cause, Map<String, String> invalidRules, List<Rule> validRules) {
        super(ErrorMessage.builder().message(message).error(NAME).build(), cause);
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public MalformedRuleException(Throwable cause, Map<String, String> invalidRules, List<Rule> validRules) {
        super(ErrorMessage.builder().message(cause.getMessage()).error(NAME).build(), cause);
        this.invalidRules = invalidRules;
        this.validRules = validRules;
    }

    public MalformedRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, String> invalidRules, List<Rule> validRules) {
        super(ErrorMessage.builder().message(message).error(NAME).build(), cause, enableSuppression, writableStackTrace);
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
