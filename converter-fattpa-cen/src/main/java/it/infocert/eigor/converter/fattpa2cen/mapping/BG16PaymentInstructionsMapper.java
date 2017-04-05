package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.DatiPagamentoType;
import it.infocert.eigor.converter.fattpa2cen.models.DettaglioPagamentoType;
import it.infocert.eigor.converter.fattpa2cen.models.ModalitaPagamentoType;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.model.BG0016PaymentInstructions;
import it.infocert.eigor.model.core.model.BT0081PaymentMeansTypeCode;
import it.infocert.eigor.model.core.model.BT0083RemittanceInformation;

import java.util.List;

class BG16PaymentInstructionsMapper {


    private static List<DettaglioPagamentoType> dettaglioPagamentoList;

    static BG0016PaymentInstructions mapPaymentInstructions(DatiPagamentoType datiPagamentoType) {
        BG0016PaymentInstructions instructions = new BG0016PaymentInstructions();

        dettaglioPagamentoList = datiPagamentoType.getDettaglioPagamento();

        if (!dettaglioPagamentoList.isEmpty()) {
            for (DettaglioPagamentoType dettaglioPagamento : dettaglioPagamentoList) {

                instructions.getBG0017CreditTransfer()
                        .add(BG17CreditTransferMapper.mapCrediTransfer(dettaglioPagamento));

                instructions.getBT0081PaymentMeansTypeCode()
                        .add(new BT0081PaymentMeansTypeCode(Untdid4461PaymentMeansCode.valueOf(mapBT81(dettaglioPagamento).name())));

                instructions.getBT0083RemittanceInformation()
                        .add(new BT0083RemittanceInformation(mapBT83(dettaglioPagamento)));
            }
        }

        return instructions;
    }

    private static ModalitaPagamentoType mapBT81(DettaglioPagamentoType dettaglioPagamento) {
        return dettaglioPagamento.getModalitaPagamento();

    }

    private static String mapBT83(DettaglioPagamentoType dettaglioPagamento) {
        return dettaglioPagamento.getCodicePagamento();
    }
}
