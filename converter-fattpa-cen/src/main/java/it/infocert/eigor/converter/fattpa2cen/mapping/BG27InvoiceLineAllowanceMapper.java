package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioLineeType;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;

class BG27InvoiceLineAllowanceMapper {

    private static DettaglioLineeType dettaglioLinee;

    static BG0027InvoiceLineAllowances mapInvoiceLineAllowance(DettaglioLineeType dettaglioLineeType) {
        BG0027InvoiceLineAllowances invoiceLineAllowances = new BG0027InvoiceLineAllowances();

        dettaglioLinee = dettaglioLineeType;
        return invoiceLineAllowances;
    }


}
