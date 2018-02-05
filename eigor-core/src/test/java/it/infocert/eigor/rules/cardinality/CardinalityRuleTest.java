package it.infocert.eigor.rules.cardinality;

import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;

import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.*;
import static org.junit.Assert.*;

@Ignore
public class CardinalityRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        this.invoice = new BG0000Invoice();
    }

    @Test
    public void shouldReadTheCardinalityExpression() throws Exception {
        CardinalityRule rule = new CardinalityRule("BT-2", "1..1");
        Field name = rule.getClass().getDeclaredField("name");
        Field min = rule.getClass().getDeclaredField("min");
        Field max = rule.getClass().getDeclaredField("max");
        name.setAccessible(true);
        min.setAccessible(true);
        max.setAccessible(true);

        String gotName = (String) name.get(rule);
        Integer gotMin = (Integer) min.get(rule);
        Integer gotMax = (Integer) max.get(rule);
        assertEquals("BT0002", gotName);
        assertEquals(new Integer(1), gotMin);
        assertEquals(new Integer(1), gotMax);
    }

    @Test
    public void shouldMaxToNullIfCardinalityIsN() throws Exception {
        CardinalityRule rule = new CardinalityRule("BT-2", "1..n");
        Field max = rule.getClass().getDeclaredField("max");
        max.setAccessible(true);
        Integer gotMax = (Integer) max.get(rule);
        assertNull(gotMax);
    }

    @Test
    public void shouldThrowMalformedRuleExceptionIfMinIsNotANumber() throws Exception {
        try {
            new CardinalityRule("BT-2", "a..n");
            fail();
        } catch (MalformedRuleException ignored) {}
    }

    @Test
    public void shouldSucceedOnValidMinimumCardinality() throws Exception {
        LocalDate now = LocalDate.now();
        invoice.getBT0002InvoiceIssueDate().add(new BT0002InvoiceIssueDate(now));


        CardinalityRule rule = new CardinalityRule("BT-2", "1..1");

        RuleOutcome outcome = rule.isCompliant(invoice);

        assertEquals(SUCCESS, outcome.outcome());
        assertTrue(outcome.description().matches("^\\w+ - Cardinality \\d\\.\\.[\\d|n] verified\\.$"));
    }

    @Test
    public void shouldSucceedWithNestedBTs() throws Exception {
        BG0032ItemAttributes itemAttributes = new BG0032ItemAttributes();
        BG0031ItemInformation itemInformation= new BG0031ItemInformation();
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        itemAttributes.getBT0161ItemAttributeValue().add(new BT0161ItemAttributeValue("Value"));
        itemInformation.getBG0032ItemAttributes().add(itemAttributes);
        invoiceLine.getBG0031ItemInformation().add(itemInformation);
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        CardinalityRule rule = new CardinalityRule("BT-161", "1..1");

        RuleOutcome outcome = rule.isCompliant(invoice);

        assertEquals(SUCCESS, outcome.outcome());
        assertTrue(outcome.description().matches("^\\w+ - Cardinality \\d\\.\\.[\\d|n] verified\\.$"));
    }

    @Test
    public void shouldBeUnapplicableWhenAParentIsMissing() throws Exception {
        CardinalityRule rule = new CardinalityRule("BT-22", "1..1");

        RuleOutcome outcome = rule.isCompliant(invoice);

        assertEquals(UNAPPLICABLE, outcome.outcome());
        assertTrue(outcome.description().matches("^\\w+ - Can't verify the cardinality because one of its parent elements is missing\\. Last parent checked: \\w\\w-\\d\\.$"));
    }

    @Test
    public void shouldNeverFailIfMinIsZero() throws Exception {
        CardinalityRule rule = new CardinalityRule("BT-1", "0..1");

        RuleOutcome zeroOutcome = rule.isCompliant(invoice);
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        RuleOutcome oneOutcome = rule.isCompliant(invoice);

        assertEquals(SUCCESS, zeroOutcome.outcome());
        assertEquals(SUCCESS, oneOutcome.outcome());
    }

    @Test
    public void shouldFailIfThereAreTooManyElements() throws Exception {
        for (int i = 0; i < 5; i++){
            invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber(String.valueOf(i)));
        }
        CardinalityRule rule = new CardinalityRule("BT-1", "1..1");

        RuleOutcome outcome = rule.isCompliant(invoice);

        assertEquals(FAILED, outcome.outcome());
        assertTrue(outcome.description().matches("^\\w+ - Cardinality \\d\\.\\.[\\d|n] not verified\\. Found \\d times\\.$"));
    }

    @Test
    public void shouldFailIfMinimumCardinalityIsNotMet() throws Exception {
        CardinalityRule rule = new CardinalityRule("BT-1", "1..1");

        RuleOutcome outcome = rule.isCompliant(invoice);

        assertEquals(FAILED, outcome.outcome());
        assertTrue(outcome.description().matches("^\\w+ - Cardinality \\d\\.\\.[\\d|n] not verified\\. Found \\d times\\.$"));
    }
}