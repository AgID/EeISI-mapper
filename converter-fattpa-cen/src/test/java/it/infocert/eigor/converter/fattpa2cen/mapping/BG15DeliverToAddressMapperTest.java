package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG15DeliverToAddressMapper;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.model.BG0015DeliverToAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG15DeliverToAddressMapperTest {

    private IndirizzoType indirizzo;
    private BG0015DeliverToAddress address;

    @Before
    public void setUp() throws Exception {
        indirizzo = mock(IndirizzoType.class);

        when(indirizzo.getIndirizzo()).thenReturn("Indirizzo");
        when(indirizzo.getNumeroCivico()).thenReturn("5");
        when(indirizzo.getComune()).thenReturn("Comune");
        when(indirizzo.getCAP()).thenReturn("CAP");
        when(indirizzo.getProvincia()).thenReturn("Provincia");
        when(indirizzo.getNazione()).thenReturn("IT");

        address = BG15DeliverToAddressMapper.mapDeliverToAddress(indirizzo);
    }

    @Test
    public void bt75Test() throws Exception {
        verify(indirizzo).getIndirizzo();

        assertEquals("Indirizzo",
                address.getBT0075DeliverToAddressLine1().get(0).toString());
    }

    @Test
    public void bt76Test() throws Exception {
        verify(indirizzo).getNumeroCivico();

        assertEquals("5",
                address.getBT0076DeliverToAddressLine2().get(0).toString());

    }

    @Test
    public void bt77Test() throws Exception {
        verify(indirizzo).getComune();

        assertEquals("Comune",
                address.getBT0077DeliverToCity().get(0).toString());
    }

    @Test
    public void bt78Test() throws Exception {
        verify(indirizzo).getCAP();

        assertEquals("CAP",
                address.getBT0078DeliverToPostCode().get(0).toString());
    }

    @Test
    public void bt79Test() throws Exception {
        verify(indirizzo).getProvincia();

        assertEquals("Provincia",
                address.getBT0079DeliverToCountrySubdivision().get(0).toString());
    }

    @Test
    public void bt80Test() throws Exception {
        verify(indirizzo).getNazione();

        assertEquals("IT",
                address.getBT0080DeliverToCountryCode().get(0).toString());
    }
}