package it.infocert.eigor.cli;

import it.infocert.eigor.api.*;
import it.infocert.eigor.cli.commands.ConversionCommand;
import it.infocert.eigor.cli.commands.HelpCommand;
import it.infocert.eigor.cli.commands.ReportFailuereCommand;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static java.nio.file.StandardOpenOption.READ;

/**
 * These are the accepted parameters.
 * <pre>
 * --input
 *   path to the file of the invoice to transform.
 * --source
 *   format of the invoice specified with '--input', as 'fatt-pa', 'ubl', ...
 * --output
 *   path of the folder where the converted invoice will be stored along with other files.
 * --target
 *   format of the transformed invoice, support the same formats of '--source'.
 * </pre>
 */
public class JoptsimpleBasecCommandLineInterpreter implements CommandLineInterpreter {

    private final ToCenConversionRepository toCenConversionRepository;
    private final FromCenConversionRepository fromCenConversionRepository;
    private final RuleRepository ruleRepository;

    public JoptsimpleBasecCommandLineInterpreter(ToCenConversionRepository toCenConversionRepository, FromCenConversionRepository fromCenConversionRepository, RuleRepository ruleRepository) {
        this.toCenConversionRepository = toCenConversionRepository;
        this.fromCenConversionRepository = fromCenConversionRepository;
        this.ruleRepository = ruleRepository;
    }

    @Override
    public CliCommand parseCommandLine(String[] args) {

        // Parses command line
        // ===================================================
        OptionParser parser = new OptionParser();
        parser.accepts("input").withRequiredArg();
        parser.accepts("output").withRequiredArg();
        parser.accepts("source").withRequiredArg();
        parser.accepts("target").withRequiredArg();
        parser.accepts("force");
        OptionSet options = parser.parse(args);


        // Validates all params
        // ===================================================

        // if all params are missing the print the help
        if (!options.hasOptions()) {
            return new HelpCommand();
        }

        // input: path to input invoice
        Path inputInvoice;
        Path outputFolder;
        ToCenConversion toCen;
        FromCenConversion fromCen;
        InputStream invoiceInSourceFormat;
        boolean forceConversion;
        {

            if (!options.has("input")) {
                return new ReportFailuereCommand("Input file missing, please specify the path of the invoice to trasform with the --input parameter.");
            }

            inputInvoice = FileSystems.getDefault().getPath((String) options.valueOf("input"));
            if (Files.notExists(inputInvoice)) {
                return new ReportFailuereCommand("Input invoice '%s' does not exist.", inputInvoice);
            }
        }

        // output: path to output folder
        {

            if (!options.has("output")) {
                return new ReportFailuereCommand("Output folder missing, please specify the output path with the --output parameter.", inputInvoice);
            }

            outputFolder = FileSystems.getDefault().getPath((String) options.valueOf("output"));
            if (Files.notExists(outputFolder)) {
                return new ReportFailuereCommand("Output folder '%s' does not exist.", outputFolder);
            }
        }

        // source format: should be supported
        {

            if (!options.has("source")) {
                return new ReportFailuereCommand("Source format missing, please specify the format of the original invoice with the --source parameter.", inputInvoice);
            }

            String source = (String) options.valueOf("source");
            toCen = this.toCenConversionRepository.findConversionToCen(source);
            if (toCen == null) {
                Set<String> supportedFormats = toCenConversionRepository.supportedToCenFormats();
                return new ReportFailuereCommand("Source format '%s' is not supported. Please choose one among: %s.", source, supportedFormats);
            }
        }

        // target format: should be supported
        {

            if (!options.has("target")) {
                return new ReportFailuereCommand("Target format missing, please specify the format of the target invoice with the --target parameter.", inputInvoice);
            }

            String target = (String) options.valueOf("target");
            fromCen = fromCenConversionRepository.findConversionFromCen(target);
            if (fromCen == null) {
                Set<String> supportedFormats = fromCenConversionRepository.supportedFormats();
                return new ReportFailuereCommand("Target format '%s' is not supported. Please choose one among: %s.", target, supportedFormats);
            }
        }

        // force flag: force conversion to continue even if there are errors
        {
            forceConversion = options.has("force");
        }

        try {
            invoiceInSourceFormat = Files.newInputStream(inputInvoice, READ);
        } catch (IOException e) {
            return new ReportFailuereCommand(e.getMessage());
        }

        return new ConversionCommand(
                ruleRepository, toCen, fromCen, inputInvoice, outputFolder, invoiceInSourceFormat, forceConversion
        );
    }

}
