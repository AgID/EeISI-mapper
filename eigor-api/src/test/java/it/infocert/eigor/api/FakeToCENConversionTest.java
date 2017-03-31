package it.infocert.eigor.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FakeToCENConversionTest {

    @Test
    public void shouldSupportFakesFormat() {

        FakeToCENConversion sut = new FakeToCENConversion();
        assertTrue( sut.support("fake") );
        assertFalse( sut.support("FAKE") );
        assertFalse( sut.support(" fake ") );
        assertFalse( sut.support("fakeFormat") );

    }

}