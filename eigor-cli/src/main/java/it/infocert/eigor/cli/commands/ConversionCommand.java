package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
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

    @Override
    public int execute(PrintStream out, PrintStream err) {

        InMemoryRuleReport ruleReport = new InMemoryRuleReport();
        File outputFolderFile;

        // Execute the conversion
        // ===================================================
        try {
            BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat).getResult();
            ruleRepository.rules().forEach(rule -> {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            });
            byte[] converted = fromCen.convert(cenInvoice).getResult();


            outputFolderFile = outputFolder.toFile();

            // writes cen invoice
            Visitor v = new DumpVisitor();
            cenInvoice.accept(v);
            FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString());

            // writes target invoice
            File outfile = new File(outputFolderFile, "invoice-target.xml");
            FileUtils.writeByteArrayToFile(outfile, converted);

            // writes report
            File outreport = new File(outputFolderFile, "rule-report.csv");
            FileUtils.writeStringToFile(outreport, ruleReport.dump());

        } catch (IOException | SyntaxErrorInInvoiceFormatException e) {
            e.printStackTrace(err);
            return 1;
        }

        out.println("Conversion file stored in folder " + outputFolderFile.getAbsolutePath());

        return 0;
    }

}
