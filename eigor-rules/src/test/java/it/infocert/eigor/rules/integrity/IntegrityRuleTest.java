package it.infocert.eigor.rules.integrity;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrityRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void evaluateAsTrueATrueExpression() throws Exception {
        String expr = "${true}";

        assertRuleOutcome(RuleOutcome.Outcome.SUCCESS, expr);
    }


    @Test
    public void evaluateAsFalseAFalseExpression() throws Exception {
        String expr = "${false}";

        assertRuleOutcome(RuleOutcome.Outcome.FAILED, expr);
    }

    @Test
    public void evaluateSuccessfulRuleExpression() throws Exception {
        String expr = "${!invoice.getBT0001InvoiceNumber().isEmpty()}";
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));

        assertRuleOutcome(RuleOutcome.Outcome.SUCCESS, expr, "Rule \\w+ successfully validated");
    }

    @Test
    public void evaluateFailingRuleExpression() throws Exception {
        String expr = "${!invoice.getBT0001InvoiceNumber().isEmpty()}";

        assertRuleOutcome(RuleOutcome.Outcome.FAILED, expr, "Rule \\w+ has failed");
    }

    @Test
    public void checkThatRuleBreaksOnMalformedExpression() throws Exception {
        String expr = "${!invoice.getBT0001InvoiceNumber().isEmpty}";

        assertRuleOutcome(RuleOutcome.Outcome.ERROR, expr);

    }

    @Test
    public void evaluateUnapplicableRuleAsUnapplicable() throws Exception {
        String expr = "${null}";

        assertRuleOutcome(RuleOutcome.Outcome.UNAPPLICABLE, expr, "Rule \\w+ is unapplicable");
    }

    @Test
    public void evaluateExpressionThatDoesNotReturnABoolean() throws Exception {
        String expr = "test";

        assertRuleOutcome(RuleOutcome.Outcome.ERROR, expr, "Error in the rule \\w+: java.lang.String cannot be cast to java.lang.Boolean");
    }

    @Test
    public void evaluateAnExpressionThatHasAIndexOutOfBoundAccessTry() throws Exception {
        String expr = "${invoice.getBG0004Seller().get(0).getBG0005SellerPostalAddress().isEmpty()}";

        assertRuleOutcome(RuleOutcome.Outcome.ERROR, expr);

    }

    private void assertRuleOutcome(RuleOutcome.Outcome expected, String expr) {
        assertRuleOutcome(expected, expr, invoice);
    }

    private RuleOutcome assertRuleOutcome(RuleOutcome.Outcome expected, String expr, BG0000Invoice invoice) {
        Rule rule = new IntegrityRule(expr, "br1");
        RuleOutcome outcome = rule.isCompliant(invoice);
        assertEquals(expected, outcome.outcome());
        return outcome;
    }

    private void assertRuleOutcome(RuleOutcome.Outcome expected, String expr, String message) {
        assertRuleOutcome(expected, expr, message, invoice);
    }

    private void assertRuleOutcome(RuleOutcome.Outcome expected, String expr, String message, BG0000Invoice invoice) {
        RuleOutcome outcome = assertRuleOutcome(expected, expr, invoice);
        assertTrue(outcome.description().matches(message));
    }
}
