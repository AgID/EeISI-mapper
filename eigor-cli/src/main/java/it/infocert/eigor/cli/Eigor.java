package it.infocert.eigor.cli;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.model.core.dump.DumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardOpenOption.READ;

public class Eigor {

    public static Logger log = LoggerFactory.getLogger(Eigor.class);

    public static void main(String[] args) {
        new Eigor().run(args);
    }

    void run(String[] args) {

        // Needed services
        // ===================================================
        ReflectionBasedRepository reflectionBasedRepository = new ReflectionBasedRepository();
        RuleRepository ruleRepository = reflectionBasedRepository;
        ToCenConversionRepository conversionRepository = reflectionBasedRepository;
        FromCenConversionRepository fromCenConversionRepository = reflectionBasedRepository;
        ToCenConversion toCen = null;
        FromCenConversion fromCen = null;
        Path inputInvoice = null;
        Path outputFolder = null;
        InputStream invoiceInSourceFormat = null;
        InMemoryRuleReport ruleReport = new InMemoryRuleReport();

        // Parses command line
        // ===================================================
        OptionParser parser = new OptionParser();
        parser.accepts( "input" ).withRequiredArg();
        parser.accepts( "output" ).withRequiredArg();
        parser.accepts( "source" ).withRequiredArg();
        parser.accepts( "target" ).withRequiredArg();
        OptionSet options = parser.parse( args );



        // Validates all params
        // ===================================================

        // if all params are missing the print the help
        if(!options.hasOptions()){
            try {
                String help = IOUtils.toString(getClass().getResourceAsStream("/help.txt"));
                System.out.println(help);
            } catch (IOException e) {

            }
        }

        // input: path to input invoice
        {

            if(!options.has("input")){
                System.err.println(String.format("Input file missing, please specify the path of the invoice to trasform with the --input parameter.", inputInvoice));
                return;
            }

            inputInvoice = FileSystems.getDefault().getPath((String) options.valueOf("input"));
            if (Files.notExists(inputInvoice)) {
                System.err.println(String.format("Input invoice '%s' does not exist.", inputInvoice));
                return;
            }
        }

        // output: path to output folder
        {

            if(!options.has("output")){
                System.err.println(String.format("Output folder missing, please specify the output path with the --output parameter.", inputInvoice));
                return;
            }

            outputFolder = FileSystems.getDefault().getPath((String) options.valueOf("output"));
            if (Files.notExists(outputFolder)) {
                System.err.println(String.format("Output folder '%s' does not exist.", outputFolder));
                return;
            }
        }

        // source format: should be supported
        {

            if(!options.has("source")){
                System.err.println(String.format("Source format missing, please specify the format of the original invoice with the --source parameter.", inputInvoice));
                return;
            }

            String source = (String) options.valueOf("source");
            toCen = reflectionBasedRepository.findConversionToCen(source);
            if (toCen == null) {
                System.err.println(String.format("Source format '%s' is not supported.", source));
            }
        }

        // target format: should be supported
        {

            if(!options.has("target")){
                System.err.println(String.format("Target format missing, please specify the format of the target invoice with the --target parameter.", inputInvoice));
                return;
            }

            String target = (String) options.valueOf("target");
            fromCen = reflectionBasedRepository.findConversionFromCen(target);
            if (fromCen == null) {
                System.err.println(String.format("Target format '%s' is not supported.", target));
            }
        }

        try {
            invoiceInSourceFormat = Files.newInputStream(inputInvoice, READ);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }



        // Execute the conversion
        // ===================================================
        try {
            BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat);
            ruleRepository.rules().forEach( rule -> {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store( ruleOutcome, rule );
            });
            byte[] converted = fromCen.convert(cenInvoice);


            File outputFolderFile = outputFolder.toFile();

            // writes cen invoice
            Visitor v = new DumpVisitor();
            cenInvoice.accept( v );
            FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString());

            // writes target invoice
            File outfile = new File(outputFolderFile, "invoice-target.xml");
            FileUtils.writeByteArrayToFile(outfile, converted);

            // writes report
            File outreport = new File(outputFolderFile, "rule-report.csv");
            FileUtils.writeStringToFile(outreport, ruleReport.dump());

        } catch (IOException | SyntaxErrorInInvoiceFormatException e) {
            e.printStackTrace(System.err);
        }

    }

}
