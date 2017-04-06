package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG08BuyerPostalAddressMapper;
import it.infocert.eigor.converter.fattpa2cen.models.CessionarioCommittenteType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.model.BG0008BuyerPostalAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG08BuyerPostalAddressMapperTest {

    private CessionarioCommittenteType cessionario;
    private IndirizzoType sede;
    private BG0008BuyerPostalAddress postalAddress;

    @Before
    public void setUp() throws Exception {
        cessionario = mock(CessionarioCommittenteType.class);
        sede = mock(IndirizzoType.class);

        when(sede.getIndirizzo()).thenReturn("Indirizzo");
        when(sede.getNumeroCivico()).thenReturn("5");
        when(sede.getComune()).thenReturn("Comune");
        when(sede.getCAP()).thenReturn("CAP");
        when(sede.getProvincia()).thenReturn("Provincia");
        when(sede.getNazione()).thenReturn("IT");
        when(cessionario.getSede()).thenReturn(sede);

        postalAddress = BG08BuyerPostalAddressMapper.mapBuyerPostalAddress(cessionario);
    }

    @Test
    public void bt50Test() throws Exception {
        verify(sede).getIndirizzo();

        assertEquals("Indirizzo",
                postalAddress.getBT0050BuyerAddressLine1().get(0).toString());
    }

    @Test
    public void bt51Test() throws Exception {
        verify(sede).getNumeroCivico();

        assertEquals("5",
                postalAddress.getBT0051BuyerAddressLine2().get(0).toString());
    }

    @Test
    public void bt52Test() throws Exception {
        verify(sede).getComune();

        assertEquals("Comune",
                postalAddress.getBT0052BuyerCity().get(0).toString());
    }

    @Test
    public void bt53Test() throws Exception {
        verify(sede).getCAP();

        assertEquals("CAP",
                postalAddress.getBT0053BuyerPostCode().get(0).toString());
    }

    @Test
    public void bt54Test() throws Exception {
        verify(sede).getProvincia();

        assertEquals("Provincia",
                postalAddress.getBT0054BuyerCountrySubdivision().get(0).toString());
    }

    @Test
    public void bt55Test() throws Exception {
        verify(sede).getNazione();

        assertEquals("IT",
                postalAddress.getBT0055BuyerCountryCode().get(0).toString());
    }
}