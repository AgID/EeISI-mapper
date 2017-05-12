package it.infocert.eigor.api;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ConversionResultTest {

    @Test
    public void conversionResultWithoutErrors() {

        // given
        ConversionResult<String> sut = new ConversionResult<String>( "the result" );

        // then
        assertThat( sut.getErrors(), allOf( notNullValue(), emptyCollectionOf(Exception.class) ));
        assertThat( sut.hasResult(), is(true));
        assertThat( sut.isSuccessful(), is(true));

    }

    @Test
    public void conversionResultWithErrors() {

        // given
        Exception exceptionToReturn = new Exception();
        ConversionResult sut = new ConversionResult<String>(asList(exceptionToReturn), "result with errors" );

        // then
        List<Exception> errors = sut.getErrors();
        assertThat(
                errors, hasItem(exceptionToReturn));
        assertThat( sut.hasResult(), is(true));
        assertThat( sut.isSuccessful(), is(false));

    }

}

