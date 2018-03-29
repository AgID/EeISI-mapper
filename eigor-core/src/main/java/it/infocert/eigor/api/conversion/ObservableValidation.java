package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.*;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class ObservableValidation extends AbstractObservable {

    private final byte[] invoiceInSourceFormat;
    private final ToCenConversion toCen;
    private final String invoiceFileName;

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

    public ConversionResult<Void> validate() {
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
    }
}
