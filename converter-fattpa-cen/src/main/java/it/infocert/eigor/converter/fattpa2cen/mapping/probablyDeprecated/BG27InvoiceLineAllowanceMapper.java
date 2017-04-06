package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioLineeType;
import it.infocert.eigor.converter.fattpa2cen.models.ScontoMaggiorazioneType;
import it.infocert.eigor.model.core.model.BG0027InvoiceLineAllowances;

class BG27InvoiceLineAllowanceMapper {

    private static ScontoMaggiorazioneType sconto;

    static BG0027InvoiceLineAllowances mapInvoiceLineAllowance(ScontoMaggiorazioneType scontoMaggiorazione) {
        BG0027InvoiceLineAllowances invoiceLineAllowances = new BG0027InvoiceLineAllowances();

        sconto = scontoMaggiorazione;


        return invoiceLineAllowances;
    }


}
