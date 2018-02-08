package it.infocert.eigor.api.utils;

import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.model.core.rules.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.FAILED;
import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.SUCCESS;
import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.UNAPPLICABLE;
import static it.infocert.eigor.model.core.rules.RuleOutcome.newOutcome;
import static org.junit.Assert.assertEquals;

public class RuleReportsTest {

    @Test public void shouldRecordAllRules() {

        // given
        InMemoryRuleReport sut = new InMemoryRuleReport();

        Rule aRule = Mockito.mock(Rule.class);

        sut.store(newOutcome(FAILED, "outcome1"), aRule);
        sut.store(newOutcome(SUCCESS, "outcome2"), aRule);
        sut.store(newOutcome(UNAPPLICABLE, "outcome3"), aRule);

        // when
        String s = RuleReports.dump(sut);

        // then
        String expected = "Outcome,Reason\r\n" + "FAILED,outcome1\r\n" + "SUCCESS,outcome2\r\n" + "UNAPPLICABLE,outcome3\r\n";
        assertEquals(expected, s);

    }

}