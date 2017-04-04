package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.AnagraficaType;
import it.infocert.eigor.converter.fattpa2cen.models.CessionarioCommittenteType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiAnagraficiCessionarioType;
import it.infocert.eigor.converter.fattpa2cen.models.IdFiscaleType;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BG08BuyerPostalAddressMapper.class})
@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG07BuyerMapperTest {

    private DatiAnagraficiCessionarioType datiAnagrafici;
    private CessionarioCommittenteType cessionario;
    private AnagraficaType anagrafica;
    private IdFiscaleType idFiscale;
    private BG0007Buyer buyer;

    @Before
    public void setUp() throws Exception {
        cessionario = mock(CessionarioCommittenteType.class);
        datiAnagrafici = mock(DatiAnagraficiCessionarioType.class);
        anagrafica = mock(AnagraficaType.class);
        idFiscale = mock(IdFiscaleType.class);
        mockStatic(BG08BuyerPostalAddressMapper.class);

        when(datiAnagrafici.getAnagrafica()).thenReturn(anagrafica);
        when(datiAnagrafici.getIdFiscaleIVA()).thenReturn(idFiscale);
        when(datiAnagrafici.getCodiceFiscale()).thenReturn("FiscalCode");
        when(anagrafica.getDenominazione()).thenReturn("Denominazione");
        when(anagrafica.getCodEORI()).thenReturn("EORICode");
        when(cessionario.getDatiAnagrafici()).thenReturn(datiAnagrafici);

    }

    @Test
    public void bt44DenomTest() throws Exception {
        buyer = BG07BuyerMapper.mapBuyer(cessionario);
        verify(anagrafica, times(2)).getDenominazione();

        assertEquals("Denominazione",
                buyer.getBT0044BuyerName().get(0).toString());
    }

    @Test
    public void bt44NameTest() throws Exception {
        reset(anagrafica);
        when(anagrafica.getDenominazione()).thenReturn(null);
        when(anagrafica.getNome()).thenReturn("Nome");
        when(anagrafica.getCognome()).thenReturn("Cognome");

        BG0007Buyer buyer = BG07BuyerMapper.mapBuyer(cessionario);

        verify(anagrafica).getDenominazione();
        verify(anagrafica).getNome();
        verify(anagrafica).getCognome();

        assertEquals("Nome Cognome",
                buyer.getBT0044BuyerName().get(0).toString());
    }

    @Test
    public void bt46Test() throws Exception {
        buyer = BG07BuyerMapper.mapBuyer(cessionario);
        verify(datiAnagrafici).getCodiceFiscale();

        assertEquals("FiscalCode",
                buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().get(0).toString());
    }

    @Test
    public void bt47Test() throws Exception {
        buyer = BG07BuyerMapper.mapBuyer(cessionario);
        verify(datiAnagrafici, times(2)).getAnagrafica();
        verify(anagrafica).getCodEORI();

        assertEquals("EORICode",
                buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().get(0).toString());
    }

    @Test
    public void bt48NullTest() throws Exception {
        reset(datiAnagrafici);
        when(datiAnagrafici.getIdFiscaleIVA()).thenReturn(null);
        when(datiAnagrafici.getAnagrafica()).thenReturn(anagrafica);

        BG0007Buyer buyer = BG07BuyerMapper.mapBuyer(cessionario);

        verify(idFiscale, never()).getIdCodice();
        verify(idFiscale, never()).getIdPaese();

        assertTrue(buyer.getBT0048BuyerVatIdentifier().isEmpty());
    }

    @Test
    public void bt48NullCodeTest() throws Exception {
        when(idFiscale.getIdPaese()).thenReturn(null);
        when(idFiscale.getIdCodice()).thenReturn(null);
        buyer = BG07BuyerMapper.mapBuyer(cessionario);

        verify(idFiscale).getIdPaese();
        verify(idFiscale).getIdCodice();

        assertTrue(buyer.getBT0048BuyerVatIdentifier().isEmpty());
    }

    @Test
    public void bt48CodeTest() throws Exception {
        when(idFiscale.getIdPaese()).thenReturn("IT");
        when(idFiscale.getIdCodice()).thenReturn("Code");
        buyer = BG07BuyerMapper.mapBuyer(cessionario);

        verify(idFiscale).getIdPaese();
        verify(idFiscale).getIdCodice();

        assertEquals("ITCode",
                buyer.getBT0048BuyerVatIdentifier().get(0).toString());
    }
}