package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.DatiDocumentiCorrelatiType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiGeneraliType;
import it.infocert.eigor.model.core.model.BG0003PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0025PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0026PrecedingInvoiceIssueDate;
import org.joda.time.LocalDate;

import java.util.List;

class BG03PrecedingInvoiceReferenceMapper {


    static BG0003PrecedingInvoiceReference mapPrecedingInvoiceReferenceMapper(DatiGeneraliType datiGenerali) {
        BG0003PrecedingInvoiceReference precedingInvoiceReference = new BG0003PrecedingInvoiceReference();

        List<DatiDocumentiCorrelatiType> datiDocumentiCorrelati = datiGenerali.getDatiFattureCollegate();

        if (!datiDocumentiCorrelati.isEmpty()) {
            for (DatiDocumentiCorrelatiType datiCorrelati : datiDocumentiCorrelati) {
                precedingInvoiceReference.getBT0025PrecedingInvoiceReference()
                        .add(new BT0025PrecedingInvoiceReference(mapBT25(datiCorrelati)));

                precedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate()
                        .add(new BT0026PrecedingInvoiceIssueDate(mapBT26(datiCorrelati)));
            }
        }

        return precedingInvoiceReference;
    }

    private static String mapBT25(DatiDocumentiCorrelatiType dati) {
        return dati.getIdDocumento();
    }

    private static LocalDate mapBT26(DatiDocumentiCorrelatiType dati) {
        return new LocalDate( dati.getData().toGregorianCalendar() );
    }
}
