package it.infocert.eigor.cli.commands;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ObservableConversion;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * A {@link ObservableConversion.ConversionCallback conversion callback}
 * that prints out useful information about the ongoing conversion.
 */
class ConsoleOutputConversionCallback extends ObservableConversion.AbstractConversionCallback {

    private final PrintStream out;

    public ConsoleOutputConversionCallback(ConversionCommand conversionCommand, PrintStream out) {
        this.out = Preconditions.checkNotNull(out);
    }

    @Override public void onStartingConversion(ObservableConversion.ConversionContext ctx) throws Exception {
        out.println("Starting conversion.");
    }

    @Override public void onSuccessfullToCenTranformation(ObservableConversion.ConversionContext ctx) throws Exception {
        out.println("Conversion to CEN completed successfully.");
    }

    @Override public void onFailedToCenConversion(ObservableConversion.ConversionContext ctx) throws Exception {
        writeToCenErrorsToOutputStream(out, ctx.getToCenResult());
        if (ctx.isForceConversion()) {
            out.println("Conversion to CEN has encountered errors but will continue anyway.");
        } else {
            out.println("Conversion to CEN has encountered errors and will abort.");
        }
    }

    @Override public void onSuccessfullyVerifiedCenRules(ObservableConversion.ConversionContext ctx) throws Exception {
        out.println("CEN rules validation completed successfully.");
        writeRuleReportToOutputStream(ctx.getRuleReport(), out);
    }

    @Override public void onFailedVerifingCenRules(ObservableConversion.ConversionContext ctx) throws Exception {
        writeRuleReportToOutputStream(ctx.getRuleReport(), out);
        if (ctx.getRuleReport().hasFailures()) {
            if (ctx.isForceConversion()) {
                out.println("CEN rules validation has encountered errors but will continue anyway.");
            } else {
                out.println("CEN rules validation has encountered errors and will abort.");
            }
        }
    }

    @Override public void onSuccessfullFromCenTransformation(ObservableConversion.ConversionContext ctx) throws Exception {
        out.println("Conversion from CEN completed successfully.");
    }

    @Override public void onFailedFromCenTransformation(ObservableConversion.ConversionContext ctx) throws Exception {
        BinaryConversionResult conversionResult = ctx.getFromCenResult();
        writeFromCenErrorsToOutStream(out, conversionResult);
        if (conversionResult.hasIssues()) {
            if (ctx.isForceConversion()) {
                out.println("Conversion from CEN has encountered errors but will continue anyway.");
            } else {
                out.println("Conversion from CEN has encountered errors and will abort.");
            }
        }
    }

    private void writeToCenErrorsToOutputStream(PrintStream out, ConversionResult conversionResult) throws IOException {
        if (conversionResult.isSuccessful()) {
            out.println("To Cen Conversion was successful!");
        } else {
            out.println(String.format("To Cen Conversion finished, but %d issues have occured.", conversionResult.getIssues().size()));
            List<IConversionIssue> errors = conversionResult.getIssues();
            for (int i = 0; i < errors.size(); i++) {
                IConversionIssue e = errors.get(i);
                out.println(String.format("%d) Error: %s", i + 1, e.getMessage()));
            }
            out.println("For more information see 'tocen-errors.csv'.");
        }
    }

    private void writeRuleReportToOutputStream(RuleReport ruleReport, PrintStream out) {
        // output stream
        List<Map.Entry<RuleOutcome, Rule>> errors = ruleReport.getErrorsAndFailures();
        for (int i = 0; i < errors.size(); i++) {
            Map.Entry<RuleOutcome, Rule> e = errors.get(i);
            out.println(String.format("%d) Rule: %s", i + 1, e.getKey().description()));
        }
    }

    private void writeFromCenErrorsToOutStream(PrintStream out, BinaryConversionResult conversionResult) {
        // writes to output stream
        if (conversionResult.isSuccessful()) {
            out.println("From Cen Conversion was successful!");
        } else {
            out.println("From Cen Conversion finished, but some issues have occured:");
            List<IConversionIssue> errors = conversionResult.getIssues();
            for (IConversionIssue e : errors) {
                out.println("Error: " + e.getMessage());
            }
            out.println("For more information see 'fromcen-errors.csv'.");

        }
    }

    @Override
    public void onUnexpectedException(Exception e, ObservableConversion.ConversionContext ctx) throws Exception {
        out.println(e.getMessage());
    }
}
