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
    private static EigorApi api = null;

    private final String sourceFormat;
    private final String targetFormat;
    private final Path outputFolder;
    private final InputStream invoiceInSourceFormat;
    private final Boolean forceConversion;
    private final boolean runIntermediateValidation;

    public static class ConversionCommandBuilder {
        private String sourceFormat;
        private String targetFormat;
        private Path outputFolder;
        private InputStream invoiceInSourceFormat;
        private Boolean forceConversion;
        private EigorConfiguration configuration;
        private Boolean runIntermediateValidation;

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

        public ConversionCommand build() {
            return new ConversionCommand(sourceFormat, targetFormat, outputFolder, invoiceInSourceFormat, forceConversion, configuration, runIntermediateValidation);
        }

    }

    private ConversionCommand(
            String sourceFormat,
            String targetFormat,
            Path outputFolder,
            InputStream invoiceInSourceFormat,
            boolean forceConversion,
            EigorConfiguration configuration,
            boolean runIntermediateValidation) {
        this.sourceFormat = checkNotNull(sourceFormat);
        this.targetFormat = checkNotNull(targetFormat);
        this.outputFolder = checkNotNull(outputFolder);
        this.invoiceInSourceFormat = checkNotNull(invoiceInSourceFormat);
        this.forceConversion = forceConversion;
        this.configuration = checkNotNull(configuration);
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

    private void conversion(File outputFolderFile, PrintStream out) throws Exception {

        api = forceConversion ? new EigorApiBuilder().enableForce().build() : new EigorApiBuilder().build();
        api.convert(sourceFormat, targetFormat, invoiceInSourceFormat,
                new ConsoleOutputConversionCallback(this, out),
                new DebugConversionCallback(outputFolderFile),
                new DumpIntermediateCenInvoiceAsCsvCallback(outputFolderFile),
                new DumpIntermediateCenInvoiceAsCenXmlCallback(
                        outputFolderFile,
                        new CenToXmlCenConverter(configuration), runIntermediateValidation));

//        new ObservableConversion(
//                ruleRepository,
//                toCen,
//                fromCen,
//                invoiceInSourceFormat,
//                forceConversion,
//                inputInvoice.toFile().getName(),
//
//                new ConsoleOutputConversionCallback(this, out),
//                new DebugConversionCallback(outputFolderFile),
//                new DumpIntermediateCenInvoiceAsCsvCallback(outputFolderFile),
//                new DumpIntermediateCenInvoiceAsCenXmlCallback(
//                        outputFolderFile,
//                        new CenToXmlCenConverter(configuration), runIntermediateValidation)
//        ).conversion();
    }

    public boolean isForceConversion() {
        return forceConversion;
    }
}
