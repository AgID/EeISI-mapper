package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConstraintsRepositoryTest {

    @Test
    public void rulesHasRun() throws Exception {
        BT0024SpecificationIdentifier test = new BT0024SpecificationIdentifier("test");
        BG0002ProcessControl control = new BG0002ProcessControl();
        control.getBT0024SpecificationIdentifier().add(test);
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBG0002ProcessControl().add(control);
        ConstraintsRepository repository = new ConstraintsRepository();
        List<Rule> rules = repository.rules();
        for (Rule rule : rules) {
            RuleOutcome compliant = rule.isCompliant(invoice);
            assertNotNull(compliant);
            assertNotNull(compliant.outcome());
        }
    }

    @Test
    public void rulesAllSucceded() throws Exception {
        BG0000Invoice invoice = createInvoice();
        ConstraintsRepository repository = new ConstraintsRepository();
        List<Rule> rules = repository.rules();
        for (Rule rule : rules) {
            RuleOutcome compliant = rule.isCompliant(invoice);
            assertEquals(RuleOutcome.Outcome.SUCCESS, compliant.outcome());
        }

    }

    private BG0000Invoice createInvoice() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0002ProcessControl processControl = new BG0002ProcessControl();
        BG0004Seller seller = new BG0004Seller();
        BG0005SellerPostalAddress sellerPostalAddress = new BG0005SellerPostalAddress();
        BG0007Buyer buyer = new BG0007Buyer();
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();

        processControl.getBT0024SpecificationIdentifier().add(new BT0024SpecificationIdentifier("Id"));

        sellerPostalAddress.getBT0040SellerCountryCode().add(new BT0040SellerCountryCode(Iso31661CountryCodes.IT));

        seller.getBT0027SellerName().add(new BT0027SellerName("Name"));
        seller.getBG0005SellerPostalAddress().add(sellerPostalAddress);

        buyer.getBT0044BuyerName().add(new BT0044BuyerName("Name"));

        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        invoice.getBT0002InvoiceIssueDate().add(new BT0002InvoiceIssueDate(LocalDate.now()));
        invoice.getBT0003InvoiceTypeCode().add(new BT0003InvoiceTypeCode(Untdid1001InvoiceTypeCode.Code380));
        invoice.getBT0005InvoiceCurrencyCode().add(new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));
        invoice.getBG0002ProcessControl().add(processControl);
        invoice.getBG0004Seller().add(seller);
        invoice.getBG0007Buyer().add(buyer);
        invoice.getBG0025InvoiceLine().add(invoiceLine);

        return invoice;
    }


}