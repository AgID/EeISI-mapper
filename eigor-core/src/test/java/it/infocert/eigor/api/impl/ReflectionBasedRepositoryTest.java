package it.infocert.eigor.api.impl;


import com.amoerie.jstreams.functions.Filter;
import it.infocert.eigor.model.core.rules.Br002AnInvoiceShallHaveAnInvoiceNumberRule;
import it.infocert.eigor.model.core.rules.Rule;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class ReflectionBasedRepositoryTest {

    private static Reflections reflections;

    @BeforeClass
    public static void setUp() throws Exception {
        reflections = new Reflections("it.infocert.eigor.model.core.rules");
    }

    @Test public void shouldFindRules() {

        // given
        ReflectionBasedRepository sut = new ReflectionBasedRepository(reflections);
        final Class<? extends Rule> aRuleThatShouldBeFound = Br002AnInvoiceShallHaveAnInvoiceNumberRule.class;

        // when
        List<Rule> allRules = sut.rules();

        // then
        List<Rule> rules = Stream.create(allRules).filter(new Filter<Rule>() {
            @Override public boolean apply(Rule r) {
                return r.getClass().equals(aRuleThatShouldBeFound);
            }
        }).toList();

        assertThat( rules, Matchers.hasSize(1) );
        assertThat( rules.get(0), instanceOf(aRuleThatShouldBeFound));

    }



}
