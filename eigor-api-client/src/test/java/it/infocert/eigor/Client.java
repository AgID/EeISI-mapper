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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.reflections.Reflections;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class Client {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    File outputFolderFile;

    @Before
    public void setUp() throws IOException {
        outputFolderFile = tmp.newFolder();
        if(!outputFolderFile.exists()) outputFolderFile.mkdirs();
    }

    @Test public void setUpAndUseTheAPI() throws IOException, ConfigurationException {

        EigorConfiguration configuration = new DefaultEigorConfigurationLoader().loadConfiguration();
        Reflections reflections = new Reflections("it.infocert");

        File dest = new File(configuration.getMandatoryString("eigor.converterdata.path"));
        if(!dest.exists()) dest.mkdirs();

        {
            String pathSegment = "converter-cen-fattpa";
            Copier copier = new Copier( new File(dest, pathSegment) );
            copier.copyFrom("/converterdata/" + pathSegment);
        }
        {
            String pathSegment = "converter-cii-cen";
            Copier copier = new Copier( new File(dest, pathSegment) );
            copier.copyFrom("/converterdata/" + pathSegment);
        }
        {
            String pathSegment = "converter-ubl-cen";
            Copier copier = new Copier( new File(dest, pathSegment) );
            copier.copyFrom("/converterdata/" + pathSegment);
        }


        ConversionRepository conversionRepository =
                new ConversionRepository.Builder()
                        .register(new Cii2Cen(reflections, configuration))
                        .register(new Ubl2Cen(reflections, configuration))
                        .register(new Cen2FattPA(reflections, configuration))
                        .build();
        conversionRepository.configure();

        ToCenConversion toCen = conversionRepository.findConversionToCen("ubl");
        FromCenConversion fromCen = conversionRepository.findConversionFromCen("fatturapa");

        Properties cardinalityRules = new Properties();
        cardinalityRules.load(checkNotNull( getClass().getResourceAsStream("/cardinality.properties") ));

        Properties cardinalityRules2 = new Properties();
        cardinalityRules2.load(checkNotNull( getClass().getResourceAsStream("/rules.properties") ));

        RuleRepository ruleRepository = new CompositeRuleRepository(
                new CardinalityRulesRepository(cardinalityRules),
                new IntegrityRulesRepository(cardinalityRules2)
        );

        ObservableConversion.ConversionCallback l1 = new DebugConversionCallback(outputFolderFile);
        InputStream invoice = new ByteArrayInputStream( "<invoice>xml</invoice>".getBytes() );
        ObservableConversion conversion = new ObservableConversion(
                ruleRepository,
                toCen,
                fromCen,
                invoice,
                false,
                "invoice",
                Arrays.asList(l1));

        BinaryConversionResult outcome = conversion.conversion();

    }

}
