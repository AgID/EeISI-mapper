package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class CompositeRuleRepositoryTest {
    @Test
    public void shouldConstructFromAListOfRepositories() throws Exception {
        RuleRepository repository = mock(RuleRepository.class);
        RuleRepository anotherRepo = mock(RuleRepository.class);

        CompositeRuleRepository compositeRuleRepository = new CompositeRuleRepository(repository, anotherRepo);

        Field componentsField = CompositeRuleRepository.class.getDeclaredField("components");
        componentsField.setAccessible(true);
        List<RuleRepository> components = (List<RuleRepository>) componentsField.get(compositeRuleRepository);
        assertThat(components.size(), is(2));
    }

    @Test
    public void shouldReturnTheCombinedRulesOfTheComponents() throws Exception {
        RuleRepository repository = mock(RuleRepository.class);
        RuleRepository anotherRepo = mock(RuleRepository.class);

        ArrayList<Rule> repoList = new ArrayList<>(10);
        ArrayList<Rule> anotherRepoList = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            repoList.add(mock(Rule.class));
            anotherRepoList.add(mock(Rule.class));
        }

        when(repository.rules()).thenReturn(repoList);
        when(anotherRepo.rules()).thenReturn(anotherRepoList);

        CompositeRuleRepository compositeRuleRepository = new CompositeRuleRepository(repository, anotherRepo);

        List<Rule> rules = compositeRuleRepository.rules();

        assertThat(rules.size(), is(20));
    }

    @Test
    public void shouldWorkWithRealRules() throws Exception {
        BG0000Invoice invoice = createInvoice();
        Properties intProps = new Properties();
        Properties cardProps = new Properties();

        intProps.setProperty("bt1.body", "${true}");
        cardProps.setProperty("BT-1", "1..1");

        IntegrityRulesRepository integrityRulesRepository = new IntegrityRulesRepository(intProps);
        CardinalityRulesRepository cardinalityRulesRepository = new CardinalityRulesRepository(cardProps);

        CompositeRuleRepository compositeRuleRepository = new CompositeRuleRepository(integrityRulesRepository, cardinalityRulesRepository);

        assertThat(compositeRuleRepository.rules().size(), is(2));

        for (Rule rule: compositeRuleRepository.rules()) {
            RuleOutcome outcome = rule.isCompliant(invoice);
            assertEquals(RuleOutcome.Outcome.SUCCESS, outcome.outcome());
        }
    }

    private BG0000Invoice createInvoice() {
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        return invoice;
    }
}