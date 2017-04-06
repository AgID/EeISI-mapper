package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioPagamentoType;
import it.infocert.eigor.model.core.model.BG0017CreditTransfer;
import it.infocert.eigor.model.core.model.BT0084PaymentAccountIdentifier;
import it.infocert.eigor.model.core.model.BT0086PaymentServiceProviderIdentifier;

class BG17CreditTransferMapper {

    private static DettaglioPagamentoType dettaglioPagamento;

    static BG0017CreditTransfer mapCrediTransfer(DettaglioPagamentoType dettaglioPagamentoType) {
        BG0017CreditTransfer transfer = new BG0017CreditTransfer();

        dettaglioPagamento = dettaglioPagamentoType;

        String iban = mapBT84();
        if (iban != null) {
            transfer.getBT0084PaymentAccountIdentifier()
                    .add(new BT0084PaymentAccountIdentifier(iban));
        }

        String bic = mapBT86();
        if (bic != null) {
            transfer.getBT0086PaymentServiceProviderIdentifier()
                    .add(new BT0086PaymentServiceProviderIdentifier(bic));
        }

        return transfer;
    }

    private static String mapBT84() {
        return dettaglioPagamento.getIBAN();
    }

    private static String mapBT86() {
        return dettaglioPagamento.getBIC();
    }
}
