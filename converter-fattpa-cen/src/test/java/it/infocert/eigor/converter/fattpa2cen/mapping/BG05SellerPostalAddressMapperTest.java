package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.CedentePrestatoreType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BG05SellerPostalAddressMapperTest {

    private IndirizzoType sede;
    private CedentePrestatoreType cedente;

    @Before
    public void setUp() throws Exception {
        IndirizzoType mock = mock(IndirizzoType.class);
        CedentePrestatoreType mock1 = mock(CedentePrestatoreType.class);

        when(mock.getIndirizzo()).thenReturn("Indirizzo");
        when(mock.getNumeroCivico()).thenReturn("5");
        when(mock.getComune()).thenReturn("Comune");
        when(mock.getCAP()).thenReturn("CAP");
        when(mock.getProvincia()).thenReturn("Provincia");
        when(mock.getNazione()).thenReturn("IT");

        when(mock1.getSede()).thenReturn(mock);

        sede = mock;
        cedente = mock1;
    }

    @Test
    public void bt35Test() throws Exception {
        BG05SellerPostalAddressMapper.mapSellerPostalAddress(cedente);

        verify(sede).getIndirizzo();
    }
}