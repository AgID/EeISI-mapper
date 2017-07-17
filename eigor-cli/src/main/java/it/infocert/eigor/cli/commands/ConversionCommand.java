package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.cli.CliCommand;
import it.infocert.eigor.model.core.dump.CsvDumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ConversionCommand implements CliCommand {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final RuleRepository ruleRepository;
    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final Path inputInvoice;
    private final Path outputFolder;
    private final InputStream invoiceInSourceFormat;
    private final Boolean forceConversion;

    public ConversionCommand(
            RuleRepository ruleRepository,
            ToCenConversion toCen,
            FromCenConversion fromCen,
            Path inputInvoice,
            Path outputFolder,
            InputStream invoiceInSourceFormat,
            boolean forceConversion) {
        this.ruleRepository = ruleRepository;
        this.toCen = toCen;
        this.fromCen = fromCen;
        this.inputInvoice = inputInvoice;
        this.outputFolder = outputFolder;
        this.invoiceInSourceFormat = invoiceInSourceFormat;
        this.forceConversion = forceConversion;
    }

    /**
     * Execute toCen converter and fromCen converter.
     * Extract conversion result and rule validation report.
     * Generate files:
     * tocen-errors.csv
     * invoice-source.{extension} (clone of source invoice)
     * fromcen-errors.csv,
     * invoice-cen.csv,
     * invoice-target.{extension},
     * rule-report.csv
     *
     * @param out The system output.
     * @param err The system err.
     * @return 0 if success, 1 if IOException|SyntaxErrorInInvoiceFormatException
     */
    @Override
    public int execute(PrintStream out, PrintStream err) {
        
        InMemoryRuleReport ruleReport = new InMemoryRuleReport();
        File outputFolderFile;
        outputFolderFile = outputFolder.toFile();

        LogSupport logSupport = new LogSupport();
        logSupport.addLogger(new File(outputFolderFile, "invoice-transformation.log"));

        // Execute the conversion
        try {
            conversion(outputFolderFile, ruleReport, out);
        } catch (IOException | SyntaxErrorInInvoiceFormatException e) {
            log.error(e.getMessage(), e);
            return 1;
        } finally {
            logSupport.removeLogger();
        }

        out.println("Conversion file stored in folder " + outputFolderFile.getAbsolutePath());

        return 0;
    }

    private void conversion(File outputFolderFile, InMemoryRuleReport ruleReport, PrintStream out) throws SyntaxErrorInInvoiceFormatException, IOException {
        ConversionResult<BG0000Invoice> toCenResult = toCen.convert(invoiceInSourceFormat);
        BG0000Invoice cenInvoice = toCenResult.getResult();
        writeToCenErrorsToOutputStream(out, toCenResult, outputFolderFile);
        writeToCenErrorsToFile(toCenResult, outputFolderFile);
        cloneSourceInvoice(this.inputInvoice, outputFolderFile);

        if (toCenResult.hasErrors()) {
            if (isForceConversion()) {
                out.println("Conversion to CEN has encountered errors but will continue anyway.");
            } else {
                out.println("Conversion to CEN has encountered errors and will abort.");
                return;
            }
        }

        applyRulesToCenObject(cenInvoice, ruleReport);
        writeRuleReport(ruleReport, outputFolderFile, out);
        if (ruleReport.hasFailures()){
            if (isForceConversion()) {
                out.println("CEN rules validation has encountered errors but will continue anyway.");
            } else {
                out.println("CEN rules validation has encountered errors and will abort.");
                return;
            }
        }

        BinaryConversionResult conversionResult = fromCen.convert(cenInvoice);
        byte[] converted = conversionResult.getResult();
        
        writeFromCenErrors(out, conversionResult, outputFolderFile);
        
        writeCenInvoice(cenInvoice, outputFolderFile);

        if (conversionResult.hasErrors()) {
            if (isForceConversion()) {
                out.println("Conversion from CEN has encountered errors but will continue anyway.");
            } else {
                out.println("Conversion from CEN has encountered errors and will abort.");
                return;
            }
        }
        writeTargetInvoice(converted, outputFolderFile);
    }

    public boolean isForceConversion() {
        return forceConversion;
    }

    private void writeToCenErrorsToOutputStream(PrintStream out, ConversionResult conversionResult, File outputFolderFile) throws IOException {
        if (conversionResult.isSuccessful()) {
            out.println("To Cen Conversion was successful!");
        } else {
            out.println( String.format("To Cen Conversion finished, but %d issues have occured.", conversionResult.getIssues().size()) );
            List<ConversionIssue> errors = conversionResult.getIssues();
            for (int i = 0; i < errors.size(); i++) {
                ConversionIssue e = errors.get(i);
                out.println( String.format("%d) Error: %s", i+1, e.getMessage()));
            }
            out.println("For more information see 'tocen-errors.csv'.");
        }
    }

    private void writeToCenErrorsToFile(ConversionResult conversionResult, File outputFolderFile) throws IOException {
        if (!conversionResult.isSuccessful()) {
            List<ConversionIssue> errors = conversionResult.getIssues();
            String data = toCsvFileContent(errors);
            File toCenErrors = new File(outputFolderFile, "tocen-errors.csv");
            FileUtils.writeStringToFile(toCenErrors, data);
        }
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


    private void writeFromCenErrors(PrintStream out, BinaryConversionResult conversionResult, File outputFolderFile) throws IOException {
        if (conversionResult.isSuccessful()) {
            out.println("From Cen Conversion was successful!");
        } else {
            out.println("From Cen Conversion finished, but some issues have occured:");
            List<ConversionIssue> errors = conversionResult.getIssues();

            // writes from-cen errors csv
            File fromCenErrors = new File(outputFolderFile, "fromcen-errors.csv");
            FileUtils.writeStringToFile(fromCenErrors, toCsvFileContent(errors));

            for (ConversionIssue e : errors) {
                out.println("Error: " + e.getMessage());
            }
            out.println("For more information see 'fromcen-errors.csv'.");

        }
    }

    private void writeCenInvoice(BG0000Invoice cenInvoice, File outputFolderFile) throws IOException {
        Visitor v = new CsvDumpVisitor();
        cenInvoice.accept(v);
        FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString());
    }

    private void writeTargetInvoice(byte[] targetInvoice, File outputFolderFile) throws IOException {
        String extension = fromCen.extension();
        while (!extension.isEmpty() && extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        File outfile = new File(outputFolderFile, "invoice-target." + extension);
        FileUtils.writeByteArrayToFile(outfile, targetInvoice);
    }

    private void writeRuleReport(InMemoryRuleReport ruleReport, File outputFolderFile, PrintStream out) throws IOException {
        File outreport = new File(outputFolderFile, "rule-report.csv");
        FileUtils.writeStringToFile(outreport, ruleReport.dump());

        List<Map.Entry<RuleOutcome, Rule>> errors = ruleReport.getErrorsAndFailures();
        for (int i = 0; i < errors.size(); i++) {
            Map.Entry<RuleOutcome, Rule> e = errors.get(i);
            out.println( String.format("%d) Rule: %s", i+1, e.getKey().description()));
        }
    }

    private void cloneSourceInvoice(Path invoiceFile, File outputFolder) throws IOException {
        String invoiceName = invoiceFile.toFile().getName();
        int lastDotPosition = invoiceName.lastIndexOf('.');
        String extension = null;
        if (lastDotPosition != -1 && lastDotPosition < invoiceName.length() - 1) {
            extension = invoiceName.substring(lastDotPosition + 1);
        }
        invoiceName = "invoice-source" + ((extension != null) ? "." + extension : "");
        FileUtils.copyFile(invoiceFile.toFile(), new File(outputFolder, invoiceName));
    }


    private String toCsvFileContent(List<ConversionIssue> errors) {
        StringBuffer toCenErrorsCsv = new StringBuffer("Error,Reason\n");
        for (ConversionIssue e : errors) {
            toCenErrorsCsv.append(e.getMessage()).append(",").append(e.getCause()).append("\n");
        }
        return toCenErrorsCsv.toString();
    }

}
