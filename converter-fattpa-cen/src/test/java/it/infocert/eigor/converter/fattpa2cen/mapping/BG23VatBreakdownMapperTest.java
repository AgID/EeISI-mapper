package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG23VatBreakdownMapper;
import it.infocert.eigor.converter.fattpa2cen.models.DatiRiepilogoType;
import it.infocert.eigor.model.core.model.BG0023VatBreakdown;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG23VatBreakdownMapperTest {

    private DatiRiepilogoType datiRiepilogo;
    private BG0023VatBreakdown vatBreakdown;

    @Before
    public void setUp() throws Exception {
        datiRiepilogo = mock(DatiRiepilogoType.class);

//        when(datiRiepilogo.getNatura()).thenReturn(NaturaType.N_1); //TODO altro Enum che non c'entra niente col suo corrispettivo (Untdid5305)
        when(datiRiepilogo.getNatura()).thenReturn(null);
        when(datiRiepilogo.getAliquotaIVA()).thenReturn(new BigDecimal(5.0d));

        vatBreakdown = BG23VatBreakdownMapper.mapVatBreakdown(datiRiepilogo);
    }

    @Test
    public void bt118Test() throws Exception {
        verify(datiRiepilogo).getNatura();

        assertTrue(vatBreakdown.getBT0118VatCategoryCode().isEmpty()); //TODO rimpiazzare con assert corretto
    }

    @Test
    public void bt119Test() throws Exception {
        verify(datiRiepilogo).getAliquotaIVA();

        assertEquals("5.0",
                vatBreakdown.getBT0119VatCategoryRate().get(0).toString());
    }
}