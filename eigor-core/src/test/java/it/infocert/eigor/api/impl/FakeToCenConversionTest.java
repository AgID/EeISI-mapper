package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FakeToCenConversionTest {

    @Test
    public void shouldSupportFakesFormat() {

        FakeToCenConversion sut = new FakeToCenConversion( mock(IReflections.class), mock(EigorConfiguration.class) );
        assertTrue( sut.support("fake") );
        assertFalse( sut.support("FAKE") );
        assertFalse( sut.support(" fake ") );
        assertFalse( sut.support("fakeFormat") );

    }

}