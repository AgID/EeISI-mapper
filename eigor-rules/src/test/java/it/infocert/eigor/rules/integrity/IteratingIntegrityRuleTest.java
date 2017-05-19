package it.infocert.eigor.rules.integrity;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0027SellerName;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Stream;

import static it.infocert.eigor.model.core.rules.RuleOutcome.Outcome.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IteratingIntegrityRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void shouldReturnSuccessOnValidRuleWithASingleChild() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        invoice.getBG0004Seller().add(seller);

        assertOutcome(SUCCESS, iter, expr);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void shouldReturnSuccessOnValidRuleWithMultipleChildren() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";

        BG0004Seller sellerMock = mock(BG0004Seller.class);

        ArrayList<BT0027SellerName> list = new ArrayList<>();
        list.add(new BT0027SellerName("name"));
        when(sellerMock.getBT0027SellerName()).thenReturn(list);

        invoice.getBG0004Seller().add(sellerMock);
        invoice.getBG0004Seller().add(sellerMock);
        invoice.getBG0004Seller().add(sellerMock);

        assertOutcome(SUCCESS, iter, expr);
        verify(sellerMock, times(3)).getBT0027SellerName();
    }

    @Test
    public void shouldOutcomeErrorIfNoIteratorIsDefined() throws Exception {
        String iter = "${invoice.getBG0004Seller()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";

        assertOutcome(ERROR, iter, expr);
    }

    @Test
    public void shouldBeUnapplicableIfIteratorIsEmpty() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";

        assertOutcome(UNAPPLICABLE, iter, expr);
    }

    @Test
    public void shouldFailIfRuleIsFailing() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";

        invoice.getBG0004Seller().add(new BG0004Seller());

        assertOutcome(FAILED, iter, expr);
    }

    @Test
    public void shouldFailIfOneOfTheParentFails() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";

        BG0004Seller seller = new BG0004Seller();
        BG0004Seller seller1 = new BG0004Seller();
        BG0004Seller seller2 = new BG0004Seller();

        seller.getBT0027SellerName().add(new BT0027SellerName("name"));
        seller2.getBT0027SellerName().add(new BT0027SellerName("name"));

        invoice.getBG0004Seller().add(seller);
        invoice.getBG0004Seller().add(seller1);
        invoice.getBG0004Seller().add(seller2);

        assertOutcome(FAILED, iter, expr);
    }

    @Test
    public void prop() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";
        Properties prop = new Properties();
        prop.put("br1.item", iter);
        prop.put("br1.body", expr);


        assertOutcome(UNAPPLICABLE, ((String) prop.get("br1.item")), ((String) prop.get("br1.body")));
    }

    @Test
    public void name() throws Exception {
        String iter = "${invoice.getBG0004Seller().iterator()}";
        String expr = "${!item.getBT0027SellerName().isEmpty()}";
        Rule rule = new IteratingIntegrityRule(iter, expr, "br1");

        RuleOutcome outcome = rule.isCompliant(invoice);
        System.out.println(outcome.outcome() + " " + outcome.description());
    }

    private void assertOutcome(RuleOutcome.Outcome expected, String iter, String expr) {
        Rule rule = new IteratingIntegrityRule(iter, expr, "br1");

        RuleOutcome outcome = rule.isCompliant(invoice);
        assertEquals(expected, outcome.outcome());
    }
}