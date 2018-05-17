package com.infocert.eigor.api;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import org.junit.Assert;

import javax.annotation.Nullable;
import java.io.InputStream;

import static it.infocert.eigor.test.Utils.invoiceAsStream;

public class ConversionUtil {

    private final EigorApi api;

    public ConversionUtil(EigorApi api) {
        this.api = Preconditions.checkNotNull( api );
    }

    ConversionResult<byte[]> assertConversionWithoutErrors(String invoice, String source, String target) {
        InputStream invoiceStream = invoiceAsStream(invoice);
        ConversionResult<byte[]> convert = api.convert(source, target, invoiceStream);
        Assert.assertFalse( buildMsgForFailedAssertion(convert, new KeepAll()), convert.hasIssues() );
        return convert;
    }

    String buildMsgForFailedAssertion(ConversionResult<byte[]> convert, Predicate<IConversionIssue> predicate){
        Iterable<IConversionIssue> conversionIssues = Iterables.filter(convert.getIssues(), predicate);
        StringBuilder issuesDescription = new StringBuilder();
        boolean areThereIssues = conversionIssues.iterator().hasNext();
        if(areThereIssues){
            issuesDescription.append("\n\nIssues:\n\n");
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
        return issuesDescription.toString();
    }

    static class KeepByErrorCode implements Predicate<IConversionIssue> {
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

    static class KeepAll implements Predicate<IConversionIssue> {

        @Override
        public boolean apply(@Nullable IConversionIssue input) {
            return true;
        }
    }

}
