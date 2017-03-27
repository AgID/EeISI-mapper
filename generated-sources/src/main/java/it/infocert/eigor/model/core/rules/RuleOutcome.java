package it.infocert.eigor.model.core.rules;

import com.google.common.base.Preconditions;

public class RuleOutcome {

    public enum Outcome {
        /** The invoice successfully passed the checks of this rule. */
        SUCCESS,
        /** The rule cannot be verified against the invoice. */
        UNAPPLICABLE,
        /** The invoice failed to pass the rule. */
        FAILED
    }

    private final Outcome outcome;
    private final String description;

    private RuleOutcome(Outcome success, String description) {
        this.outcome = Preconditions.checkNotNull( success );
        this.description = Preconditions.checkNotNull( description );
    }

    /**
     * @param outcome
     * @param stringFormat
     * @param params
     */
    public static RuleOutcome newOutcome(Outcome outcome, String stringFormat, Object... params) {
        return new RuleOutcome(
                outcome, String.format(stringFormat, params)
        );
    }

    public Outcome outcome() {
        return outcome;
    }

    public String description() {
        return description;
    }
}
