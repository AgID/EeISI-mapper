package it.infocert.eigor.rules;

import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class RuleOutcomeAsConversionIssueAdapterTest {

    @Test
    public void shouldAdaptTheRule() {

        // given
        RuleOutcome ruleOutcome = RuleOutcome.newErrorOutcome("an error");
        RuleOutcomeAsConversionIssueAdapter sut = new RuleOutcomeAsConversionIssueAdapter(
                ruleOutcome
        );

        // then
        assertThat(sut.getCause(), nullValue());
        assertThat(sut.getMessage(), is("an error"));
        assertThat(sut.isError(), is(true));
        assertThat(sut.isWarning(), is(false));

    }

}
