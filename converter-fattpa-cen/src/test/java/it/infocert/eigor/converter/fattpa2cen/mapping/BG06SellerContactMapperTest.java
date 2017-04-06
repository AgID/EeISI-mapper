package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG06SellerContactMapper;
import it.infocert.eigor.converter.fattpa2cen.models.CedentePrestatoreType;
import it.infocert.eigor.converter.fattpa2cen.models.ContattiType;
import it.infocert.eigor.model.core.model.BG0006SellerContact;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG06SellerContactMapperTest {

    private ContattiType contatti;
    private CedentePrestatoreType cedente;
    private BG0006SellerContact sellerContact;

    @Before
    public void setUp() throws Exception {
        ContattiType contattiMock = mock(ContattiType.class);
        CedentePrestatoreType cedenteMock = mock(CedentePrestatoreType.class);

        when(contattiMock.getTelefono()).thenReturn("3401697790");
        when(contattiMock.getEmail()).thenReturn("test@example.com");

        when(cedenteMock.getContatti()).thenReturn(contattiMock);

        contatti = contattiMock;
        cedente = cedenteMock;

        sellerContact = BG06SellerContactMapper.mapSellerContact(cedente);
    }

    @Test
    public void nullContattiTest() throws Exception {
        reset(cedente);
        when(cedente.getContatti()).thenReturn(null);
        sellerContact = BG06SellerContactMapper.mapSellerContact(cedente);

        assertNull(sellerContact);
    }

    @Test
    public void bt42NotNullTest() throws Exception {
        verify(contatti).getTelefono();

        assertEquals("3401697790",
                sellerContact.getBT0042SellerContactTelephoneNumber().get(0).toString());
    }

    @Test
    public void bt42NullTest() throws Exception {
        reset(contatti);
        when(contatti.getTelefono()).thenReturn(null);
        sellerContact = BG06SellerContactMapper.mapSellerContact(cedente);

        assertTrue(sellerContact.getBT0041SellerContactPoint().isEmpty());
    }

    @Test
    public void bt43NotNullTest() throws Exception {
        verify(contatti).getEmail();

        assertEquals("test@example.com",
                sellerContact.getBT0043SellerContactEmailAddress().get(0).toString());
    }

    @Test
    public void bt43NullTest() throws Exception {
        reset(contatti);
        when(contatti.getEmail()).thenReturn(null);
        sellerContact = BG06SellerContactMapper.mapSellerContact(cedente);

        assertTrue(sellerContact.getBT0043SellerContactEmailAddress().isEmpty());
    }
}