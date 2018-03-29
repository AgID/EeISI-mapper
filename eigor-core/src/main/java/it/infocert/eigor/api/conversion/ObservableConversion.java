package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
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
 * <li>a conversion being started,</li>
 * <li>a conversion completed with errors</li>
 * <li>etc...</li>
 * </ul>
 * For details, please check {@link ConversionCallback}.
 * </p>
 */
public class ObservableConversion extends AbstractObservable {

    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final String invoiceFileName;
    private byte[] invoiceInSourceFormat;
    private final Boolean forceConversion;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ObservableConversion(RuleRepository ruleRepository, ToCenConversion toCen, FromCenConversion fromCen, InputStream invoiceInSourceFormat, boolean forceConversion, String invoiceFileName, List<ConversionCallback> listeners) {
        super(checkNotNull(listeners), ruleRepository);
        this.toCen = checkNotNull(toCen);
        this.fromCen = checkNotNull(fromCen);

        checkNotNull(invoiceInSourceFormat, "The binary version of the invoice is mandatory.");
        try {
            this.invoiceInSourceFormat = IOUtils.toByteArray(invoiceInSourceFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.forceConversion = forceConversion;
        checkNotNull(invoiceFileName);
        checkArgument(!invoiceFileName.isEmpty());
        this.invoiceFileName = invoiceFileName;
    }

    public BinaryConversionResult conversion() {

        // Whether to go on to the next step. When false, we should stop executing subsequent operations.
        boolean keepOnGoing = true;

        // The intermediate CEN invoice.
        BG0000Invoice cenInvoice = null;

        // The final converted invoice
        BinaryConversionResult fromCenResult = null;

        final ConversionContext ctx = new ConversionContext();
        ctx.setForceConversion(forceConversion);
        ctx.setInvoiceInSourceFormat(invoiceInSourceFormat);
        ctx.setInvoiceFileName(invoiceFileName);
        ctx.setTargetInvoiceExtension(fromCen.extension());

        // The rule report
        InMemoryRuleReport ruleReport;

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
                issues.addAll(toCenResult.getIssues());
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
                        issues.add(new RuleOutcomeAsConversionIssueAdapter(errorsAndFailure.getKey()));
                    }

                    fireOnFailedVerifyingCenRules(ctx);
                    if (!forceConversion)
                        keepOnGoing = false;
                }
            }

            // 3rd step CEN -> XML
            if (keepOnGoing) {
                fireOnStartingFromCenTransformation(ctx);
                fromCenResult = fromCen.convert(cenInvoice);
                ctx.setFromCenResult(fromCenResult);
                if (!fromCenResult.hasIssues()) {
                    fireOnSuccessfullFromCenTransformation(ctx);
                } else {
                    fireOnFailedFromCenTransformation(ctx);
                    issues.addAll(fromCenResult.getIssues());
                }
            }
        } catch (SyntaxErrorInInvoiceFormatException e) {
            issues.add(ConversionIssue.newError(e));
            fireOnUnexpectedException(e, ctx);
        }

        // anyhow, we inform the listeners we completed the transformation
        fireOnTerminatedConversion(ctx);

        return new BinaryConversionResult(fromCenResult != null ? fromCenResult.getResult() : null, issues);

    }

}
