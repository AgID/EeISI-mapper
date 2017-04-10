package it.infocert.eigor.rules.business;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.BT0027SellerName;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShallContainBusinessRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void failingTest() throws Exception {
        ShallContainBusinessRule rule = new ShallContainBusinessRule("BT0001", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void successfulTest() throws Exception {
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        ShallContainBusinessRule rule = new ShallContainBusinessRule("BT0001", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainSuccesfulTest() throws Exception {
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("Name"));
        invoice.getBG0004Seller().add(seller);
        ShallContainBusinessRule rule = new ShallContainBusinessRule("BT0027", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

}