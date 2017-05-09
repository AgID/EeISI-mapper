package it.infocert.eigor.cli.commands;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import it.infocert.eigor.api.FromCenConversion;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
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
import java.util.List;

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

    @Override
    public int execute(PrintStream out, PrintStream err) {

        File outputFolderFile;
        outputFolderFile = outputFolder.toFile();

        LogSupport logSupport = new LogSupport();
        logSupport.addLogger(new File(outputFolderFile, "invoice-transformation.log"));



        InMemoryRuleReport ruleReport = new InMemoryRuleReport();

        // Execute the conversion
        // ===================================================
        try {
            conversion(outputFolderFile, ruleReport);

        } catch (IOException | SyntaxErrorInInvoiceFormatException e) {
            e.printStackTrace(err);
            return 1;
        } finally {
            logSupport.removeLogger();
        }

        out.println("Conversion file stored in folder " + outputFolderFile.getAbsolutePath());

        return 0;
    }

    private void conversion(File outputFolderFile, InMemoryRuleReport ruleReport) throws SyntaxErrorInInvoiceFormatException, IOException {
        BG0000Invoice cenInvoice = toCen.convert(invoiceInSourceFormat);
        List<Rule> rules = ruleRepository.rules();
        if(rules!=null) {
            rules.forEach(rule -> {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            });
        }
        byte[] converted = fromCen.convert(cenInvoice).getResult();


        // writes cen invoice
        Visitor v = new DumpVisitor();
        cenInvoice.accept(v);
        FileUtils.writeStringToFile(new File(outputFolderFile, "invoice-cen.csv"), v.toString());

        // writes target invoice
        String extension = fromCen.extension();
        while(!extension.isEmpty() && extension.startsWith(".")){
            extension = extension.substring(1);
        }
        File outfile = new File(outputFolderFile, "invoice-target." + extension);
        FileUtils.writeByteArrayToFile(outfile, converted);

        // writes report
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

            if(appender != null) {
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
            appender= new OutputStreamAppender<>();
            appender.setName( "OutputStream Appender" );
            appender.setContext(context);
            appender.setEncoder(encoder);
            appender.setOutputStream(stream);
            appender.setImmediateFlush(true);
            appender.start();


            log.addAppender(appender);

        }

        public void removeLogger() {

            if(appender==null) {
                throw new IllegalArgumentException("Not yet added");
            }

            appender.stop();
            log.detachAppender(appender);

            log.info( "text from logger");
        }
    }





}
