package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG10PayeeMapper;
import it.infocert.eigor.converter.fattpa2cen.models.DettaglioPagamentoType;
import it.infocert.eigor.model.core.model.BG0010Payee;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG10PayeeMapperTest {

    private DettaglioPagamentoType dettaglioPagamento;
    private BG0010Payee payee;

    @Before
    public void setUp() throws Exception {
        dettaglioPagamento = mock(DettaglioPagamentoType.class);

        when(dettaglioPagamento.getBeneficiario()).thenReturn("Beneficiario");

        payee = BG10PayeeMapper.mapPayee(dettaglioPagamento);
    }

    @Test
    public void bt59Test() throws Exception {
        verify(dettaglioPagamento).getBeneficiario();

        assertEquals("Beneficiario",
                payee.getBT0059PayeeName().get(0).toString());
    }
}