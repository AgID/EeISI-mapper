package it.infocert.eigor.converter.fattpa2cen.mapping;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.BG03PrecedingInvoiceReferenceMapper;
import it.infocert.eigor.converter.fattpa2cen.models.DatiDocumentiCorrelatiType;
import it.infocert.eigor.converter.fattpa2cen.models.DatiGeneraliType;
import it.infocert.eigor.model.core.model.BG0003PrecedingInvoiceReference;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG03PrecedingInvoiceReferenceMapperTest {

    private List<DatiDocumentiCorrelatiType> list;
    private BG0003PrecedingInvoiceReference precedingInvoiceReference;

    @Before
    public void setUp() throws Exception {
        DatiDocumentiCorrelatiType documentiCorrelati = mock(DatiDocumentiCorrelatiType.class);
        DatiGeneraliType datiGenerali = mock(DatiGeneraliType.class);

        when(documentiCorrelati.getIdDocumento()).thenReturn("IdDoc");

        list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(documentiCorrelati);
        }

        when(datiGenerali.getDatiFattureCollegate()).thenReturn(list);
        when(documentiCorrelati.getData()).thenReturn(new XMLGregorianCalendarImpl(new GregorianCalendar(2017, 3, 4)));

        precedingInvoiceReference = BG03PrecedingInvoiceReferenceMapper.mapPrecedingInvoiceReferenceMapper(datiGenerali);
    }

    @Test
    public void bt25Test() throws Exception {
        for (DatiDocumentiCorrelatiType dati : list) {
            verify(dati, times(4)).getIdDocumento();
        }

        assertEquals("IdDoc",
                precedingInvoiceReference.getBT0025PrecedingInvoiceReference().get(0).toString());
    }

    @Test
    public void bt26Test() throws Exception {
        for (DatiDocumentiCorrelatiType dati : list) {
            verify(dati, times(4)).getData();
        }

        assertEquals("2017-04-04",
                precedingInvoiceReference.getBT0026PrecedingInvoiceIssueDate().get(0).toString());
    }
}