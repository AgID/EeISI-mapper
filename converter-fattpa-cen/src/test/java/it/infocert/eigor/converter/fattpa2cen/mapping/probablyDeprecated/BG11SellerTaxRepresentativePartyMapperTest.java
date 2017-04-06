package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG11SellerTaxRepresentativePartyMapper;
import it.infocert.eigor.converter.fattpa2cen.models.AnagraficaType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiAnagraficiRappresentanteType;
import it.infocert.eigor.converter.fattpa2cen.models.IdFiscaleType;
import it.infocert.eigor.converter.fattpa2cen.models.RappresentanteFiscaleType;
import it.infocert.eigor.model.core.model.BG0011SellerTaxRepresentativeParty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG11SellerTaxRepresentativePartyMapperTest {

    private DatiAnagraficiRappresentanteType datiAnagrafici;
    private RappresentanteFiscaleType rappresentanteFiscale;
    private AnagraficaType anagrafica;
    private IdFiscaleType idFiscale;
    private BG0011SellerTaxRepresentativeParty party;

    @Before
    public void setUp() throws Exception {
        rappresentanteFiscale = mock(RappresentanteFiscaleType.class);
        datiAnagrafici = mock(DatiAnagraficiRappresentanteType.class);
        anagrafica = mock(AnagraficaType.class);
        idFiscale = mock(IdFiscaleType.class);

        when(rappresentanteFiscale.getDatiAnagrafici()).thenReturn(datiAnagrafici);
        when(datiAnagrafici.getAnagrafica()).thenReturn(anagrafica);
        when(datiAnagrafici.getIdFiscaleIVA()).thenReturn(idFiscale);
        when(anagrafica.getDenominazione()).thenReturn("Denominazione");
        when(idFiscale.getIdCodice()).thenReturn("Code");
        when(idFiscale.getIdPaese()).thenReturn("IT");

        party = BG11SellerTaxRepresentativePartyMapper.mapSellerTaxRepresentativeParty(rappresentanteFiscale);
    }

    @Test
    public void bt62DenomTest() throws Exception {
        verify(anagrafica, times(2)).getDenominazione();

        assertEquals("Denominazione",
                party.getBT0062SellerTaxRepresentativeName().get(0).toString());
    }

    @Test
    public void bt62NameTest() throws Exception {
        reset(anagrafica);
        when(anagrafica.getDenominazione()).thenReturn(null);
        when(anagrafica.getNome()).thenReturn("Name");
        when(anagrafica.getCognome()).thenReturn("Surname");

        party = BG11SellerTaxRepresentativePartyMapper.mapSellerTaxRepresentativeParty(rappresentanteFiscale);

        verify(anagrafica).getNome();
        verify(anagrafica).getCognome();

        assertEquals("Name Surname",
                party.getBT0062SellerTaxRepresentativeName().get(0).toString());
    }

    @Test
    public void bt63Test() throws Exception {
        verify(idFiscale).getIdCodice();
        verify(idFiscale).getIdPaese();

        assertEquals("ITCode",
                party.getBT0063SellerTaxRepresentativeVatIdentifier().get(0).toString());
    }
}