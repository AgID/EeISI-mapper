package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioPagamentoType;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class BG16PaymentInstructionsMapperTest {

    private DettaglioPagamentoType dettaglioPagamento;
    private List<DettaglioPagamentoType> dettaglioPagamentoList;

    @Before
    public void setUp() throws Exception {
        dettaglioPagamento = mock(DettaglioPagamentoType.class);
        dettaglioPagamentoList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            dettaglioPagamentoList.add(dettaglioPagamento);
        }
    }
}