package it.infocert.eigor;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.DebugConversionCallback;
import it.infocert.eigor.api.conversion.ObservableConversion;
import it.infocert.eigor.api.io.Copier;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPA;
import it.infocert.eigor.converter.cii2cen.Cii2Cen;
import it.infocert.eigor.converter.ubl2cen.Ubl2Cen;
import it.infocert.eigor.rules.repositories.CardinalityRulesRepository;
import it.infocert.eigor.rules.repositories.CompositeRuleRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class LowLevelAPIUsage {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    Logger log = LoggerFactory.getLogger(getClass().getClass());

    File outputFolderFile;

    @Before
    public void setUp() throws IOException {
        outputFolderFile = tmp.newFolder();
        if(!outputFolderFile.exists()) outputFolderFile.mkdirs();
    }

    @Test public void lowLevelApiUsageSingleConversion() throws IOException, ConfigurationException {

        // needed support classes
        Reflections reflections = new Reflections("it.infocert");

        // load the eigor configuration
        EigorConfiguration configuration = new DefaultEigorConfigurationLoader().loadConfiguration();

        // "clone" the resources needed for each converter in a local file system
        File dest = new File(configuration.getMandatoryString("eigor.converterdata.path"));
        if(!dest.exists()) dest.mkdirs();
        {
            String pathSegment = "converter-cen-fattpa";
            new Copier( new File(dest, pathSegment) )
                    .withCallback(new Copier.Callback() {
                        @Override public void afterFileCopied(File file) throws IOException {
                            if(file.isFile() && file.getName().endsWith(".xslt")){
                                FileUtils.touch(file);
                            }
                        }
                    })
                    .copyFrom("/converterdata/" + pathSegment);
        }
        {
            String pathSegment = "converter-cii-cen";
            new Copier( new File(dest, pathSegment) )
                    .withCallback(new Copier.Callback() {
                        @Override public void afterFileCopied(File file) throws IOException {
                            if(file.isFile() && file.getName().endsWith(".xslt")){
                                FileUtils.touch(file);
                            }
                        }
                    })
                    .copyFrom("/converterdata/" + pathSegment);
        }
        {
            String pathSegment = "converter-ubl-cen";
            new Copier( new File(dest, pathSegment) )
                    .withCallback(new Copier.Callback() {
                        @Override public void afterFileCopied(File file) throws IOException {
                            if(file.isFile() && file.getName().endsWith(".xslt")){
                                FileUtils.touch(file);
                            }
                        }
                    })
                    .copyFrom("/converterdata/" + pathSegment);
        }

        // set up the rule repository
        Properties cardinalityRules = new Properties();
        cardinalityRules.load(checkNotNull( getClass().getResourceAsStream("/cardinality.properties") ));
        Properties cardinalityRules2 = new Properties();
        cardinalityRules2.load(checkNotNull( getClass().getResourceAsStream("/rules.properties") ));
        RuleRepository ruleRepository = new CompositeRuleRepository(
                new CardinalityRulesRepository(cardinalityRules),
                new IntegrityRulesRepository(cardinalityRules2)
        );

        // prepare the set of conversions to be supported by the api
        ConversionRepository conversionRepository =
                new ConversionRepository.Builder()
                        .register(new Cii2Cen(reflections, configuration))
                        .register(new Ubl2Cen(reflections, configuration))
                        .register(new Cen2FattPA(reflections, configuration))
                        .build();
        conversionRepository.configure();

        ObservableConversion.ConversionCallback callback = new DebugConversionCallback(outputFolderFile);

        // this retrieves the converters from the relate repository, it is likely the "format" values
        // would come from a different software module, i.e. the GUI.
        ToCenConversion toCen = conversionRepository.findConversionToCen("ubl");
        FromCenConversion fromCen = conversionRepository.findConversionFromCen("fatturapa");

        // this is, finally, the actual conversion
        InputStream invoice = new ByteArrayInputStream( "<invoice>xml</invoice>".getBytes() );
        BinaryConversionResult outcome = new ObservableConversion(
                ruleRepository,
                toCen,
                fromCen,
                invoice,
                false,
                "invoice",
                Arrays.asList(callback)).conversion();

        StringBuffer sb = new StringBuffer();
        if(outcome.hasResult()){
            sb.append(format("Result follows:\n" + new String(outcome.getResult())));
        }else{
            sb.append(format("Result is not available, please check the errors.\n"));
        }

        if(outcome.hasErrors()){
            List<IConversionIssue> issues = outcome.getIssues();
            for (int i=0; i<issues.size(); i++) {
                IConversionIssue iConversionIssue = issues.get(i);
                sb.append( format( "%d)\n\tmessage: %s\n\ttype:    %s\n",
                        i+1,
                        iConversionIssue.getMessage(),
                        iConversionIssue.isError() ? "ERROR" : "warn" ) );
            }
        }

        log.info(sb.toString());

    }

    @Test public void lowLevelApiUsageMultipleConversion() throws IOException, ConfigurationException, InterruptedException {

        List<String> ublInvoices = Arrays.asList(
            "/examples/ubl/ubl-tc434-example2-ita-cius-compliant.xml",
            "/examples/ubl/ubl-tc434-example9.1.xml",
            "/examples/ubl/ubl-tc434-example6.xml",
            "/examples/ubl/UBL-Invoice-2.1-Example-KO.xml",
            "/examples/ubl/ubl-tc434-example4.xml",
            "/examples/ubl/UBL-Invoice-2.1-Example-Trivial.xml",
            "/examples/ubl/ubl-tc434-example9-ita-cius-compliant.xml",
            "/examples/ubl/ubl-tc434-example1-CIUS-ITA.xml",
            "/examples/ubl/ubl-tc434-example7.xml",
            "/examples/ubl/ubl-oasis.xml",
            "/examples/ubl/UBL-Invoice-2.1-Example-ita-cius-compliant.xml",
            "/examples/ubl/ubl-tc434-example2.xml",
            "/examples/ubl/ubl-tc434-example5.xml",
            "/examples/ubl/ubl-tc434-example9.xml",
            "/examples/ubl/ubl-tc434-example9.1-ita-cius-compliant.xml",
            "/examples/ubl/ubl-tc434-example8-ita-cius-compliant.xml",
            "/examples/ubl/ubl-tc434-example8.xml",
            "/examples/ubl/UBL-Invoice-2.1-Example.xml",
            "/examples/ubl/ubl-tc434-example7-ita-cius-compliant.xml",
            "/examples/ubl/ubl-plain.xml",
            "/examples/ubl/ubl-tc434-example3.xml",
            "/examples/ubl/ubl-tc434-example1.xml"
        );

        // needed support classes
        Reflections reflections = new Reflections("it.infocert");

        // load the eigor configuration
        EigorConfiguration configuration = new DefaultEigorConfigurationLoader().loadConfiguration();

        // "clone" the resources needed for each converter in a local file system
        File dest = new File(configuration.getMandatoryString("eigor.converterdata.path"));
        if(!dest.exists()) dest.mkdirs();
        {
            String pathSegment = "converter-cen-fattpa";
            new Copier( new File(dest, pathSegment) )
                    .withCallback(new Copier.Callback() {
                        @Override public void afterFileCopied(File file) throws IOException {
                            if(file.isFile() && file.getName().endsWith(".xslt")){
                                FileUtils.touch(file);
                            }
                        }
                    })
                    .copyFrom("/converterdata/" + pathSegment);
        }
        {
            String pathSegment = "converter-cii-cen";
            new Copier( new File(dest, pathSegment) )
                    .withCallback(new Copier.Callback() {
                        @Override public void afterFileCopied(File file) throws IOException {
                            if(file.isFile() && file.getName().endsWith(".xslt")){
                                FileUtils.touch(file);
                            }
                        }
                    })
                    .copyFrom("/converterdata/" + pathSegment);
        }
        {
            String pathSegment = "converter-ubl-cen";
            new Copier( new File(dest, pathSegment) )
                    .withCallback(new Copier.Callback() {
                        @Override public void afterFileCopied(File file) throws IOException {
                            if(file.isFile() && file.getName().endsWith(".xslt")){
                                FileUtils.touch(file);
                            }
                        }
                    })
                    .copyFrom("/converterdata/" + pathSegment);
        }

        // set up the rule repository
        Properties cardinalityRules = new Properties();
        cardinalityRules.load(checkNotNull( getClass().getResourceAsStream("/cardinality.properties") ));
        Properties cardinalityRules2 = new Properties();
        cardinalityRules2.load(checkNotNull( getClass().getResourceAsStream("/rules.properties") ));
        final RuleRepository ruleRepository = new CompositeRuleRepository(
                new CardinalityRulesRepository(cardinalityRules),
                new IntegrityRulesRepository(cardinalityRules2)
        );

        // prepare the set of conversions to be supported by the api
        ConversionRepository conversionRepository =
                new ConversionRepository.Builder()
                        .register(new Cii2Cen(reflections, configuration))
                        .register(new Ubl2Cen(reflections, configuration))
                        .register(new Cen2FattPA(reflections, configuration))
                        .build();
        conversionRepository.configure();

        final ObservableConversion.ConversionCallback callback = new DebugConversionCallback(outputFolderFile);

        // this retrieves the converters from the relate repository, it is likely the "format" values
        // would come from a different software module, i.e. the GUI.
        final ToCenConversion toCen = conversionRepository.findConversionToCen("ubl");
        final FromCenConversion fromCen = conversionRepository.findConversionFromCen("fatturapa");

        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (final String ublInvoice : ublInvoices) {

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    long elapsed = System.currentTimeMillis();
                    StringBuffer sb = new StringBuffer();

                    try {
                        // this is, finally, the actual conversion
                        InputStream invoiceStream = getClass().getResourceAsStream(ublInvoice);
                        assertThat( "Could not be null.", invoiceStream, notNullValue() );

                        BinaryConversionResult outcome = new ObservableConversion(
                                ruleRepository,
                                toCen,
                                fromCen,
                                invoiceStream,
                                false,
                                "invoice",
                                Arrays.asList(callback)).conversion();

                        sb = new StringBuffer();
                        if(outcome.hasResult()){
                            sb.append(format("Result follows:\n" + new String(outcome.getResult())));
                        }else{
                            sb.append(format("Result is not available, please check the errors.\n"));
                        }

                        if(outcome.hasErrors()){
                            List<IConversionIssue> issues = outcome.getIssues();
                            for (int i=0; i<issues.size(); i++) {
                                IConversionIssue iConversionIssue = issues.get(i);
                                sb.append( format( "%d)\n\tmessage: %s\n\ttype:    %s\n",
                                        i+1,
                                        iConversionIssue.getMessage(),
                                        iConversionIssue.isError() ? "ERROR" : "warn" ) );
                            }
                        }

                    } catch (Exception e) {
                        log.error("An error occurred.", e);
                    } finally {
                        elapsed = System.currentTimeMillis() - elapsed;
                    }

                    log.info(sb.toString());
                    log.info("Conversion of {} completed in {}ms", ublInvoice, elapsed);
                }
            });



        }

        executor.awaitTermination(120, TimeUnit.SECONDS);



    }

}
