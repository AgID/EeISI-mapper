package it.infocert.eigor.api.impl;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FakeToCenConversionTest {

    @Test
    public void shouldSupportFakesFormat() {

        FakeToCenConversion sut = new FakeToCenConversion();
        assertTrue( sut.support("fake") );
        assertFalse( sut.support("FAKE") );
        assertFalse( sut.support(" fake ") );
        assertFalse( sut.support("fakeFormat") );

    }

}