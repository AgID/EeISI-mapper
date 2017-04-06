package it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated;

import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG21DocumentLevelChargesMapper;
import it.infocert.eigor.converter.fattpa2cen.models.*;
import it.infocert.eigor.model.core.model.BG0021DocumentLevelCharges;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG21DocumentLevelChargesMapperTest {

    private DatiGeneraliDocumentoType datiDocumento;
    private DatiCassaPrevidenzialeType datiCassa;
    private DatiBolloType datiBollo;
    private BG0021DocumentLevelCharges charges;
    private List<DatiCassaPrevidenzialeType> list;

    @Before
    public void setUp() throws Exception {
        datiDocumento = mock(DatiGeneraliDocumentoType.class);
        datiCassa = mock(DatiCassaPrevidenzialeType.class);
        datiBollo = mock(DatiBolloType.class);
        list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            list.add(datiCassa);
        }

        when(datiDocumento.getDatiBollo()).thenReturn(datiBollo);
        when(datiDocumento.getDatiCassaPrevidenziale()).thenReturn(list);
        when(datiCassa.getAlCassa()).thenReturn(new BigDecimal(5.0d));
//        when(datiCassa.getTipoCassa()).thenReturn(TipoCassaType.TC_01); //TODO mappa l'enum con il giusto corrispettivo in Untdid7161
        when(datiCassa.getTipoCassa()).thenReturn(null); //TODO rimuovere quando quanto scritto sopra è completo
        when(datiBollo.getBolloVirtuale()).thenReturn(BolloVirtualeType.SI);
        when(datiBollo.getImportoBollo()).thenReturn(new BigDecimal(5.0d));
    }

    @Test
    public void bt99NotNullTest() throws Exception {
        charges = BG21DocumentLevelChargesMapper.mapDocumentLevelCharges(datiDocumento);

        verify(datiBollo).getImportoBollo();

        assertEquals("5.0",
                charges.getBT0099DocumentLevelChargeAmount().get(0).toString());
    }

    @Test
    public void bt99NullTest() throws Exception {
        resetDatiDocumento();

        charges = BG21DocumentLevelChargesMapper.mapDocumentLevelCharges(datiDocumento);

        verify(datiBollo, never()).getImportoBollo();

        assertTrue(charges.getBT0099DocumentLevelChargeAmount().isEmpty());
    }

    @Test
    public void bt104NotNullTest() throws Exception {
        charges = BG21DocumentLevelChargesMapper.mapDocumentLevelCharges(datiDocumento);

        verify(datiBollo).getBolloVirtuale();

        assertEquals(BolloVirtualeType.SI.toString(),
                charges.getBT0104DocumentLevelChargeReason().get(0).toString());
    }

    @Test
    public void bt104NullTest() throws Exception {
        resetDatiDocumento();

        charges = BG21DocumentLevelChargesMapper.mapDocumentLevelCharges(datiDocumento);

        verify(datiBollo, never()).getBolloVirtuale();

        assertTrue(charges.getBT0104DocumentLevelChargeReason().isEmpty());
    }

    @Test
    public void bt105Test() throws Exception {
        charges = BG21DocumentLevelChargesMapper.mapDocumentLevelCharges(datiDocumento);

        verify(datiCassa, times(4)).getTipoCassa();

        assertTrue(charges.getBT0105DocumentLevelChargeReasonCode().isEmpty()); //TODO sostituire con il controllo appropriato
    }

    private void resetDatiDocumento() {
        reset(datiDocumento);
        when(datiDocumento.getDatiBollo()).thenReturn(null);
        when(datiDocumento.getDatiCassaPrevidenziale()).thenReturn(list);
    }
}