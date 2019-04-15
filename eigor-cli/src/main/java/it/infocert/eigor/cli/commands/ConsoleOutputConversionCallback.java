package it.infocert.eigor.cli.commands;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.conversion.AbstractConversionCallback;
import it.infocert.eigor.api.conversion.ConversionContext;

import java.io.PrintStream;

/**
 * A {@link ConsoleOutputConversionCallback conversion callback}
 * that prints out useful information about the ongoing conversion.
 */
class ConsoleOutputConversionCallback extends AbstractConversionCallback {

    private final PrintStream out;

    public ConsoleOutputConversionCallback(ConversionCommand conversionCommand, PrintStream out) {
        this.out = Preconditions.checkNotNull(out);
    }

    @Override public void onStartingConversion(ConversionContext ctx) throws Exception {
        out.println("Starting conversion.");
    }

    @Override public void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception {
        out.println("Conversion to CEN completed successfully.");
    }

    @Override public void onFailedToCenConversion(ConversionContext ctx) throws Exception {
        if (ctx.isForceConversion()) {
            out.println("Conversion to CEN has encountered errors but will continue anyway.");
        } else {
            out.println("Conversion to CEN has encountered errors and will abort.");
        }
    }

    @Override public void onSuccessfullyVerifiedCenRules(ConversionContext ctx) throws Exception {
        out.println("CEN rules validation completed successfully.");
    }

    @Override public void onSuccessfullFromCenTransformation(ConversionContext ctx) throws Exception {
        out.println("Conversion from CEN completed successfully.");
    }

    @Override public void onFailedFromCenTransformation(ConversionContext ctx) throws Exception {
        BinaryConversionResult conversionResult = ctx.getFromCenResult();
        if (conversionResult.hasIssues()) {
            if (ctx.isForceConversion()) {
                out.println("Conversion from CEN has encountered errors but will continue anyway.");
            } else {
                out.println("Conversion from CEN has encountered errors and will abort.");
            }
        }
    }

    @Override
    public void onUnexpectedException(Exception e, ConversionContext ctx) throws Exception {
        out.println(e.getMessage());
    }
}
