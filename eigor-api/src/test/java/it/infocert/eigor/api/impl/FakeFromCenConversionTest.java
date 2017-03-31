package it.infocert.eigor.api.impl;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FakeFromCenConversionTest {

    @Test public void shouldSupportTheFakeFormatOnly() {

        FakeFromCenConversion sut = new FakeFromCenConversion();

        assertTrue( sut.support("fake") );
        assertFalse( sut.support("FAKE") );
        assertFalse( sut.support(" fake ") );
        assertFalse( sut.support(null) );

    }

}