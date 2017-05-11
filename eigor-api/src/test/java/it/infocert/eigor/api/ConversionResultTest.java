package it.infocert.eigor.api;

import org.junit.Test;

import static org.junit.Assert.*;


public class ConversionResultTest {

    @Test
    public void shouldCreateConversionResultWithoutErrors() {

        // given
        ConversionResult sut = new ConversionResult("result".getBytes());

        // then
        assertTrue( sut.getErrors().isEmpty() );
        assertTrue( sut.hasResult() );
        assertArrayEquals( "result".getBytes(), sut.getResult() );

    }

}