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

import static java.lang.String.format;

public class HiLevelAPIUsage {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    Logger log = LoggerFactory.getLogger(this.getClass());

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
        RuleRepository ruleRepository = DefaultRuleRepository.newInstance();

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

}
