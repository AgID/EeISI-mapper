package it.infocert.eigor.cli.commands;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Add an appender to the logger to log an execution linked to an invoice.
 */
public class LogSupport {

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

    /**
     * Add an appender that logs to the given File.
     * Remember to remove the appender when it is not needed anymore with {@link LogSupport#removeLogger()}.
     */
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
        Thread loggingThread = Thread.currentThread();
        appender.addFilter(new Filter() {
            @Override
            public FilterReply decide(Object o) {
                return Thread.currentThread() == loggingThread ? FilterReply.ACCEPT : FilterReply.DENY;
            }
        });

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
