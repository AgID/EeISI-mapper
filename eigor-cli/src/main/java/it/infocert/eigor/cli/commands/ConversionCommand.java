package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.cli.CliCommand;
import it.infocert.eigor.model.core.dump.DumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by danidemi on 19/04/17.
 */
public class ConversionCommand implements CliCommand {

    private final RuleRepository ruleRepository;
    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final Path inputInvoice;
    private final Path outputFolder;
    private final InputStream invoiceInSourceFormat;

    public ConversionCommand(RuleRepository ruleRepository, ToCenConversion toCen, FromCenConversion fromCen, Path inputInvoice, Path outputFolder, InputStream invoiceInSourceFormat) {
        this.ruleRepository = ruleRepository;
        this.toCen = toCen;
        this.fromCen = fromCen;
        this.inputInvoice = inputInvoice;
        this.outputFolder = outputFolder;
        this.invoiceInSourceFormat = invoiceInSourceFormat;
    }

    /**
     * Execute toCen converter and fromCen converter.
     * Extract conversion result and rule validation report.
     * Generate fromcen-errors.csv, invoice-cen.csv, invoice-target.xml, rule-report.csv
     * @param out The system output.
     * @param err The system err.
     * @return 0 if success, 1 if IOException|SyntaxErrorInInvoiceFormatException
     */
    @Override
    public int execute(PrintStream out, PrintStream err) {

        InMemoryRuleReport ruleReport = new InMemoryRuleReport();
        File outputFolderFile;

        // Execute the conversion
        // ===================================================
        try {
            BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat);
            ruleRepository.rules().forEach(rule -> {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            });
            ConversionResult conversionResult = fromCen.convert(cenInvoice);
            byte[] converted = conversionResult.getResult();
            outputFolderFile = outputFolder.toFile();

            writeFromCenErrors(out, conversionResult, outputFolderFile);
            writeCenInvoice(cenInvoice, outputFolderFile);
            writeTargetInvoice(converted, outputFolderFile);
            writeRuleReport(ruleReport, outputFolderFile);

        } catch (IOException | SyntaxErrorInInvoiceFormatException e) {
            e.printStackTrace(err);
            return 1;
        }

        out.println("Conversion file stored in folder " + outputFolderFile.getAbsolutePath());

        return 0;
    }

    private void writeFromCenErrors(PrintStream out, ConversionResult conversionResult, File outputFolderFile) throws IOException {
        if(conversionResult.isSuccessful()){
            out.println("Conversion was successful!");
        }else {
            out.println("Conversion finished, but some errors have occured:");
            List<Exception> errors = conversionResult.getErrors();


            String fromCenErrorsCsv = "Error,Reason\n" +
                    errors.stream()
                            .map(x -> x.getMessage() + "," + x.getCause())
                            .collect(Collectors.joining("\n"));

            // writes from-cen errors csv
            File fromCenErrors = new File(outputFolderFile, "fromcen-errors.csv");
            FileUtils.writeStringToFile(fromCenErrors, fromCenErrorsCsv);

            for (Exception e : errors){
                out.println("Error: " + e.getMessage());
            }
            out.println("For more information see 'fromcen-errors.csv'.");
        }
    }

    private void writeCenInvoice(BG0000Invoice cenInvoice, File outputFolderFile) throws IOException {
        Visitor v = new DumpVisitor();
        cenInvoice.accept(v);
        FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString());
    }

    private void writeTargetInvoice(byte[] targetInvoice, File outputFolderFile) throws IOException {
        File outfile = new File(outputFolderFile, "invoice-target.xml");
        FileUtils.writeByteArrayToFile(outfile, targetInvoice);
    }

    private void writeRuleReport(InMemoryRuleReport ruleReport, File outputFolderFile) throws IOException {
        File outreport = new File(outputFolderFile, "rule-report.csv");
        FileUtils.writeStringToFile(outreport, ruleReport.dump());
    }

}
