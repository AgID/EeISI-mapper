package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.model.core.rules.Br002AnInvoiceShallHaveAnInvoiceNumberRule;
import it.infocert.eigor.model.core.rules.Rule;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ReflectionBasedRepositoryTest {

    private static Reflections reflections;

    @BeforeClass
    public static void setUp() throws Exception {
        reflections = new Reflections("it.infocert");
    }

    @Test public void shouldFindRules() {

        // given
        ReflectionBasedRepository sut = new ReflectionBasedRepository(new Reflections("it.infocert.eigor.model"));
        Class<? extends Rule> aRuleThatShouldBeFound = Br002AnInvoiceShallHaveAnInvoiceNumberRule.class;

        // when
        List<Rule> allRules = sut.rules();

        // then
        List<Rule> rules = allRules.stream().filter(r -> r.getClass().equals(aRuleThatShouldBeFound)).collect(toList());
        assertThat( rules, Matchers.hasSize(1) );
        assertThat( rules.get(0), instanceOf(aRuleThatShouldBeFound));

    }

    @Test public void shouldFindToCen() {

        // given
        ReflectionBasedRepository sut = new ReflectionBasedRepository(reflections);
        Class<? extends ToCenConversion> aConversionThatShouldBeFound = FakeToCenConversion.class;

        // when
        ToCenConversion conversion = sut.findConversionToCen("fake");

        // then
        assertThat( conversion, notNullValue() );
        assertThat( conversion, instanceOf(aConversionThatShouldBeFound));

    }

    @Test public void shouldFindFromCen() {

        // given
        ReflectionBasedRepository sut = new ReflectionBasedRepository(reflections);
        Class<? extends FromCenConversion> aConversionThatShouldBeFound = FakeFromCenConversion.class;

        // when
        FromCenConversion conversion = sut.findConversionFromCen("fake");

        // then
        assertThat( conversion, notNullValue() );
        assertThat( conversion, instanceOf(aConversionThatShouldBeFound));

    }

    @Test public void shouldReturnAllSupportedFormats() {

        // given
        ReflectionBasedRepository sut = new ReflectionBasedRepository(reflections);

        // when
        Set<String> formats = sut.supportedToCenFormats();

        // then
        assertThat( formats.size(), is(0) );


    }

}