package it.infocert.eigor.rules.business;

import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShallContainRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void failingTest() throws Exception {
        ShallContainRule rule = new ShallContainRule("/BT0001");
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void successfulTest() throws Exception {
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        ShallContainRule rule = new ShallContainRule("/BT0001");
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainSuccessfulTest() throws Exception {
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("Name"));
        invoice.getBG0004Seller().add(seller);
        ShallContainRule rule = new ShallContainRule("/BG0004/BT0027");
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainFailingTest() throws Exception {
        invoice.getBG0004Seller().add(new BG0004Seller());
        ShallContainRule rule = new ShallContainRule("/BG0004/BT0027");
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }
}