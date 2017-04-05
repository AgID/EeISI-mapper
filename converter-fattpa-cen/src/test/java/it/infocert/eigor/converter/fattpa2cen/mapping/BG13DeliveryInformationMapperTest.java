package it.infocert.eigor.converter.fattpa2cen.mapping;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import it.infocert.eigor.converter.fattpa2cen.models.DatiTrasportoType;
import it.infocert.eigor.converter.fattpa2cen.models.IndirizzoType;
import it.infocert.eigor.model.core.model.BG0013DeliveryInformation;
import it.infocert.eigor.model.core.model.BG0015DeliverToAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { BG15DeliverToAddressMapper.class } )
@SuppressWarnings("ResultOfMethodCallIgnored")
public class BG13DeliveryInformationMapperTest {

    private DatiTrasportoType datiTrasporto;
    private BG0013DeliveryInformation information;
    private IndirizzoType indirizzoResa;

    @Before
    public void setUp() throws Exception {
        datiTrasporto = mock(DatiTrasportoType.class);
        indirizzoResa = mock(IndirizzoType.class);
        mockStatic(BG15DeliverToAddressMapper.class);
        XMLGregorianCalendarImpl calendar = new XMLGregorianCalendarImpl(new GregorianCalendar(2017, 3, 5));

        when(datiTrasporto.getDataOraConsegna()).thenReturn(calendar);
        when(datiTrasporto.getIndirizzoResa()).thenReturn(indirizzoResa);
        when(BG15DeliverToAddressMapper.mapDeliverToAddress(indirizzoResa)).thenReturn(mock(BG0015DeliverToAddress.class));

        information = BG13DeliveryInformationMapper.mapDeliveryInformation(datiTrasporto);
    }

    @Test
    public void bt72Test() throws Exception {
        verify(datiTrasporto).getDataOraConsegna();

        assertEquals("2017-04-05",
                information.getBT0072ActualDeliveryDate().get(0).toString());
    }
}