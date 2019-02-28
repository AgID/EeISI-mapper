package it.infocert.eigor.cli.commands;

import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.DebugConversionCallback;
import it.infocert.eigor.api.conversion.DumpIntermediateCenInvoiceAsCsvCallback;
import it.infocert.eigor.api.conversion.ObservableConversion;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.cli.CliCommand;
import it.infocert.eigor.converter.cen2xmlcen.CenToXmlCenConverter;
import it.infocert.eigor.converter.cen2xmlcen.DumpIntermediateCenInvoiceAsCenXmlCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConversionCommand implements CliCommand {

    private final EigorConfiguration configuration;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final RuleRepository ruleRepository;
    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final Path inputInvoice;
    private final Path outputFolder;
    private final InputStream invoiceInSourceFormat;
    private final Boolean forceConversion;
    private final boolean runIntermediateValidation;

    public static class ConversionCommandBuilder {
        private RuleRepository ruleRepository;
        private ToCenConversion toCen;
        private FromCenConversion fromCen;
        private Path inputInvoice;
        private Path outputFolder;
        private InputStream invoiceInSourceFormat;
        private Boolean forceConversion;
        private EigorConfiguration configuration;
        private Boolean runIntermediateValidation;

        public ConversionCommandBuilder() {
            runIntermediateValidation = false;
            forceConversion = false;
        }

        public ConversionCommandBuilder(
                RuleRepository ruleRepository,
                ToCenConversion toCen,
                FromCenConversion fromCen,
                Path inputInvoice,
                Path outputFolder,
                InputStream invoiceInSourceFormat,
                EigorConfiguration configuration) {
            this.ruleRepository = checkNotNull( ruleRepository );
            this.toCen = checkNotNull( toCen );
            this.fromCen = checkNotNull( fromCen );
            this.inputInvoice = checkNotNull( inputInvoice );
            this.outputFolder = checkNotNull( outputFolder );
            this.invoiceInSourceFormat = checkNotNull( invoiceInSourceFormat );
            this.configuration = checkNotNull( configuration );
        }

        public ConversionCommandBuilder setRuleRepository(RuleRepository ruleRepository) {
            this.ruleRepository = ruleRepository;
            return this;
        }

        public ConversionCommandBuilder setToCen(ToCenConversion toCen) {
            this.toCen = toCen;
            return this;
        }

        public ConversionCommandBuilder setFromCen(FromCenConversion fromCen) {
            this.fromCen = fromCen;
            return this;
        }

        public ConversionCommandBuilder setInputInvoice(Path inputInvoice) {
            this.inputInvoice = inputInvoice;
            return this;
        }

        public ConversionCommandBuilder setOutputFolder(Path outputFolder) {
            this.outputFolder = outputFolder;
            return this;
        }

        public ConversionCommandBuilder setInvoiceInSourceFormat(InputStream invoiceInSourceFormat) {
            this.invoiceInSourceFormat = invoiceInSourceFormat;
            return this;
        }

        public ConversionCommandBuilder setForceConversion(boolean forceConversion) {
            this.forceConversion = forceConversion;
            return this;
        }

        public ConversionCommandBuilder setConfiguration(EigorConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public ConversionCommandBuilder setRunIntermediateValidation(boolean intermediateValidation) {
            this.runIntermediateValidation = intermediateValidation;
            return this;
        }

        public ConversionCommand build() {
            return new ConversionCommand(ruleRepository, toCen, fromCen, inputInvoice, outputFolder, invoiceInSourceFormat, forceConversion, configuration, runIntermediateValidation);
        }

    }

    private ConversionCommand(
            RuleRepository ruleRepository,
            ToCenConversion toCen,
            FromCenConversion fromCen,
            Path inputInvoice,
            Path outputFolder,
            InputStream invoiceInSourceFormat,
            boolean forceConversion, EigorConfiguration configuration, boolean runIntermediateValidation) {
        this.ruleRepository = checkNotNull( ruleRepository );
        this.toCen = checkNotNull( toCen );
        this.fromCen = checkNotNull( fromCen );
        this.inputInvoice = checkNotNull( inputInvoice );
        this.outputFolder = checkNotNull( outputFolder );
        this.invoiceInSourceFormat = checkNotNull( invoiceInSourceFormat );
        this.forceConversion = forceConversion;
        this.configuration = checkNotNull( configuration );
        this.runIntermediateValidation = runIntermediateValidation;
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

        new ObservableConversion(
                ruleRepository,
                toCen,
                fromCen,
                invoiceInSourceFormat,
                forceConversion,
                inputInvoice.toFile().getName(),

                new ConsoleOutputConversionCallback(this, out),
                new DebugConversionCallback(outputFolderFile),
                new DumpIntermediateCenInvoiceAsCsvCallback(outputFolderFile),
                new DumpIntermediateCenInvoiceAsCenXmlCallback(
                        outputFolderFile,
                        new CenToXmlCenConverter(configuration),runIntermediateValidation)
        ).conversion();

    }

    public boolean isForceConversion() {
        return forceConversion;
    }

}
