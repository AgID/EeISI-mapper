package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioLineeType;
import it.infocert.eigor.converter.fattpa2cen.models.ScontoMaggiorazioneType;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.*;

import java.math.BigDecimal;
import java.util.List;

class BG25InvoiceLineMapper {

    private static DettaglioLineeType dettaglioLinee;

    static BG0025InvoiceLine mapInvoiceLine(DettaglioLineeType dettaglioLineeType) {
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();

        dettaglioLinee = dettaglioLineeType;

        invoiceLine.getBT0126InvoiceLineIdentifier()
                .add(new BT0126InvoiceLineIdentifier(mapBT126()));

        String descrizione = mapBT127();
        if (descrizione != null) {
            invoiceLine.getBT0127InvoiceLineNote()
                    .add(new BT0127InvoiceLineNote(descrizione));
        }

        BigDecimal quantita = mapBT129();
        if (quantita != null) {
            invoiceLine.getBT0129InvoicedQuantity()
                    .add(new BT0129InvoicedQuantity(quantita.doubleValue()));
        }

        String unitOfMeasure = mapBT130();
        if (unitOfMeasure != null) {
            invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode()
                    .add(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.valueOf(unitOfMeasure)));
        }

        List<ScontoMaggiorazioneType> scontoMaggiorazione = dettaglioLinee.getScontoMaggiorazione();
        if (!scontoMaggiorazione.isEmpty()) {
            for (ScontoMaggiorazioneType sconto : scontoMaggiorazione) {
                invoiceLine.getBG0027InvoiceLineAllowances()
                        .add(BG27InvoiceLineAllowanceMapper.mapInvoiceLineAllowance(sconto));
            }
        }

        return invoiceLine;
    }

    private static String mapBT126() {
        return String.valueOf(dettaglioLinee.getNumeroLinea());
    }

    private static String mapBT127() {
        return dettaglioLinee.getDescrizione();
    }

    private static BigDecimal mapBT129() {
        return dettaglioLinee.getQuantita();
    }

    private static String mapBT130() {
        return dettaglioLinee.getUnitaMisura();
    }
}
