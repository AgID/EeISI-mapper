package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioPagamentoType;
import it.infocert.eigor.model.core.model.BG0010Payee;
import it.infocert.eigor.model.core.model.BT0059PayeeName;

class BG10PayeeMapper {

    private static DettaglioPagamentoType dettaglioPagamento;

    static BG0010Payee mapPayee(DettaglioPagamentoType dettaglioPagamentoType) {
        BG0010Payee payee = new BG0010Payee();

        dettaglioPagamento = dettaglioPagamentoType;

        payee.getBT0059PayeeName().add(new BT0059PayeeName(mapBT59()));

        return payee;
    }

    private static String mapBT59() {
        return dettaglioPagamento.getBeneficiario();
    }
}
