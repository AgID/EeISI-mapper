package it.infocert.eigor.model.core;

import java.util.ArrayList;
import java.util.List;

public class Invoice {

    // this is mandatory
    private InvoiceNumber invoiceNumber;

    // this is optional
    private CurrencyCode vatAccountingCurrencyCode;

    // 1..n
    private List<InvoiceLine> invoiceLines = new ArrayList<>();

    public InvoiceNumber getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(InvoiceNumber invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setVatAccountingCurrencyCode(CurrencyCode vatAccountingCurrencyCode) {
        this.vatAccountingCurrencyCode = vatAccountingCurrencyCode;
    }

    /**
     * Shall be used in combination with the Total VAT amount in accounting currency (BT-111)
     * when the VAT accounting currency code differs from the Invoice currency code.
     * The lists of valid currencies are registered with the ISO 4217 Maintenance Agency
     * "Codes for the representation of currencies and funds".
     * Please refer to Article 230 of the Council Directive 2006/112/EC [2] for more information.
     */
    public CurrencyCode getVatAccountingCurrencyCode() {
        return vatAccountingCurrencyCode;
    }

    public void addInvoiceLine(InvoiceLine invoiceLine) {
        this.invoiceLines.add(invoiceLine);
    }
}
