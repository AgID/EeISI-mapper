package it.infocert.eigor.rules.constraints;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0022DocumentTotals;
import it.infocert.eigor.model.core.model.BT0006VatAccountingCurrencyCode;
import it.infocert.eigor.model.core.model.BT0111InvoiceTotalVatAmountInAccountingCurrency;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.*;

public class ConditionalShallContainRuleTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
    }

    @Test
    public void success() throws Exception {
        ConditionalShallContainRule rule = new ConditionalShallContainRule("/BG0022/BT0111", "/BT0006", new Reflections("it.infocert"));
        BG0022DocumentTotals totals = new BG0022DocumentTotals();
        totals.getBT0111InvoiceTotalVatAmountInAccountingCurrency().add(new BT0111InvoiceTotalVatAmountInAccountingCurrency(22d));
        invoice.getBG0022DocumentTotals().add(totals);
        invoice.getBT0006VatAccountingCurrencyCode().add(new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));

        RuleOutcome compliant = rule.isCompliant(invoice);

        assertEquals(RuleOutcome.Outcome.SUCCESS, compliant.outcome());
    }

    @Test
    public void failure() throws Exception {
        ConditionalShallContainRule rule = new ConditionalShallContainRule("/BG0022/BT0111", "/BT0006", new Reflections("it.infocert"));
        invoice.getBT0006VatAccountingCurrencyCode().add(new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));

        RuleOutcome compliant = rule.isCompliant(invoice);

        assertEquals(RuleOutcome.Outcome.FAILED, compliant.outcome());
    }

    @Test
    public void unapplicable() throws Exception {
        ConditionalShallContainRule rule = new ConditionalShallContainRule("/BG0022/BT0111", "/BT0006", new Reflections("it.infocert"));
        BG0022DocumentTotals totals = new BG0022DocumentTotals();
        totals.getBT0111InvoiceTotalVatAmountInAccountingCurrency().add(new BT0111InvoiceTotalVatAmountInAccountingCurrency(22d));
        invoice.getBG0022DocumentTotals().add(totals);

        RuleOutcome compliant = rule.isCompliant(invoice);

        assertEquals(RuleOutcome.Outcome.UNAPPLICABLE, compliant.outcome());
    }
}