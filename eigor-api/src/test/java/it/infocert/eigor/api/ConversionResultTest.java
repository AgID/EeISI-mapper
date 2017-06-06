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
        ConversionResult<String> sut = new ConversionResult<String>("the result");

        // then
        assertThat(sut.getIssues(), allOf(notNullValue(), emptyCollectionOf(ConversionIssue.class)));
        assertThat(sut.hasResult(), is(true));
        assertThat(sut.isSuccessful(), is(true));

    }

    @Test
    public void conversionResultWithErrors() {

        // given
        ConversionIssue issueToReturn = ConversionIssue.newError(new Exception());
        ConversionResult sut = new ConversionResult<String>(asList(issueToReturn), "result with issues");

        // then
        List<ConversionIssue> errors = sut.getIssues();
        assertThat(
                errors, hasItem(issueToReturn));
        assertThat(sut.hasResult(), is(true));
        assertThat(sut.isSuccessful(), is(false));
        assertThat(sut.hasErrors(), is(true));

    }

}

