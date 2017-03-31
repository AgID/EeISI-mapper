package it.infocert.eigor.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FakeFromCenConverterTest {

    @Test public void shouldSupportTheFakeFormatOnly() {

        FakeFromCenConverter sut = new FakeFromCenConverter();

        assertTrue( sut.support("fake") );
        assertFalse( sut.support("FAKE") );
        assertFalse( sut.support(" fake ") );
        assertFalse( sut.support(null) );

    }

}