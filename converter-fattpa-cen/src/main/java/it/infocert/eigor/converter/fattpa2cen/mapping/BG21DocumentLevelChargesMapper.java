package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.*;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.*;

import java.math.BigDecimal;
import java.util.List;

class BG21DocumentLevelChargesMapper {

    private static DatiGeneraliDocumentoType datiDocumento;

    static BG0021DocumentLevelCharges mapDocumentLevelCharges(DatiGeneraliDocumentoType datiGeneraliDocumento) {
        BG0021DocumentLevelCharges documentLevelCharges = new BG0021DocumentLevelCharges();

        datiDocumento = datiGeneraliDocumento;

        BigDecimal importoBollo = mapBT99();
        if (importoBollo != null) {
            documentLevelCharges.getBT0099DocumentLevelChargeAmount()
                    .add(new BT0099DocumentLevelChargeAmount(importoBollo.doubleValue()));
        }

        BolloVirtualeType bolloVirtuale = mapBT104();
        if (bolloVirtuale != null) {
            documentLevelCharges.getBT0104DocumentLevelChargeReason()
                    .add(new BT0104DocumentLevelChargeReason(bolloVirtuale.value()));
        }

        List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale = datiDocumento.getDatiCassaPrevidenziale();
        if (datiCassaPrevidenziale != null && !datiCassaPrevidenziale.isEmpty()) {
            for (DatiCassaPrevidenzialeType dati : datiCassaPrevidenziale) {

                TipoCassaType tipoCassa = mapBT105(dati);
                if (tipoCassa != null) {
                    documentLevelCharges.getBT0105DocumentLevelChargeReasonCode()
                            .add(new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.valueOf(tipoCassa.value())));
                }


                BigDecimal alCassa = mapBT101(dati);
                if (alCassa != null) {
                    documentLevelCharges.getBT0101DocumentLevelChargePercentage()
                            .add(new BT0101DocumentLevelChargePercentage(alCassa.doubleValue()));
                }
            }
        }
        return documentLevelCharges;
    }

    private static BigDecimal mapBT99() {
        DatiBolloType datiBollo = datiDocumento.getDatiBollo();
        if (datiBollo != null) {
            return datiBollo.getImportoBollo();
        } else {
            return null;
        }
    }

    private static BigDecimal mapBT101(DatiCassaPrevidenzialeType dati) {
        return dati.getAlCassa();
    }

    private static BolloVirtualeType mapBT104() {
        DatiBolloType datiBollo = datiDocumento.getDatiBollo();
        if (datiBollo != null) {
            return datiBollo.getBolloVirtuale();
        } else {
            return null;
        }
    }

    private static TipoCassaType mapBT105(DatiCassaPrevidenzialeType dati) {
        return dati.getTipoCassa();
    }




}
