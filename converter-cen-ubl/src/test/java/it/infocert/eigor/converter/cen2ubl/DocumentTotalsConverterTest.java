package it.infocert.eigor.converter.cen2ubl;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Marco Basilico on 28/09/2017.
 */
public class DocumentTotalsConverterTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        BG0022DocumentTotals totals = new BG0022DocumentTotals();
        totals.getBT0106SumOfInvoiceLineNetAmount().add(new BT0106SumOfInvoiceLineNetAmount(100d));
        totals.getBT0109InvoiceTotalAmountWithoutVat().add(new BT0109InvoiceTotalAmountWithoutVat(100d));
        invoice.getBG0022DocumentTotals().add(totals);
        invoice.getBT0005InvoiceCurrencyCode().add(new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));
    }

    @Test
    public void shouldAddAttributeToAllChildren() throws Exception {
        DocumentTotalsConverter sut = new DocumentTotalsConverter();
        Document doc = new Document(new Element("Invoice"));
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element amount = doc.getRootElement().getChild("LegalMonetaryTotal");

        for (Element child : amount.getChildren()) {
            assertTrue(child.hasAttributes());
            assertEquals("EUR", child.getAttributeValue("currencyID"));
        }

    }
}