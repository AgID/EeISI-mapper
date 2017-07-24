package it.infocert.eigor.model.core.rules;

import com.google.common.base.Preconditions;

public class RuleOutcome {

    public enum Outcome {
        /** The invoice successfully passed the checks of this rule. */
        SUCCESS,
        /** The rule cannot be verified against the invoice. */
        UNAPPLICABLE,
        /** The invoice failed to pass the rule. */
        FAILED,
        /** The rule cannot be evaluated because of a problem in the rule itself. */
        ERROR
    }

    private final Outcome outcome;
    private final String description;

    public static RuleOutcome newOutcome(Outcome outcome, String stringFormat, Object... params) {
        return new RuleOutcome(
                outcome, String.format(stringFormat, params)
        );
    }

    /**
     * @see Outcome#FAILED
     */
    public static RuleOutcome newFailedOutcome(String stringFormat, Object... params) {
        return newOutcome(Outcome.FAILED, stringFormat, params);
    }

    /**
     * @see Outcome#SUCCESS
     */
    public static RuleOutcome newSuccessOutcome(String stringFormat, Object... params) {
        return newOutcome(Outcome.SUCCESS, stringFormat, params);
    }

    /**
     * @see Outcome#UNAPPLICABLE
     */
    public static RuleOutcome newUnapplicableOutcome(String stringFormat, Object... params) {
        return newOutcome(Outcome.UNAPPLICABLE, stringFormat, params);
    }

    public static RuleOutcome newErrorOutcome(String stringFormat, Object... params) {
        return newOutcome(Outcome.ERROR, stringFormat, params);
    }

    private RuleOutcome(Outcome success, String description) {
        this.outcome = Preconditions.checkNotNull( success );
        this.description = Preconditions.checkNotNull( description );
    }

    public Outcome outcome() {
        return outcome;
    }

    public String description() {
        return description;
    }
}
