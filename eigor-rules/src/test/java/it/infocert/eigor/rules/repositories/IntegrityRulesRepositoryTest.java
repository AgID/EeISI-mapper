package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0027SellerName;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;
import it.infocert.eigor.rules.integrity.IteratingIntegrityRule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.SUCCESS;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class IntegrityRulesRepositoryTest {

    private Properties properties;
    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
        invoice = new BG0000Invoice();
    }

    @Test
    public void repositoryShouldReturnAListOfRulesFromProps() throws Exception {
        properties.put("br1.body", "${}");
        IntegrityRulesRepository repo = new IntegrityRulesRepository(properties);
        List<Rule> firstRun = repo.rules();
        assertNotNull(firstRun);
        assertFalse(firstRun.isEmpty());

        //assert lazy evaluation is working by calling it again
        List<Rule> secondRun = repo.rules();
        assertNotNull(secondRun);
        assertEquals(firstRun, secondRun);
    }

    @Test
    public void emptyPropertiesShouldLeadToEmptyRuleList() throws Exception {
        assertTrue(getRules().isEmpty());
    }

    @Test
    public void simpleRuleMustSucceed() throws Exception {
        properties.put("br1.body", "${!invoice.getBT0001InvoiceNumber().isEmpty()}");
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        assertOutcome(SUCCESS);
    }

    @Test
    public void wrongPropertyNameMustBreak() throws Exception {
        properties.put("br1", "");
        try {
            getRules();
            fail();
        } catch (MalformedRuleException ignored) {
        }
    }

    @Test
    public void iteratingRuleMustBeRegistered() throws Exception {
        properties.put("br1.items", "${invoice.getBG0004Seller().iterator()}");
        properties.put("br1.body", "${!item.getBT0027SellerName().isEmpty()}");

        List<Rule> rules = getRules();

        assertNotNull(rules);
        assertFalse(rules.isEmpty());
        assertThat(rules.get(0), instanceOf(IteratingIntegrityRule.class));
    }

    @Test
    public void iteratingRuleMustSucceed() throws Exception {
        properties.put("br1.items", "${invoice.getBG0004Seller().iterator()}");
        properties.put("br1.body", "${!item.getBT0027SellerName().isEmpty()}");

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        invoice.getBG0004Seller().add(seller);

        assertOutcome(SUCCESS);
    }

    @Test
    public void assertThatAnExceptionIsThrownWhenARuleIsMalformed() throws Exception {
        properties.put("br1.items", "${invoice.getBG0004Seller().iterator()}");
        properties.put("br1.body", "${!item.getBT0027SellerName().isEmpty()}}");
        IntegrityRulesRepository repo = new IntegrityRulesRepository(properties);

        try {
            repo.rules();
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(MalformedRuleException.class));
            assertThat(((MalformedRuleException) e).getInvalidRules().size(), is(1));
            assertThat(((MalformedRuleException) e).getValidRules().size(), is(1));
        }
    }

    private void assertOutcome(RuleOutcome.Outcome expected) {
        getRules().forEach(rule -> {
            RuleOutcome outcome = rule.isCompliant(invoice);
            assertEquals(expected, outcome.outcome());
        });
    }

    private List<Rule> getRules() {
        IntegrityRulesRepository repo = new IntegrityRulesRepository(properties);
        return repo.rules();
    }
}