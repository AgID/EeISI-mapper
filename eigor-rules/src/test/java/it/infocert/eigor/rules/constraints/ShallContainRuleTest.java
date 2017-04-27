package it.infocert.eigor.rules.constraints;

import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.assertEquals;

public class ShallContainRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void failingTest() throws Exception {
        ShallContainRule rule = new ShallContainRule("/BT0001", new Reflections("it.infocert"));
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void successfulTest() throws Exception {
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        ShallContainRule rule = new ShallContainRule("/BT0001", new Reflections("it.infocert"));
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainSuccessfulTest() throws Exception {
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("Name"));
        invoice.getBG0004Seller().add(seller);
        ShallContainRule rule = new ShallContainRule("/BG0004/BT0027", new Reflections("it.infocert"));
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }

    @Test
    public void complexChainFailingTest() throws Exception {
        invoice.getBG0004Seller().add(new BG0004Seller());
        ShallContainRule rule = new ShallContainRule("/BG0004/BT0027", new Reflections("it.infocert"));
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void multipleChildsFailingTest() throws Exception {
        ShallContainRule rule = new ShallContainRule(Arrays.array("/BG0025/BG0031/BG0032/BT0160", "/BG0025/BG0031/BG0032/BT0160"), new Reflections("it.infocert"));
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.FAILED, outcome);
    }

    @Test
    public void multipleChildsSuccessfulTest() throws Exception {
        BG0032ItemAttributes itemAttributes = new BG0032ItemAttributes();
        itemAttributes.getBT0160ItemAttributeName().add(new BT0160ItemAttributeName("Name"));
        itemAttributes.getBT0161ItemAttributeValue().add(new BT0161ItemAttributeValue("Attribute"));
        BG0031ItemInformation itemInformation = new BG0031ItemInformation();
        itemInformation.getBG0032ItemAttributes().add(itemAttributes);
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBG0031ItemInformation().add(itemInformation);
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        ShallContainRule rule = new ShallContainRule(Arrays.array("/BG0025/BG0031/BG0032/BT0160", "/BG0025/BG0031/BG0032/BT0160"), new Reflections("it.infocert"));
        RuleOutcome compliant = rule.isCompliant(invoice);
        RuleOutcome.Outcome outcome = compliant.outcome();

        assertEquals(RuleOutcome.Outcome.SUCCESS, outcome);
    }
}