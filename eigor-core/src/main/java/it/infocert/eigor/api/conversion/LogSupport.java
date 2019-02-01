package it.infocert.eigor.api.conversion;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.LogManager;

/**
 * Add an appender to the logger to log an execution linked to an invoice.
 */
public class LogSupport {

    // inspired by http://stackoverflow.com/questions/19058722/creating-an-outputstreamappender-for-logback#19074027

    private final Logger log;
    private FileAppender appender;
    private final LoggerContext context;
    private boolean isLogbackSupportActive = false;

    public LogSupport(Class clazz) {
        final ILoggerFactory factory = getLoggerContext();
        if (factory != null) {
            context = (LoggerContext) factory;
            log = context.getLogger(clazz);
            ((ch.qos.logback.classic.Logger) log).setLevel(Level.ALL);
            isLogbackSupportActive = true;
            log.info("Logback successfully configured. A dedicated conversion logfile will be created for every conversion");
        } else {
            context = null;
            log = LoggerFactory.getLogger(LogSupport.class);
            log.warn("Logback was not found in the system, therefore the dedicated conversion logfile will not be created. Defaulting to standard logging.");
        }
    }

    public LogSupport() {
        final ILoggerFactory factory = getLoggerContext();
        if (factory != null) {
            context = (LoggerContext) factory;
            log = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            ((ch.qos.logback.classic.Logger) log).setLevel(Level.ALL);
            isLogbackSupportActive = true;
            log.info("Logback successfully configured. A dedicated conversion logfile will be created for every conversion");
        } else {
            context = null;
            log = LoggerFactory.getLogger(LogSupport.class);
            log.warn("Logback was not found in the system, therefore the dedicated conversion logfile will not be created. Defaulting to standard logging.");
        }
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

        if ("ch.qos.logback.classic.Logger".equalsIgnoreCase(log.getClass().getName())) {

            // Encoder
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern("%d{HH:mm:ss} %-5level %logger{36} - %msg%n");
            encoder.start();

            // FileAppender
            appender = new FileAppender<>();
            appender.setName("FileAppender");
            appender.setContext(context);
            appender.setEncoder(encoder);
            appender.setOutputStream(stream);
            appender.setFile(outputLog.getAbsolutePath());
            appender.setImmediateFlush(true);
            appender.start();
            final Thread loggingThread = Thread.currentThread();
            appender.addFilter(new Filter() {
                @Override
                public FilterReply decide(Object o) {
                    return Thread.currentThread() == loggingThread ? FilterReply.ACCEPT : FilterReply.DENY;
                }
            });
            List<ch.qos.logback.classic.Logger> logs = context.getLoggerList();
            for(int i=0; i<logs.size(); i++){
                ch.qos.logback.classic.Logger logger = context.getLogger(logs.get(i).getName());
                logger.setLevel(Level.ALL);
                if(!logger.getName().contains(".")) {
                    logger.addAppender(appender);
                }
                if(logger.getName().contains("ROOT")) {
                    logger.detachAppender("FileAppender");
                    logger.detachAppender("STDOUT");
                }
            }
        } else {
            log.warn("Logback not found in the system, cannot add a custom appender");
        }

    }

    public void removeLogger() {

        if (appender == null) {
            throw new IllegalArgumentException("Not yet added");
        }

        if (log instanceof ch.qos.logback.classic.Logger) {
            appender.stop();
            ((ch.qos.logback.classic.Logger) log).detachAppender(appender);
        } else {
            log.warn("Logback not found in the system, cannot remove custom appender");
        }
    }

    public boolean isLogbackSupportActive() {
        return isLogbackSupportActive;
    }

    private static ILoggerFactory getLoggerContext() {
        final ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        return "loggercontext".equalsIgnoreCase(factory.getClass().getSimpleName()) ? factory : null;
    }
}
