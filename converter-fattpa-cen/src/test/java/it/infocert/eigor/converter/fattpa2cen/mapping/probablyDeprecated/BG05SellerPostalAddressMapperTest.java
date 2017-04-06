package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG05SellerPostalAddressMapper;
import it.infocert.eigor.converter.fattpa2cen.models.CedentePrestatoreType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.model.BG0005SellerPostalAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG05SellerPostalAddressMapperTest {

    private IndirizzoType sede;
    private CedentePrestatoreType cedente;
    private BG0005SellerPostalAddress sellerPostalAddress;

    @Before
    public void setUp() throws Exception {
        sede = mock(IndirizzoType.class);
        cedente = mock(CedentePrestatoreType.class);

        when(sede.getIndirizzo()).thenReturn("Indirizzo");
        when(sede.getNumeroCivico()).thenReturn("5");
        when(sede.getComune()).thenReturn("Comune");
        when(sede.getCAP()).thenReturn("CAP");
        when(sede.getProvincia()).thenReturn("Provincia");
        when(sede.getNazione()).thenReturn("IT");

        when(cedente.getSede()).thenReturn(sede);

        sellerPostalAddress = BG05SellerPostalAddressMapper.mapSellerPostalAddress(cedente);
    }

    @Test
    public void bt35Test() throws Exception {
        verify(sede).getIndirizzo();

        assertEquals("Indirizzo",
                sellerPostalAddress.getBT0035SellerAddressLine1().get(0).toString());
    }

    @Test
    public void bt36Test() throws Exception {
        verify(sede).getNumeroCivico();

        assertEquals("5",
                sellerPostalAddress.getBT0036SellerAddressLine2().get(0).toString());
    }
    @Test
    public void bt37Test() throws Exception {
        verify(sede).getComune();

        assertEquals("Comune",
                sellerPostalAddress.getBT0037SellerCity().get(0).toString());
    }
    @Test
    public void bt38Test() throws Exception {
        verify(sede).getCAP();

        assertEquals("CAP",
                sellerPostalAddress.getBT0038SellerPostCode().get(0).toString());
    }
    @Test
    public void bt39Test() throws Exception {
        verify(sede).getProvincia();

        assertEquals("Provincia",
                sellerPostalAddress.getBT0039SellerCountrySubdivision().get(0).toString());
    }
    @Test
    public void bt40Test() throws Exception {
        verify(sede).getNazione();

        assertEquals("IT",
                sellerPostalAddress.getBT0040SellerCountryCode().get(0).toString());
    }
}