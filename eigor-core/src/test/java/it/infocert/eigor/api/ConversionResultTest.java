package it.infocert.eigor.api;

import org.junit.Test;

import java.util.Collections;
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
        ConversionResult<String> sut = new ConversionResult<String>("the result");

        // then
        assertThat(sut.getIssues(), allOf(notNullValue(), emptyCollectionOf(IConversionIssue.class)));
        assertThat(sut.hasResult(), is(true));
        assertThat(sut.isSuccessful(), is(true));

    }

    @Test
    public void conversionResultWithErrors() {

        // given
        IConversionIssue issueToReturn = ConversionIssue.newError(new Exception());
        ConversionResult<String> sut = new ConversionResult<>(Collections.singletonList(issueToReturn), "result with issues");

        // then
        List<IConversionIssue> errors = sut.getIssues();
        assertThat(
                errors, hasItem(issueToReturn));
        assertThat(sut.hasResult(), is(true));
        assertThat(sut.isSuccessful(), is(false));
        assertThat(sut.hasIssues(), is(true));

    }

}

