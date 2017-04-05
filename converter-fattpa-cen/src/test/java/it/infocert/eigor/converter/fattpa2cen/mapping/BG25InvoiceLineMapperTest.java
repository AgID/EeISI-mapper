package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.DettaglioLineeType;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BT0130InvoicedQuantityUnitOfMeasureCode;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG25InvoiceLineMapperTest {

    private DettaglioLineeType dettaglioLinee;
    private BG0025InvoiceLine invoiceLine;

    @Before
    public void setUp() throws Exception {
        dettaglioLinee = mock(DettaglioLineeType.class);

        when(dettaglioLinee.getNumeroLinea()).thenReturn(1);
        when(dettaglioLinee.getDescrizione()).thenReturn("Descrizione");
        when(dettaglioLinee.getQuantita()).thenReturn(new BigDecimal(5.0d));
        when(dettaglioLinee.getUnitaMisura()).thenReturn("ZONE_E57"); //TODO: fix this and implement access by bt118Test, common bt118Test and symbol

        invoiceLine = BG25InvoiceLineMapper.mapInvoiceLine(dettaglioLinee);
    }

    @Test
    public void bt126Test() throws Exception {
        verify(dettaglioLinee).getNumeroLinea();

        assertEquals("1",
                invoiceLine.getBT0126InvoiceLineIdentifier().get(0).toString());
    }

    @Test
    public void bt127Test() throws Exception {
        verify(dettaglioLinee).getDescrizione();

        assertEquals("Descrizione",
                invoiceLine.getBT0127InvoiceLineNote().get(0).toString());
    }

    @Test
    public void bt129Test() throws Exception {
        verify(dettaglioLinee).getQuantita();

        assertEquals("5.0",
                invoiceLine.getBT0129InvoicedQuantity().get(0).toString());
    }

    @Test
    public void bt130Test() throws Exception {
        verify(dettaglioLinee).getDescrizione();

        assertEquals(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.ZONE_E57),
                invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().get(0));
    }
}