package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.models.*;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0005SellerPostalAddress;
import it.infocert.eigor.model.core.model.BG0006SellerContact;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BG05SellerPostalAddressMapper.class, BG06SellerContactMapper.class})
@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG04SellerMapperTest {

    private DatiAnagraficiCedenteType datiAnagrafici;
    private AnagraficaType anagrafica;
    private CedentePrestatoreType cedente;
    private BG0004Seller seller;
    private IscrizioneREAType iscrizioneREA;
    private IdFiscaleType idFiscale;


    @Before
    public void setUp() throws Exception {
        cedente = mock(CedentePrestatoreType.class);
        datiAnagrafici = mock(DatiAnagraficiCedenteType.class);
        anagrafica = mock(AnagraficaType.class);
        iscrizioneREA = mock(IscrizioneREAType.class);
        idFiscale = mock(IdFiscaleType.class);
        mockStatic(BG05SellerPostalAddressMapper.class);
        mockStatic(BG06SellerContactMapper.class);

        when(BG05SellerPostalAddressMapper.mapSellerPostalAddress(cedente)).thenReturn(mock(BG0005SellerPostalAddress.class));
        when(BG06SellerContactMapper.mapSellerContact(cedente)).thenReturn(mock(BG0006SellerContact.class));

        when(anagrafica.getDenominazione()).thenReturn("Denominazione");
        when(anagrafica.getCodEORI()).thenReturn("EORICode");
        when(datiAnagrafici.getCodiceFiscale()).thenReturn("XXXZZZ000111");
        when(iscrizioneREA.getNumeroREA()).thenReturn("REANumber");
        when(iscrizioneREA.getCapitaleSociale()).thenReturn(new BigDecimal(5.0d));
        when(idFiscale.getIdCodice()).thenReturn("Code");
        when(idFiscale.getIdPaese()).thenReturn("IT");

        when(cedente.getDatiAnagrafici()).thenReturn(datiAnagrafici);
        when(cedente.getIscrizioneREA()).thenReturn(iscrizioneREA);
        when(datiAnagrafici.getAnagrafica()).thenReturn(anagrafica);
        when(datiAnagrafici.getIdFiscaleIVA()).thenReturn(idFiscale);
        when(datiAnagrafici.getRegimeFiscale()).thenReturn(RegimeFiscaleType.fromValue("RF02"));

        seller = BG04SellerMapper.mapSeller(cedente);
    }

    @Test
    public void bt27DenomTest() throws Exception {
        verify(anagrafica, times(2)).getDenominazione();

        assertEquals("Denominazione",
                seller.getBT0027SellerName().get(0).toString());
    }

    @Test
    public void bt27NameTest() throws Exception {
        reset(anagrafica);
        when(anagrafica.getDenominazione()).thenReturn(null);
        when(anagrafica.getNome()).thenReturn("Nome");
        when(anagrafica.getCognome()).thenReturn("Cognome");
        seller = BG04SellerMapper.mapSeller(cedente);

        verify(anagrafica, times(1)).getDenominazione();
        verify(anagrafica).getNome();
        verify(anagrafica).getCognome();

        assertEquals("Nome Cognome",
                seller.getBT0027SellerName().get(0).toString());
    }

    @Test
    public void bt29FiscalCodeTest() throws Exception {
        verify(datiAnagrafici).getCodiceFiscale();

        assertEquals("XXXZZZ000111",
                seller.getBT0029SellerIdentifierAndSchemeIdentifier().get(0).toString());
    }

    @Test
    public void bt29REATest() throws Exception {
        verify(cedente, times(2)).getIscrizioneREA();
        verify(iscrizioneREA).getNumeroREA();

        assertEquals("REANumber",
                seller.getBT0029SellerIdentifierAndSchemeIdentifier().get(1).toString());
    }

    @Test
    public void bt30Test() throws Exception {
        verify(datiAnagrafici, times(2)).getAnagrafica();
        verify(anagrafica).getCodEORI();

        assertEquals("EORICode",
                seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier()
                        .get(0).toString());
    }

    @Test
    public void bt31NotNullTest() throws Exception {
        verify(datiAnagrafici).getIdFiscaleIVA();

        assertEquals("ITCode",
                seller.getBT0031SellerVatIdentifier().get(0).toString());
    }

    @Test
    public void bt31NullTest() throws Exception {
        reset(idFiscale);
        when(idFiscale.getIdPaese()).thenReturn(null);
        when(idFiscale.getIdCodice()).thenReturn(null);

        seller = BG04SellerMapper.mapSeller(cedente);

        verify(datiAnagrafici, times(2)).getIdFiscaleIVA();

        assertTrue(seller.getBT0031SellerVatIdentifier().isEmpty());
    }

    @Test
    public void bt32Test() throws Exception {
        verify(datiAnagrafici).getRegimeFiscale();

        assertEquals(RegimeFiscaleType.RF_02.name(),
                seller.getBT0032SellerTaxRegistrationIdentifier().get(0).toString());
    }

    @Test
    public void bt33Test() throws Exception {
        verify(cedente, times(2)).getIscrizioneREA();
        verify(iscrizioneREA).getCapitaleSociale();

        assertEquals(new BigDecimal(5.0d).toString(),
                seller.getBT0033SellerAdditionalLegalInformation().get(0).toString());
    }
}