package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        IConversionIssue issueToReturn = ConversionIssue.newError(new EigorException("test", ErrorCode.Location.FATTPA_OUT, ErrorCode.Action.CONFIGURED_MAP, ErrorCode.Error.INVALID));
        ConversionResult<String> sut = new ConversionResult<>(Collections.singletonList(issueToReturn), "result with issues");

        // then
        List<IConversionIssue> errors = sut.getIssues();
        assertThat(
                errors, hasItem(issueToReturn));
        assertThat(sut.hasResult(), is(true));
        assertThat(sut.isSuccessful(), is(false));
        assertThat(sut.hasIssues(), is(true));

    }


    // This is why ConversionIssue / API needs to be rethought/adjusted in a next major release. See issue #234
    @Test
    public void conversionResultIssuesAreNotActuallyImmutableButHasIssuesMustStillWorkAsAdvertised() {

        List<IConversionIssue> issues = new ArrayList<>();

        IConversionIssue issueWarn = ConversionIssue.newWarning(new EigorException("warn", ErrorCode.Location.FATTPA_OUT, ErrorCode.Action.CONFIGURED_MAP, ErrorCode.Error.INVALID));
        issues.add(issueWarn);

        ConversionResult<String> sut = new ConversionResult<>(issues, "result");

        assertThat(sut.isSuccessful(), is(false));
        assertThat(sut.hasIssues(), is(false));


        IConversionIssue issueError = ConversionIssue.newError(new EigorException("error", ErrorCode.Location.FATTPA_OUT, ErrorCode.Action.CONFIGURED_MAP, ErrorCode.Error.INVALID));
        issues.add(issueError);

        assertThat(sut.hasIssues(), is(true));
    }


}

