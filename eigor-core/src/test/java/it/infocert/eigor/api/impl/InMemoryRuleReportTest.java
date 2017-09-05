package it.infocert.eigor.api.impl;

import it.infocert.eigor.model.core.rules.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.*;
import static it.infocert.eigor.model.core.rules.RuleOutcome.newOutcome;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InMemoryRuleReportTest {
    
    @Test public void shouldHaveFailureIfItContainsFailedOrErrorOutcomes(){

        // given
        InMemoryRuleReport sut = new InMemoryRuleReport();

        Rule aRule = Mockito.mock(Rule.class);

        sut.store(newOutcome(FAILED, "outcome1"), aRule);
        sut.store(newOutcome(SUCCESS, "outcome2"), aRule);
        sut.store(newOutcome(UNAPPLICABLE, "outcome3"), aRule);

        // then
        assertTrue(sut.hasFailures());

    }

    @Test public void shouldNotHaveFailureIfItDoesNotContainFailedOrErrorOutcomes(){

        // given
        InMemoryRuleReport sut = new InMemoryRuleReport();

        Rule aRule = Mockito.mock(Rule.class);

        sut.store(newOutcome(SUCCESS, "outcome1"), aRule);
        sut.store(newOutcome(SUCCESS, "outcome2"), aRule);
        sut.store(newOutcome(UNAPPLICABLE, "outcome3"), aRule);

        // then
        assertFalse(sut.hasFailures());

    }

}