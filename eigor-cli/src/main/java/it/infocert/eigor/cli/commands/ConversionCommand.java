package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.DebugConversionCallback;
import it.infocert.eigor.api.conversion.ObservableConversion;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.cli.CliCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        File outputFolderFile = outputFolder.toFile();

        // Execute the conversion
        try {
            conversion(outputFolderFile, ruleReport, out);
        } catch (IOException | SyntaxErrorInInvoiceFormatException e) {
            log.error(e.getMessage(), e);
            return 1;
        } finally {
            out.println("Conversion file stored in folder " + outputFolderFile.getAbsolutePath());
        }
        return 0;
    }

    private void conversion(File outputFolderFile, InMemoryRuleReport ruleReport, PrintStream out) throws SyntaxErrorInInvoiceFormatException, IOException {

        List<ObservableConversion.ConversionCallback> conversionCallbacks = new ArrayList<>();
        conversionCallbacks.add(new ConsoleOutputConversionCallback(this, out));
        conversionCallbacks.add(new DebugConversionCallback(outputFolderFile));

        String invoiceFileName = inputInvoice.toFile().getName();

        new ObservableConversion(
                ruleRepository,
                toCen,
                fromCen,
                invoiceInSourceFormat,
                forceConversion.booleanValue(), invoiceFileName, conversionCallbacks)
                .conversion();

    }

    public boolean isForceConversion() {
        return forceConversion;
    }

}
