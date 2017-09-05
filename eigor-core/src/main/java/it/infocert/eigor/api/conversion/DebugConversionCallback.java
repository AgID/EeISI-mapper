package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.utils.RuleReports;
import it.infocert.eigor.model.core.dump.CsvDumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This callback can be used to collect info that can result priceless in investigating conversion problems.
 * In a given folder it writes:
 * <ul>
 *     <li>A copy of the original inoice to be converted.</li>
 *     <li>A list of errors occurred during the ->CEN transformation.</li>
 *     <li>A list of CEN validation errors.</li>
 *     <li>A list of errors occurred during the CEN-> transformation.</li>
 *     <li>The resulting transformed invoice.</li>
 *     <li>A log related to the operations executed by the thread that took in charge this conversion.</li>
 * </ul>
 */
public class DebugConversionCallback extends ObservableConversion.AbstractConversionCallback {

    public static final Charset ENCODING = checkNotNull( Charset.forName("UTF-8") );
    private final File outputFolderFile;
    private LogSupport logSupport = null;
    private final boolean enableLog = true;

    public DebugConversionCallback(File outputFolderFile) {
        this.outputFolderFile = outputFolderFile;
    }

    @Override public void onStartingConversion(ObservableConversion.ConversionContext ctx) throws Exception {

        // attach the logging for this conversion
        if(enableLog) {
            logSupport = new LogSupport();
//            logSupport.addLogger(new File(outputFolderFile, "invoice-transformation.log")); //TODO Why is this needed?
        }
        cloneSourceInvoice(ctx.getInvoiceInSourceFormat(), outputFolderFile, ctx.getSourceInvoiceFileName());
    }

    @Override public void onSuccessfullToCenTranformation(ObservableConversion.ConversionContext ctx) throws Exception {
        writeToCenErrorsToFile(ctx.getToCenResult(), outputFolderFile);
        writeCenInvoice(ctx.getToCenResult().getResult(), outputFolderFile);
    }

    @Override public void onFailedToCenConversion(ObservableConversion.ConversionContext ctx) throws Exception {
        writeToCenErrorsToFile(ctx.getToCenResult(), outputFolderFile);
        writeCenInvoice(ctx.getToCenResult().getResult(), outputFolderFile);
    }

    @Override public void onSuccessfullyVerifiedCenRules(ObservableConversion.ConversionContext ctx) throws Exception {
        writeRuleReportToFile(ctx.getRuleReport(), outputFolderFile);
    }

    @Override public void onFailedVerifingCenRules(ObservableConversion.ConversionContext ctx) throws Exception {
        writeRuleReportToFile(ctx.getRuleReport(), outputFolderFile);
    }

    @Override public void onSuccessfullFromCenTransformation(ObservableConversion.ConversionContext ctx) throws Exception {
        writeFromCenErrorsToFile(ctx.getFromCenResult(), outputFolderFile);
        String targetExtension = ctx.getTargetInvoiceExtension();
        writeTargetInvoice(ctx.getFromCenResult().getResult(), outputFolderFile, targetExtension);
    }

    @Override public void onFailedFromCenTransformation(ObservableConversion.ConversionContext ctx) throws Exception {
        writeFromCenErrorsToFile(ctx.getFromCenResult(), outputFolderFile);
        String targetExtension = ctx.getTargetInvoiceExtension();
        writeTargetInvoice(ctx.getFromCenResult().getResult(), outputFolderFile, targetExtension);
    }

    @Override public void onTerminatedConversion(ObservableConversion.ConversionContext ctx) throws Exception {
        if(logSupport!=null) {
            logSupport.removeLogger();
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

    private void writeToCenErrorsToFile(ConversionResult conversionResult, File outputFolderFile) throws IOException {
        if (!conversionResult.isSuccessful()) {
            List<IConversionIssue> errors = conversionResult.getIssues();
            String data = toCsvFileContent(errors);
            File toCenErrors = new File(outputFolderFile, "tocen-errors.csv");
            FileUtils.writeStringToFile(toCenErrors, data);
        }
    }

    private void writeFromCenErrorsToFile(BinaryConversionResult conversionResult, File outputFolderFile) throws IOException {
        // writes to file
        if (!conversionResult.isSuccessful()) {
            // writes from-cen errors csv
            List<IConversionIssue> errors = conversionResult.getIssues();
            File fromCenErrors = new File(outputFolderFile, "fromcen-errors.csv");
            FileUtils.writeStringToFile(fromCenErrors, toCsvFileContent(errors), ENCODING);
        }
    }

    private String toCsvFileContent(List<IConversionIssue> errors) {
        StringBuffer toCenErrorsCsv = new StringBuffer("Error,Reason\n");
        for (IConversionIssue e : errors) {
            toCenErrorsCsv.append(e.getMessage()).append(",").append(e.getCause()).append("\n");
        }
        return toCenErrorsCsv.toString();
    }

    private void writeRuleReportToFile(RuleReport ruleReport, File outputFolderFile) throws IOException {
        File outreport = new File(outputFolderFile, "rule-report.csv");
        FileUtils.writeStringToFile(outreport, dump(ruleReport), ENCODING);
    }

    public String dump(RuleReport ruleReport) {
        return RuleReports.dump(ruleReport);
    }

    private void writeCenInvoice(BG0000Invoice cenInvoice, File outputFolderFile) throws IOException {
        Visitor v = new CsvDumpVisitor();
        cenInvoice.accept(v);
        FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString());
    }

    private void writeTargetInvoice(byte[] targetInvoice, File outputFolderFile, String targetInvoiceExtension) throws IOException {

        while(targetInvoiceExtension.startsWith(".")) targetInvoiceExtension = targetInvoiceExtension.substring(1);

        File outfile = new File(outputFolderFile, "invoice-target." + targetInvoiceExtension);
        FileUtils.writeByteArrayToFile(outfile, targetInvoice);
    }

}
