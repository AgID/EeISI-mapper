package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.*;

import java.math.BigDecimal;

public final class Scenarios {

    static BG0000Invoice invoiceWithAmounts() {
        BG0000Invoice theInvoice;
        theInvoice = new BG0000Invoice();
        BG0022DocumentTotals totals = new BG0022DocumentTotals();
        totals.getBT0106SumOfInvoiceLineNetAmount().add(new BT0106SumOfInvoiceLineNetAmount(new BigDecimal(100)));
        totals.getBT0109InvoiceTotalAmountWithoutVat().add(new BT0109InvoiceTotalAmountWithoutVat(new BigDecimal(100)));
        theInvoice.getBG0022DocumentTotals().add(totals);
        theInvoice.getBT0005InvoiceCurrencyCode().add(new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR));
        return theInvoice;
    }

    private Scenarios() {
    }
}