package it.infocert.eigor.api;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class BinaryConversionResultTest {

    @Test
    public void shouldNotHaveResultsWhenByteArrayIsEmpty() {

        // given
        BinaryConversionResult sut = new BinaryConversionResult(new byte[]{}, new ArrayList<>());

        // then
        assertFalse( sut.hasResult() );

    }

    @Test
    public void shouldHaveResultsWhenByteArrayIsNotEmpty() {

        // given
        BinaryConversionResult sut = new BinaryConversionResult(new byte[]{1,2,3}, new ArrayList<>());

        // then
        assertTrue( sut.hasResult() );

    }


    @Test
    public void shouldCreateConversionResultWithoutErrors() {

        // given
        BinaryConversionResult sut = new BinaryConversionResult("result".getBytes());

        // then
        assertTrue( sut.getErrors().isEmpty() );
        assertTrue( sut.hasResult() );
        assertArrayEquals( "result".getBytes(), sut.getResult() );

    }

}