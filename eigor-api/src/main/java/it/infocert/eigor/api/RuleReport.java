package it.infocert.eigor.api;

import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import java.util.List;
import java.util.Map;

/**
 * Keep track of all the applied rules and their outcome.
 */
public interface RuleReport {
    void store(RuleOutcome ruleOutcome, Rule rule);
    boolean hasFailures();

    List<Map.Entry<RuleOutcome, Rule>> getErrorsAndFailures();
}
