package it.infocert.eigor.model.core.rules;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class RuleOutcomeTest {

    @Test
    public void shouldCreateAnOutcomeInStringFormatStyle() {

        RuleOutcome outcome = RuleOutcome.newOutcome(RuleOutcome.Outcome.SUCCESS, "Hello %d times %s", 100, "John");
        assertThat( outcome.outcome(), is(RuleOutcome.Outcome.SUCCESS) );
        assertThat( outcome.description(), is("Hello 100 times John") );

    }

    @Test
    public void shouldCreateAnOutcomeWithFullString() {

        RuleOutcome outcome = RuleOutcome.newOutcome(RuleOutcome.Outcome.SUCCESS, "Hello John");
        assertThat( outcome.outcome(), is(RuleOutcome.Outcome.SUCCESS) );
        assertThat( outcome.description(), is("Hello John") );

    }

    @Test
    public void shouldCreateANotAppliedOutcome() {

        RuleOutcome outcome = RuleOutcome.newOutcome(RuleOutcome.Outcome.UNAPPLICABLE, "Hello John");
        assertThat( outcome.outcome(), is(RuleOutcome.Outcome.UNAPPLICABLE) );
        assertThat( outcome.description(), is("Hello John") );

    }

}