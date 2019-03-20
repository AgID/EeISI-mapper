package it.infocert.eigor.cli.commands;

import com.infocert.eigor.api.EigorApi;
import com.infocert.eigor.api.EigorApiBuilder;
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
import it.infocert.eigor.cli.Eigor;
import it.infocert.eigor.cli.EigorCli;
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
    private EigorApi api;

    private final String sourceFormat;
    private final String targetFormat;
    private final Path outputFolder;
    private final InputStream invoiceInSourceFormat;
    private final Boolean forceConversion;
    private final String invoiceInName;
    private final boolean runIntermediateValidation;

    public static class ConversionCommandBuilder {
        private String sourceFormat;
        private String targetFormat;
        private Path outputFolder;
        private InputStream invoiceInSourceFormat;
        private Boolean forceConversion;
        private Boolean runIntermediateValidation;
        private EigorConfiguration configuration;
        private String invoiceInName;
        private EigorApi api;

        public ConversionCommandBuilder() {
            runIntermediateValidation = false;
            forceConversion = false;
        }

        public ConversionCommandBuilder setSourceFormat(String sourceFormat) {
            this.sourceFormat = sourceFormat;
            return this;

        }

        public ConversionCommandBuilder setTargetFormat(String targetFormat) {
            this.targetFormat = targetFormat;
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

        public ConversionCommandBuilder setApi(EigorApi api) {
            this.api = api;
            return this;
        }

        public ConversionCommandBuilder setInvoiceInName(String invoiceInName) {
            this.invoiceInName = invoiceInName;
            return this;
        }

        public ConversionCommand build() {
            return new ConversionCommand(sourceFormat, targetFormat, outputFolder, invoiceInSourceFormat, forceConversion, configuration, runIntermediateValidation, api, invoiceInName);
        }

    }

    private ConversionCommand(
            String sourceFormat,
            String targetFormat,
            Path outputFolder,
            InputStream invoiceInSourceFormat,
            boolean forceConversion,
            EigorConfiguration configuration,
            boolean runIntermediateValidation,
            EigorApi api,
            String invoiceName) {
        this.sourceFormat = checkNotNull(sourceFormat);
        this.targetFormat = checkNotNull(targetFormat);
        this.outputFolder = checkNotNull(outputFolder);
        this.invoiceInSourceFormat = checkNotNull(invoiceInSourceFormat);
        this.forceConversion = forceConversion;
        this.configuration = checkNotNull(configuration);
        this.runIntermediateValidation = runIntermediateValidation;
        this.api = api;
        this.invoiceInName = invoiceName;
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
        File outputFolderFile = outputFolder.toFile();

        // Execute the conversion
        try {
            conversion(outputFolderFile, out);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 1;
        } finally {
            out.println("Conversion file stored in folder " + outputFolderFile.getAbsolutePath());
        }
        return 0;
    }

    private void conversion(File outputFolderFile, PrintStream out) {

        api.convert(sourceFormat, targetFormat, invoiceInSourceFormat, invoiceInName,
                new ConsoleOutputConversionCallback(this, out),
                new DebugConversionCallback(outputFolderFile),
                new DumpIntermediateCenInvoiceAsCsvCallback(outputFolderFile),
                new DumpIntermediateCenInvoiceAsCenXmlCallback(
                        outputFolderFile,
                        new CenToXmlCenConverter(configuration), runIntermediateValidation));
    }

    public boolean isForceConversion() {
        return forceConversion;
    }
}
