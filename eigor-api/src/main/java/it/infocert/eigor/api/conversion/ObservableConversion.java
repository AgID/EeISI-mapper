package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;
import it.infocert.eigor.rules.RuleOutcomeAsConversionIssueAdapter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is the core algorithm that converts a given invoice to the desired format.
 * <h2>Callbacks</h2>
 * <p>
 * It is possible to attach an unlimited number of callbacks that get informed
 * about meaningful events such, among others:
 * <ul>
 *      <li>a conversion being started,</li>
 *      <li>a conversion completed with errors</li>
 *      <li>etc...</li>
 * </ul>
 * For details, please check {@link ConversionCallback}.
 * </p>
 */
public class ObservableConversion {

    private final RuleRepository ruleRepository;
    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final String invoiceFileName;
    private byte[] invoiceInSourceFormat;
    private final Boolean forceConversion;
    private final ArrayList<ConversionCallback> listeners;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ObservableConversion(RuleRepository ruleRepository, ToCenConversion toCen, FromCenConversion fromCen, InputStream invoiceInSourceFormat, boolean forceConversion, String invoiceFileName, List<ConversionCallback> listeners) {
        this.ruleRepository = checkNotNull( ruleRepository );
        this.toCen = checkNotNull( toCen );
        this.fromCen = checkNotNull( fromCen );

        checkNotNull( invoiceInSourceFormat, "The binary version of the invoice is mandatory." );
        try {
            this.invoiceInSourceFormat = IOUtils.toByteArray(invoiceInSourceFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.forceConversion = checkNotNull( forceConversion );
        this.listeners = new ArrayList<>( checkNotNull( listeners ) );
        checkArgument(invoiceFileName!=null && !invoiceFileName.isEmpty());
        this.invoiceFileName = invoiceFileName;
    }

    public BinaryConversionResult conversion() {

        // Whether to go on to the next step. When false, we should stop executing subsequent operations.
        boolean keepOnGoing = true;

        // The intermediate CEN invoice.
        BG0000Invoice cenInvoice = null;

        // The final converted invoice
        BinaryConversionResult conversionResult = null;

        final ConversionContext ctx = new ConversionContext();
        ctx.setForceConversion(forceConversion.booleanValue());
        ctx.setInvoiceInSourceFormat(invoiceInSourceFormat);
        ctx.setInvoiceFileName(invoiceFileName);
        ctx.setTargetInvoiceExtension(fromCen.extension());

        // The rule report
        InMemoryRuleReport ruleReport = null;

        List<IConversionIssue> issues = new ArrayList<>();


        try {
            // conversion start
            fireOnStartingConverionEvent(ctx);

            // 1st step XML -> CEN
            fireOnStartingToCenTranformationEvent(ctx);
            ConversionResult<BG0000Invoice> toCenResult = toCen.convert(new ByteArrayInputStream(invoiceInSourceFormat));
            ctx.setToCenResult(toCenResult);
            if (!toCenResult.hasIssues()) {
                fireOnSuccessfullToCenTranformationEvent(ctx);
            } else {
                fireOnFailedToCenConversion(ctx);
                issues.addAll( toCenResult.getIssues() );
                if (!forceConversion)
                    keepOnGoing = false;
            }

            // 2nd step CEN verification
            if (keepOnGoing) {
                fireOnStartingVerifyingCenRules(ctx);
                cenInvoice = toCenResult.getResult();
                ruleReport = new InMemoryRuleReport();
                applyRulesToCenObject(cenInvoice, ruleReport);
                ctx.setRuleReport(ruleReport);
                if (!ruleReport.hasFailures()) {
                    fireOnSuccessfullyVerifiedCenRules(ctx);
                } else {

                    for (Map.Entry<RuleOutcome, Rule> errorsAndFailure : ruleReport.getErrorsAndFailures()) {
                        issues.add( new RuleOutcomeAsConversionIssueAdapter(errorsAndFailure.getKey()) );
                    }

                    fireOnFailedVerifyingCenRules(ctx);
                    if (!forceConversion)
                        keepOnGoing = false;
                }
            }

            // 3rd step CEN -> XML
            if (keepOnGoing) {
                fireOnStartingFromCenTransformation(ctx);
                conversionResult = fromCen.convert(cenInvoice);
                ctx.setFromCenResult(conversionResult);
                if (!conversionResult.hasIssues()) {
                    fireOnSuccessfullFromCenTransformation(ctx);
                } else {
                    fireOnFailedFromCenTransformation(ctx);
                    issues.addAll( toCenResult.getIssues() );
                }
            }
        } catch (SyntaxErrorInInvoiceFormatException e) {
            issues.add(ConversionIssue.newError(e));
            fireOnUnexpectedException(e, ctx);
        }

        // anyhow, we inform the listeners we completed the transformation
        fireOnTerminatedConverion(ctx);

        return new BinaryConversionResult(conversionResult!=null ? conversionResult.getResult() : null, issues);

    }

    private void applyRulesToCenObject(BG0000Invoice cenInvoice, InMemoryRuleReport ruleReport) {
        List<Rule> rules;
        try {
            rules = ruleRepository.rules();
        } catch (MalformedRuleException e) {
            Map<String, String> invalidRules = e.getInvalidRules();

            for (Map.Entry<String, String> entry : invalidRules.entrySet()) {
                log.error(
                        String.format("Rule %s is malformed: %s. Rule expression should follow the pattern ${ expression } without any surrounding quotes,", entry.getKey(), entry.getValue())
                );
            }

            rules = e.getValidRules();
        }
        if (rules != null) {
            for (Rule rule : rules) {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            }

        }
    }
    
    private void fireOnStartingConverionEvent(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onStartingConversion(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnStartingToCenTranformationEvent(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onStartingToCenTranformation(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnSuccessfullToCenTranformationEvent(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onSuccessfullToCenTranformation(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnFailedToCenConversion(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onFailedToCenConversion(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }        
    }

    private void fireOnStartingVerifyingCenRules(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onStartingVerifyingCenRules(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnSuccessfullyVerifiedCenRules(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onSuccessfullyVerifiedCenRules(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnFailedVerifyingCenRules(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onFailedVerifingCenRules(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnStartingFromCenTransformation(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onStartingFromCenTransformation(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnSuccessfullFromCenTransformation(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onSuccessfullFromCenTransformation(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnFailedFromCenTransformation(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onFailedFromCenTransformation(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnUnexpectedException(Exception theE, ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onUnexpectedException(theE, ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    private void fireOnTerminatedConverion(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try{
                listener.onTerminatedConversion(ctx);
            }catch (Exception e){
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }




    public interface ConversionCallback {

        void onStartingConversion(ConversionContext ctx) throws Exception;

        void onStartingToCenTranformation(ConversionContext ctx) throws Exception;

        void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception;

        void onFailedToCenConversion(ConversionContext ctx) throws Exception;

        void onStartingVerifyingCenRules(ConversionContext ctx) throws Exception;

        void onSuccessfullyVerifiedCenRules(ConversionContext ctx) throws Exception;

        void onFailedVerifingCenRules(ConversionContext ctx) throws Exception;

        void onStartingFromCenTransformation(ConversionContext ctx) throws Exception;

        void onSuccessfullFromCenTransformation(ConversionContext ctx) throws Exception;

        void onFailedFromCenTransformation(ConversionContext ctx) throws Exception;

        void onUnexpectedException(Exception e, ConversionContext ctx) throws Exception;

        void onTerminatedConversion(ConversionContext ctx) throws Exception;

    }

    public static abstract class AbstractConversionCallback implements ConversionCallback {

        @Override
        public void onStartingConversion(ConversionContext ctx) throws Exception {}

        @Override
        public void onStartingToCenTranformation(ConversionContext ctx) throws Exception {}

        @Override
        public void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception {}

        @Override
        public void onFailedToCenConversion(ConversionContext ctx) throws Exception {}

        @Override public void onStartingVerifyingCenRules(ConversionContext ctx) throws Exception {}

        @Override public void onSuccessfullyVerifiedCenRules(ConversionContext ctx) throws Exception {}

        @Override public void onFailedVerifingCenRules(ConversionContext ctx) throws Exception {}

        @Override public void onStartingFromCenTransformation(ConversionContext ctx) throws Exception {}

        @Override
        public void onSuccessfullFromCenTransformation(ConversionContext ctx) throws Exception {}

        @Override
        public void onFailedFromCenTransformation(ConversionContext ctx) throws Exception {}

        @Override
        public void onUnexpectedException(Exception e, ConversionContext ctx) throws Exception {}

        @Override
        public void onTerminatedConversion(ConversionContext ctx) throws Exception {}

    }

    public static class ConversionContext {

        private ConversionResult<BG0000Invoice> toCenResult;
        private InMemoryRuleReport ruleReport;
        private BinaryConversionResult fromCenResult;
        private byte[] invoiceInSourceFormat;
        private boolean forceConversion;
        private String invoiceFileName;
        private String targetInvoiceExtension;

        /**
         * If XML->CEN transformation has already taken place, this returns the related conversion result,
         * {@literal null} otherwise.
         */
        public ConversionResult<BG0000Invoice> getToCenResult() {
            return toCenResult;
        }

        private void setToCenResult(ConversionResult<BG0000Invoice> toCenResult) {
            this.toCenResult = toCenResult;
        }

        /**
         * If CEN rule verification has already taken place, this returns the related report,
         * {@literal null} otherwise.
         */
        public RuleReport getRuleReport() {
            return ruleReport;
        }

        private void setRuleReport(InMemoryRuleReport ruleReport) {
            this.ruleReport = ruleReport;
        }

        /**
         * If CEN rule verification has already taken place, this returns the related result,
         * {@literal null} otherwise.
         */
        public BinaryConversionResult getFromCenResult() {
            return fromCenResult;
        }

        private void setFromCenResult(BinaryConversionResult fromCenResult) {
            this.fromCenResult = fromCenResult;
        }

        /**
         * Get the input invoice in source format. Always available.
         */
        public byte[] getInvoiceInSourceFormat() {
            return invoiceInSourceFormat;
        }

        private void setInvoiceInSourceFormat(byte[] invoiceInSourceFormat) {
            this.invoiceInSourceFormat = invoiceInSourceFormat;
        }

        /**
         * Whether the conversion will goes on until the end or will stop at the first error.
         */
        public boolean isForceConversion() {
            return forceConversion;
        }

        private void setForceConversion(boolean forceConversion) {
            this.forceConversion = forceConversion;
        }

        private void setInvoiceFileName(String invoiceFileName) {
            this.invoiceFileName = invoiceFileName;
        }

        /**
         * The name of the original file being converted.
         */
        public String getSourceInvoiceFileName() {
            return invoiceFileName;
        }

        /** The preferred extension for the destination invoice. */
        public String getTargetInvoiceExtension() {
            return targetInvoiceExtension;
        }

        private void setTargetInvoiceExtension(String targetInvoiceExtension) {
            this.targetInvoiceExtension = targetInvoiceExtension;
        }
    }

}
