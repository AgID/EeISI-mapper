package it.infocert.eigor.cli.commands;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.cli.CliCommand;
import it.infocert.eigor.model.core.dump.DumpVisitor;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.Visitor;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.List;

/**
 * Created by danidemi on 19/04/17.
 */
public class ConversionCommand implements CliCommand {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final RuleRepository ruleRepository;
    private final ToCenConversion toCen;
    private final FromCenConversion fromCen;
    private final Path inputInvoice;
    private final Path outputFolder;
    private final InputStream invoiceInSourceFormat;

    public ConversionCommand(
            RuleRepository ruleRepository,
            ToCenConversion toCen,
            FromCenConversion fromCen,
            Path inputInvoice,
            Path outputFolder,
            InputStream invoiceInSourceFormat) {
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
     * Generate files:
     *          invoice-source.{extension} (clone of source invoice)
     *          fromcen-errors.csv,
     *          invoice-cen.csv,
     *          invoice-target.{extension},
     *          rule-report.csv
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
        // ===================================================
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
        BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat);
        List<Rule> rules = ruleRepository.rules();
        if(rules!=null) {
            rules.forEach(rule -> {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            });
        }
        ConversionResult conversionResult = fromCen.convert(cenInvoice);
        byte[] converted = conversionResult.getResult();

        cloneSourceInvoice(this.inputInvoice, outputFolderFile);
        writeFromCenErrors(out, conversionResult, outputFolderFile);
        writeCenInvoice(cenInvoice, outputFolderFile);
        writeTargetInvoice(converted, outputFolderFile);
        writeRuleReport(ruleReport, outputFolderFile);
    }

    private void cloneSourceInvoice(Path invoiceFile, File outputFolder) throws IOException {
        String invoiceName = invoiceFile.toFile().getName();
        int lastDotPosition = invoiceName.lastIndexOf('.');
        String extension = null;
        if (lastDotPosition != -1 && lastDotPosition < invoiceName.length() - 1) {
            extension = invoiceName.substring(lastDotPosition+1);
        }
        invoiceName = "invoice-source" + ((extension != null) ? "." + extension : "");
        FileUtils.copyFile(invoiceFile.toFile(), new File(outputFolder, invoiceName));
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
        String extension = fromCen.extension();
        while(!extension.isEmpty() && extension.startsWith(".")){
            extension = extension.substring(1);
        }
        File outfile = new File(outputFolderFile, "invoice-target." + extension);
        FileUtils.writeByteArrayToFile(outfile, targetInvoice);
    }

    private void writeRuleReport(InMemoryRuleReport ruleReport, File outputFolderFile) throws IOException {
        File outreport = new File(outputFolderFile, "rule-report.csv");
        FileUtils.writeStringToFile(outreport, ruleReport.dump());
    }

    public static class LogSupport {

        // inspired by http://stackoverflow.com/questions/19058722/creating-an-outputstreamappender-for-logback#19074027


        private final ch.qos.logback.classic.Logger log;
        private OutputStreamAppender appender;
        private final LoggerContext context;

        public LogSupport(Class clazz) {
            context = (LoggerContext) LoggerFactory.getILoggerFactory();
            log = context.getLogger(clazz);
        }

        public LogSupport() {
            context = (LoggerContext) LoggerFactory.getILoggerFactory();
            log = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        }

        public void addLogger(File outputLog) {

            if (appender != null) {
                throw new IllegalStateException("Already added");
            }


            // Destination stream
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(outputLog);
            } catch (FileNotFoundException e) {
                log.error("An error occurred.", e);
            }


            // Encoder
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern("%d{HH:mm:ss} %-5level %logger{36} - %msg%n");
            encoder.start();

            // OutputStreamAppender
            appender = new OutputStreamAppender<>();
            appender.setName("OutputStream Appender");
            appender.setContext(context);
            appender.setEncoder(encoder);
            appender.setOutputStream(stream);
            appender.setImmediateFlush(true);
            appender.start();
//            appender.addFilter(new Filter() {
//                @Override
//                public FilterReply decide(Object o) {
//                    return null;
//                }
//            });


            log.addAppender(appender);

        }

        public void removeLogger() {

            if (appender == null) {
                throw new IllegalArgumentException("Not yet added");
            }

            appender.stop();
            log.detachAppender(appender);

            log.info("text from logger");
        }
    }

}
