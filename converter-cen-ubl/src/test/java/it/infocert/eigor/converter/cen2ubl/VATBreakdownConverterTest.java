package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class VATBreakdownConverterTest {
    private VATBreakdownConverter converter;
    private Document document;

    @Before
    public void setUp() throws Exception {
        converter = new VATBreakdownConverter();
        document = new Document(new Element("Invoice"));
    }

    @Test
    public void taxAmountFromBT0111AndCurrencyIdIfBT0006Exists() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBG0022AndBT0110AndBT0111AndBT0006();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);
        Element taxTotal = document.getRootElement().getChild("TaxTotal");
        Element taxAmount = taxTotal.getChild("TaxAmount");

        assertTrue("1000.00".equals(taxAmount.getText()));
        assertTrue("EUR".equals(taxAmount.getAttributeValue("currencyID")));
    }

    @Test
    public void taxAmountFromBT0110AndCurrencyIdFromBT0005IfBT0006DoesNotExists() throws Exception {
        BG0000Invoice invoice = createInvoiceWithBG0022AndBT0110AndBT0111AndBT0005();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);
        Element taxTotal = document.getRootElement().getChild("TaxTotal");
        Element taxAmount = taxTotal.getChild("TaxAmount");

        assertTrue("1000.00".equals(taxAmount.getText()));
        assertTrue("EUR".equals(taxAmount.getAttributeValue("currencyID")));
    }

    private BG0000Invoice createInvoiceWithBG0022AndBT0110AndBT0111AndBT0005() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0022DocumentTotals bg0022 = new BG0022DocumentTotals();
        bg0022.getBT0110InvoiceTotalVatAmount().add(new BT0110InvoiceTotalVatAmount(1000d));
        bg0022.getBT0111InvoiceTotalVatAmountInAccountingCurrency().add(new BT0111InvoiceTotalVatAmountInAccountingCurrency(666d));
        invoice.getBG0022DocumentTotals().add(bg0022);

        BT0005InvoiceCurrencyCode bt0005 = new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR);
        invoice.getBT0005InvoiceCurrencyCode().add(bt0005);
        return invoice;
    }

    private BG0000Invoice createInvoiceWithBG0022AndBT0110AndBT0111AndBT0006() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0022DocumentTotals bg0022 = new BG0022DocumentTotals();
        bg0022.getBT0110InvoiceTotalVatAmount().add(new BT0110InvoiceTotalVatAmount(666d));
        bg0022.getBT0111InvoiceTotalVatAmountInAccountingCurrency().add(new BT0111InvoiceTotalVatAmountInAccountingCurrency(1000d));
        invoice.getBG0022DocumentTotals().add(bg0022);

        BT0005InvoiceCurrencyCode bt0005 = new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.KPW);
        invoice.getBT0005InvoiceCurrencyCode().add(bt0005);

        BT0006VatAccountingCurrencyCode bt0006 = new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR);
        invoice.getBT0006VatAccountingCurrencyCode().add(bt0006);
        return invoice;
    }
}