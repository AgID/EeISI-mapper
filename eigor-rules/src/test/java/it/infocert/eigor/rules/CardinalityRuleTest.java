package it.infocert.eigor.rules;

import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardinalityRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void onlyOneCompliant() throws Exception {
        CardinalityRule rule = new CardinalityRule("/BT0001", 1, 1);
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have exactly 1 BT0001, it has: 1.", compliant.description());
        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void onlyOneNonCompliant() throws Exception {
        CardinalityRule rule = new CardinalityRule("/BT0001", 1, 1);

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have exactly 1 BT0001, it has: 0.", compliant.description());
        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void atLeastOneCompliant() throws Exception {
        CardinalityRule rule = new CardinalityRule("/BG0004/BT0029", 0, null);
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0029SellerIdentifierAndSchemeIdentifier().add(new BT0029SellerIdentifierAndSchemeIdentifier("id"));
        invoice.getBG0004Seller().add(seller);

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have at least 0 BT0029, it has: 1.", compliant.description());
        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void atLeastOneNotCompliant() throws Exception {
        CardinalityRule rule = new CardinalityRule("/BG0004/BT0029", 1, null); //this voluntarily is a non valid rule
        invoice.getBG0004Seller().add(new BG0004Seller());

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have at least 1 BT0029, it has: 0.", compliant.description());
        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void atMostOneCompliant() throws Exception {
        CardinalityRule rule = new CardinalityRule("/BG0004/BT0028", 0, 1);
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0028SellerTradingName().add(new BT0028SellerTradingName("name"));
        invoice.getBG0004Seller().add(seller);

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have between 0 and 1 BT0028, it has: 1.", compliant.description());
        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void atMostOneNotCompliant() throws Exception {
        CardinalityRule rule = new CardinalityRule("/BG0004/BT0028", 0, 1);
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0028SellerTradingName().add(new BT0028SellerTradingName("name"));
        seller.getBT0028SellerTradingName().add(new BT0028SellerTradingName("name2"));
        invoice.getBG0004Seller().add(seller);

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have between 0 and 1 BT0028, it has: 2.", compliant.description());
        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void deepPathSuccess() throws Exception {
        BG0032ItemAttributes itemAttributes = new BG0032ItemAttributes();
        itemAttributes.getBT0161ItemAttributeValue().add(new BT0161ItemAttributeValue("Attribute"));
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBG0032ItemAttributes().add(itemAttributes);
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBG0031ItemInformation().add(itemInformation);
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        CardinalityRule rule = new CardinalityRule("/BG0025/BG0031/BG0032/BT0161", 0, 1);

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void deepPathFailure() throws Exception {
        BG0032ItemAttributes itemAttributes = new BG0032ItemAttributes();
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBG0032ItemAttributes().add(itemAttributes);
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBG0031ItemInformation().add(itemInformation);
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        CardinalityRule rule = new CardinalityRule("/BG0025/BG0031/BG0032/BT0161", 1, 1);

        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void multipleChildsSuccess() throws Exception {
        BG0025InvoiceLine invoiceLine1 = new BG0025InvoiceLine();
        invoiceLine1.getBG0029PriceDetails().add(new BG0029PriceDetails());
        BG0025InvoiceLine invoiceLine2 = new BG0025InvoiceLine();
        invoiceLine2.getBG0029PriceDetails().add(new BG0029PriceDetails());
        BG0025InvoiceLine invoiceLine3 = new BG0025InvoiceLine();
        invoiceLine3.getBG0029PriceDetails().add(new BG0029PriceDetails());
        invoice.getBG0025InvoiceLine().add(invoiceLine1);
        invoice.getBG0025InvoiceLine().add(invoiceLine2);
        invoice.getBG0025InvoiceLine().add(invoiceLine3);

        CardinalityRule rule = new CardinalityRule("/BG0025/BG0029", 1, 1);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have exactly 1 BG0029, it has: 1.", compliant.description());
        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void multipleChildsFailure() throws Exception {
        BG0025InvoiceLine invoiceLine1 = new BG0025InvoiceLine();
        invoiceLine1.getBG0029PriceDetails().add(new BG0029PriceDetails());
        invoiceLine1.getBG0029PriceDetails().add(new BG0029PriceDetails());
        BG0025InvoiceLine invoiceLine2 = new BG0025InvoiceLine();
        invoiceLine2.getBG0029PriceDetails().add(new BG0029PriceDetails());
        BG0025InvoiceLine invoiceLine3 = new BG0025InvoiceLine();
        invoiceLine3.getBG0029PriceDetails().add(new BG0029PriceDetails());
        invoiceLine3.getBG0029PriceDetails().add(new BG0029PriceDetails());
        invoice.getBG0025InvoiceLine().add(invoiceLine1);
        invoice.getBG0025InvoiceLine().add(invoiceLine2);
        invoice.getBG0025InvoiceLine().add(invoiceLine3);

        CardinalityRule rule = new CardinalityRule("/BG0025/BG0029", 1, 1);
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals("An invoice shall have exactly 1 BG0029, it has: 2.", compliant.description());
        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }
}