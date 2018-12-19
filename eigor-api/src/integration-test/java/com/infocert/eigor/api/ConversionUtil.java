package com.infocert.eigor.api;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.AbstractConversionCallback;
import it.infocert.eigor.api.conversion.ConversionContext;
import it.infocert.eigor.model.core.dump.DumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static it.infocert.eigor.test.Utils.invoiceAsStream;

public class ConversionUtil {

    private final EigorApi api;

    public ConversionUtil(EigorApi api) {
        this.api = Preconditions.checkNotNull( api );
    }

    ConversionResult<byte[]> assertConversionWithoutErrors(String invoice, String source, String target) {
        Predicate<IConversionIssue> predicate = new KeepAll();
        return assertConversionWithoutErrors(invoice, source, target, predicate);
    }

    ConversionResult<byte[]> assertConversionWithoutErrors(String invoice, String source, String target, Predicate<IConversionIssue> errorsToKeep) {
        InputStream invoiceStream = invoiceAsStream(invoice);

        final BG0000Invoice[] intermediateInvoice = new BG0000Invoice[1];
        class MyConversionCallback extends AbstractConversionCallback {

            @Override
            public void onTerminatedConversion(ConversionContext ctx) throws Exception {
                ConversionResult<BG0000Invoice> toCenResult = ctx.getToCenResult();
                if(toCenResult.hasResult()){
                    intermediateInvoice[0] = toCenResult.getResult();
                }
            }
        }

        MyConversionCallback mcc = new MyConversionCallback();

        ConversionResult<byte[]> convert = api.convert(source, target, invoiceStream, mcc);

        List<IConversionIssue> issues = convert.getIssues().stream().filter( errorsToKeep ).collect(Collectors.toList());

        String messageInCaseOfFailedTest = buildMsgForFailedAssertion(convert, errorsToKeep, intermediateInvoice[0]);

        //Assert.assertTrue(messageInCaseOfFailedTest, issues.isEmpty() );
        return convert;
    }

    String buildMsgForFailedAssertion(ConversionResult<byte[]> convert, Predicate<IConversionIssue> predicate, BG0000Invoice intermediateCenInvoice){

        Iterable<IConversionIssue> conversionIssues = convert.getIssues().stream().filter( predicate ).collect(Collectors.toList());

        StringBuilder issuesDescription = new StringBuilder();
        boolean areThereIssues = conversionIssues.iterator().hasNext();
        if(areThereIssues){

            issuesDescription.append("\n\n====== Issues: ======\n\n");

            issuesDescription.append(msgForIssues(conversionIssues));

            issuesDescription.append("\n\n====== Intermediate CEN Invoice: ======\n\n");

            if(intermediateCenInvoice!=null) {
                issuesDescription.append(msgForIntermediateInvoice(intermediateCenInvoice));
            }else{
                issuesDescription.append("The conversion failed before producing any intermediate CEN invoice.");
            }

            issuesDescription.append("\n\n====== Converted Invoice: ======\n\n");

            issuesDescription.append(msgConvertedInvoice(convert))
                    .append("\n\n");
        }
        return issuesDescription.toString();
    }

    private String msgForIntermediateInvoice(BG0000Invoice cenInvoice) {
        DumpVisitor v = new DumpVisitor();
        cenInvoice.accept( v );
        return v.toString();
    }

    private String msgConvertedInvoice(ConversionResult<byte[]> convert) {
        return new String(convert.getResult());
    }

    private StringBuilder msgForIssues(Iterable<IConversionIssue> conversionIssues) {
        StringBuilder issuesDescription2 = new StringBuilder();
        for (IConversionIssue issue : conversionIssues) {
            issuesDescription2
                    .append( issue.getMessage() )
                    .append("\n")
                    .append("   ►►► ")
                    .append(issue.getCause()!=null ? issue.getCause().getMessage() : "no details")
                    .append("\n\n");
        }
        return issuesDescription2;
    }


    static class KeepByErrorCode implements Predicate<IConversionIssue> {
        private final String errorCode;

        public KeepByErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public boolean test(@Nullable IConversionIssue input) {
            return input.getErrorMessage() != null
                    && input.getErrorMessage().getErrorCode()!=null
                    && input.getErrorMessage().getErrorCode().toString().equals(errorCode);
        }
    }

    static class KeepAll implements Predicate<IConversionIssue> {

        @Override
        public boolean test(@Nullable IConversionIssue input) {
            return true;
        }
    }

    static class KeepXSDErrorsOnly implements Predicate<IConversionIssue> {

        @Override
        public boolean test(@Nullable IConversionIssue input) {
            try{
                return input.getErrorMessage().getErrorCode().getAction().toString().equals("XSD_VALIDATION");
            }catch(NullPointerException npe){
                return false;
            }
        }
    }

}
