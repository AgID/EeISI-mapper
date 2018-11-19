package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.RuleOutcomeAsConversionIssueAdapter;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class ObservableValidation extends AbstractObservable {

    private final byte[] invoiceInSourceFormat;
    private final ToCenConversion toCen;
    private final String invoiceFileName;
    private Predicate<IConversionIssue> isSyntaxIssue = i -> {
        if(Objects.nonNull(i.getErrorMessage()) && Objects.nonNull(i.getErrorMessage().getErrorCode())) {
            return ErrorCode.Action.XSD_VALIDATION.equals(i.getErrorMessage().getErrorCode().getAction());
        }
        return false;
    };
    private Predicate<IConversionIssue> isSemanticIssue = i -> {
        if(Objects.nonNull(i.getErrorMessage()) && Objects.nonNull(i.getErrorMessage().getErrorCode())) {
            return ErrorCode.Action.XSD_VALIDATION.equals(i.getErrorMessage().getErrorCode().getAction())
                    || ErrorCode.Action.SCH_VALIDATION.equals(i.getErrorMessage().getErrorCode().getAction());
        }
        return false;
    };

    public ObservableValidation(InputStream invoiceInSourceFormat, ToCenConversion toCen, String invoiceFileName, List<ConversionCallback> listeners, RuleRepository ruleRepository) {
        super(checkNotNull(listeners), ruleRepository);
        checkNotNull(invoiceInSourceFormat, "The binary version of the invoice is mandatory.");
        try {
            this.invoiceInSourceFormat = IOUtils.toByteArray(invoiceInSourceFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.toCen = toCen;
        this.invoiceFileName = invoiceFileName;
    }

    public ConversionResult<Void> validateSyntax() {
        return validate(isSyntaxIssue, false);
    }

    public ConversionResult<Void> validateSemantics() {
        return validate(isSemanticIssue, false);
    }

    public ConversionResult<Void> validate() {
        return validate(i -> true, true);
    }

    /*public ConversionResult<Void> validate() {
        final ConversionContext ctx = new ConversionContext();
        ctx.setForceConversion(true);
        ctx.setInvoiceInSourceFormat(invoiceInSourceFormat);
        ctx.setInvoiceFileName(invoiceFileName);

        final ArrayList<IConversionIssue> issues = new ArrayList<>();
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

                // 2nd step CEN verification
                fireOnStartingVerifyingCenRules(ctx);
                final BG0000Invoice invoice = toCenResult.getResult();
                final InMemoryRuleReport ruleReport = new InMemoryRuleReport();
                applyRulesToCenObject(invoice, ruleReport);
                ctx.setRuleReport(ruleReport);
                if (!ruleReport.hasFailures()) {
                    fireOnSuccessfullyVerifiedCenRules(ctx);
                } else {

                    for (Map.Entry<RuleOutcome, Rule> errorsAndFailure : ruleReport.getErrorsAndFailures()) {
                        issues.add(new RuleOutcomeAsConversionIssueAdapter(errorsAndFailure.getKey()));
                    }

                    fireOnFailedVerifyingCenRules(ctx);
                }
            }
        } catch (SyntaxErrorInInvoiceFormatException e) {
            issues.add(ConversionIssue.newError(e));
            fireOnUnexpectedException(e, ctx);
        }

        // anyhow, we inform the listeners we completed the transformation
        fireOnTerminatedConversion(ctx);

        return new ConversionResult<>(issues, null);
    }*/

    private ConversionResult<Void> validate(Predicate<IConversionIssue> isValidIssue, boolean runCenVerification) {
        final ConversionContext ctx = new ConversionContext();
        ctx.setForceConversion(true);
        ctx.setInvoiceInSourceFormat(invoiceInSourceFormat);
        ctx.setInvoiceFileName(invoiceFileName);

        final ArrayList<IConversionIssue> issues = new ArrayList<>();
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

                toCenResult.getIssues()
                        .stream()
                        .filter(isValidIssue)
                        .forEach(issues::add);

                // 2nd step CEN verification
                if(runCenVerification) {
                    fireOnStartingVerifyingCenRules(ctx);
                    final BG0000Invoice invoice = toCenResult.getResult();
                    final InMemoryRuleReport ruleReport = new InMemoryRuleReport();
                    applyRulesToCenObject(invoice, ruleReport);
                    ctx.setRuleReport(ruleReport);
                    if (!ruleReport.hasFailures()) {
                        fireOnSuccessfullyVerifiedCenRules(ctx);
                    } else {

                        for (Map.Entry<RuleOutcome, Rule> errorsAndFailure : ruleReport.getErrorsAndFailures()) {
                            issues.add(new RuleOutcomeAsConversionIssueAdapter(errorsAndFailure.getKey()));
                        }

                        fireOnFailedVerifyingCenRules(ctx);
                    }
                }
            }
        } catch (SyntaxErrorInInvoiceFormatException e) {
            issues.add(ConversionIssue.newError(e));
            fireOnUnexpectedException(e, ctx);
        }

        // anyhow, we inform the listeners we completed the transformation
        fireOnTerminatedConversion(ctx);

        return new ConversionResult<>(issues, null);
    }
}
