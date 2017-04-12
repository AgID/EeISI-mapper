package it.infocert.eigor.rules.business;

import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.CardinalityRule;
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
        ShallContainBusinessRule rule = new ShallContainBusinessRule("/BT0001", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void successfulTest() throws Exception {
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        ShallContainBusinessRule rule = new ShallContainBusinessRule("/BT0001", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainSuccessfulTest() throws Exception {
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("Name"));
        invoice.getBG0004Seller().add(seller);
        ShallContainBusinessRule rule = new ShallContainBusinessRule("/BG0004/BT0027", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainFailingTest() throws Exception {
        invoice.getBG0004Seller().add(new BG0004Seller());
        ShallContainBusinessRule rule = new ShallContainBusinessRule("/BG0004/BT0027", invoice);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

//    @Test
    public void name() throws Exception {
        BG0025InvoiceLine invoiceLine1 = new BG0025InvoiceLine();
        invoiceLine1.getBG0029PriceDetails().add(new BG0029PriceDetails());
        BG0025InvoiceLine invoiceLine2 = new BG0025InvoiceLine();
        invoiceLine2.getBG0029PriceDetails().add(new BG0029PriceDetails());
        BG0025InvoiceLine invoiceLine3 = new BG0025InvoiceLine();
        invoiceLine3.getBG0029PriceDetails().add(new BG0029PriceDetails());
        invoice.getBG0025InvoiceLine().add(invoiceLine1);
        invoice.getBG0025InvoiceLine().add(invoiceLine2);
        invoice.getBG0025InvoiceLine().add(invoiceLine3);

        CardinalityRule rule = new CardinalityRule("/BG0025/BG0029/BT", 1, 1);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }
}