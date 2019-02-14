package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.RuleReport;
import it.infocert.eigor.api.utils.RuleReports;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.infocert.eigor.api.utils.ConversionIssueUtils.toCsv;

/**
 * This callback can be used to collect info that can result priceless in investigating conversion problems.
 * In a given folder it writes:
 * <ul>
 * <li>A copy of the original inoice to be converted.</li>
 * <li>A list of errors occurred during the ->CEN transformation.</li>
 * <li>A list of CEN validation errors.</li>
 * <li>A list of errors occurred during the CEN-> transformation.</li>
 * <li>The resulting transformed invoice.</li>
 * <li>A log related to the operations executed by the thread that took in charge this conversion.</li>
 * </ul>
 */
public class DebugConversionCallback extends AbstractConversionCallback {

    private final static Logger log = LoggerFactory.getLogger(DebugConversionCallback.class);
    public static final Charset ENCODING = checkNotNull(Charset.forName("UTF-8"));
    private final File outputFolderFile;
    private LogSupport logSupport = null;
    private final boolean enableLog = true;

    public DebugConversionCallback(File outputFolderFile) {
        this.outputFolderFile = outputFolderFile;
    }

    @Override
    public void onStartingConversion(ConversionContext ctx) throws Exception {

        // attach the logging for this conversion
        if (enableLog) {
            logSupport = new LogSupport();
            if (logSupport.isLogbackSupportActive()) {
                logSupport.addLogger(new File(outputFolderFile, "invoice-transformation.log"));
            }
        }
        cloneSourceInvoice(ctx.getInvoiceInSourceFormat(), outputFolderFile, ctx.getSourceInvoiceFileName());
    }

    @Override
    public void onSuccessfullToCenTranformation(ConversionContext ctx) throws Exception {
        writeToCenErrorsToFile(ctx.getToCenResult(), outputFolderFile);
    }

    @Override
    public void onFailedToCenConversion(ConversionContext ctx) throws Exception {
        writeToCenErrorsToFile(ctx.getToCenResult(), outputFolderFile);
    }

    @Override
    public void onSuccessfullyVerifiedCenRules(ConversionContext ctx) throws Exception {
        writeRuleReportToFile(ctx.getRuleReport(), outputFolderFile);
    }

    @Override
    public void onFailedVerifyingCenRules(ConversionContext ctx) throws Exception {
        writeRuleReportToFile(ctx.getRuleReport(), outputFolderFile);
    }

    @Override
    public void onSuccessfullFromCenTransformation(ConversionContext ctx) throws Exception {
        writeFromCenErrorsToFile(ctx.getFromCenResult(), outputFolderFile);
        String targetExtension = ctx.getTargetInvoiceExtension();
        writeTargetInvoice(ctx.getFromCenResult().getResult(), outputFolderFile, targetExtension);
    }

    @Override
    public void onFailedFromCenTransformation(ConversionContext ctx) throws Exception {
        writeFromCenErrorsToFile(ctx.getFromCenResult(), outputFolderFile);
        String targetExtension = ctx.getTargetInvoiceExtension();
        writeTargetInvoice(ctx.getFromCenResult().getResult(), outputFolderFile, targetExtension);
    }

    @Override
    public void onTerminatedConversion(ConversionContext ctx) throws Exception {
        if (logSupport != null) {
            try {
                logSupport.removeLogger();
            } catch (IllegalArgumentException ignored) {
            } //Not yet added exception
        }
    }

    private void cloneSourceInvoice(Path invoiceFile, File outputFolder) throws IOException {
        String invoiceName = invoiceFile.toFile().getName();
        invoiceName = nameOfFileForClonedInvoice(invoiceName);
        FileUtils.copyFile(invoiceFile.toFile(), new File(outputFolder, invoiceName));
    }

    private String nameOfFileForClonedInvoice(String invoiceName) {
        int lastDotPosition = invoiceName.lastIndexOf('.');
        String extension = null;
        if (lastDotPosition != -1 && lastDotPosition < invoiceName.length() - 1) {
            extension = invoiceName.substring(lastDotPosition + 1);
        }
        invoiceName = "invoice-source" + ((extension != null) ? "." + extension : "");
        return invoiceName;
    }

    private void cloneSourceInvoice(byte[] invoiceFile, File outputFolder, String originalInvoiceFileName) throws IOException {
        FileUtils.writeByteArrayToFile(
                new File(outputFolder, nameOfFileForClonedInvoice(originalInvoiceFileName)),
                invoiceFile);
    }

    private void writeToCenErrorsToFile(ConversionResult<?> conversionResult, File outputFolderFile) throws IOException {
        List<IConversionIssue> errors = conversionResult.getIssues();
        String data = toCsv(errors);
        File toCenErrors = new File(outputFolderFile, "tocen-errors.csv");
        FileUtils.writeStringToFile(toCenErrors, data, StandardCharsets.UTF_8);
    }

    private void writeFromCenErrorsToFile(BinaryConversionResult conversionResult, File outputFolderFile) throws IOException {
        // writes to file
        // writes from-cen errors csv
        List<IConversionIssue> errors = conversionResult.getIssues();
        File fromCenErrors = new File(outputFolderFile, "fromcen-errors.csv");
        FileUtils.writeStringToFile(fromCenErrors, toCsv(errors), ENCODING);
    }

    private void writeRuleReportToFile(RuleReport ruleReport, File outputFolderFile) throws IOException {
        File outreport = new File(outputFolderFile, "rule-report.csv");
        FileUtils.writeStringToFile(outreport, dump(ruleReport), ENCODING);
    }

    public String dump(RuleReport ruleReport) {
        return RuleReports.dump(ruleReport);
    }

    private void writeTargetInvoice(byte[] targetInvoice, File outputFolderFile, String targetInvoiceExtension) throws IOException {

        while (targetInvoiceExtension.startsWith(".")) targetInvoiceExtension = targetInvoiceExtension.substring(1);

        File outfile = new File(outputFolderFile, "invoice-target." + targetInvoiceExtension);
        final String content = new String(targetInvoice);
        FileUtils.writeStringToFile(outfile, content, StandardCharsets.UTF_8);
    }

    @Override
    public void onUnexpectedException(Exception e, ConversionContext ctx) throws Exception {
        log.error(e.getMessage(), e);
    }
}
