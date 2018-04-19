package com.infocert.eigor.api;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class Issues {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    EigorApi api;

    @Before public void initApi() throws IOException, ConfigurationException {
        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(tmp.newFolder())
                .enableForce()
                .build();
    }

    @Test
    public void issue245(){
        InputStream resource = getClass().getResourceAsStream("/issues/issue-245-fattpa.xml");
        Assert.assertNotNull(resource);

        ConversionResult<byte[]> convert = api.convert("fatturapa", "ubl", resource);

        KeepByErrorCode predicate = new KeepByErrorCode("UBL_OUT.XSD_VALIDATION.INVALID");
        Iterable<IConversionIssue> conversionIssues = Iterables.filter(convert.getIssues(), predicate);


        StringBuilder issuesDescription = null;
        boolean areThereIssues = conversionIssues.iterator().hasNext();

        if(areThereIssues){
            issuesDescription = new StringBuilder("\n\nIssues:\n\n");

            for (IConversionIssue issue : conversionIssues) {

                issuesDescription
                        .append( issue.getMessage() )
                        .append("\n")
                        .append("   ►►► ")
                        .append(issue.getCause()!=null ? issue.getCause().getMessage() : "no details")
                        .append("\n\n");
            }

            issuesDescription.append( new String(convert.getResult()) )
                    .append("\n\n");
        }

        assertTrue(issuesDescription.toString(), !areThereIssues);

    }

    private static class KeepByErrorCode implements Predicate<IConversionIssue> {
        private final String errorCode;

        public KeepByErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public boolean apply(@Nullable IConversionIssue input) {
            return input.getErrorMessage() != null
                && input.getErrorMessage().getErrorCode()!=null
                && input.getErrorMessage().getErrorCode().toString().equals(errorCode);
        }
    }
}
