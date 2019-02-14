package it.infocert.eigor.cli;

import com.google.common.io.Resources;
import it.infocert.eigor.api.FromCenConversionRepository;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.ToCenConversionRepository;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.impl.FromCenListBakedRepository;
import it.infocert.eigor.api.impl.ToCenListBakedRepository;
import it.infocert.eigor.api.utils.EigorVersion;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.converter.cen2cii.Cen2Cii;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPA;
import it.infocert.eigor.converter.cen2peoppl.Cen2PEPPOLBSI;
import it.infocert.eigor.converter.cen2peppolcn.Cen2PeppolCn;
import it.infocert.eigor.converter.cen2ubl.Cen2Ubl;
import it.infocert.eigor.converter.cen2ublcn.Cen2UblCn;
import it.infocert.eigor.converter.cen2xmlcen.CenToXmlCenConverter;
import it.infocert.eigor.converter.cii2cen.Cii2Cen;
import it.infocert.eigor.converter.csvcen2cen.CsvCen2Cen;
import it.infocert.eigor.converter.fattpa2cen.FattPa2Cen;
import it.infocert.eigor.converter.ubl2cen.Ubl2Cen;
import it.infocert.eigor.converter.ublcn2cen.UblCn2Cen;
import it.infocert.eigor.converter.xmlcen2cen.XmlCen2Cen;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.rules.repositories.CardinalityRulesRepository;
import it.infocert.eigor.rules.repositories.CompositeRuleRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class Eigor {

    private static final Logger log = LoggerFactory.getLogger(Eigor.class);

    public static void main(String[] args) {
        System.out.println(EigorVersion.getAsString());
        log.info(EigorVersion.getAsString());
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Eigor.class);
        ctx.getBean(EigorCli.class).run(args);
    }

    @Bean
    EigorConfiguration configuration() {
        EigorConfiguration eigorConfiguration = new DefaultEigorConfigurationLoader().loadConfiguration();
        return eigorConfiguration;
    }

    @Bean
    IReflections reflections() {
        return new JavaReflections();
    }

    @Bean
    RuleRepository ruleRepository(IReflections reflections) {
        return new RuleRepository() {
            @Override public List<Rule> rules() {
                throw new IllegalArgumentException("Not implemented yet.");
            }
        };
    }

    @Bean
    RuleRepository compositeRepository(RuleRepository cardinalityRepository, RuleRepository integrityRepository) {
        return new CompositeRuleRepository(cardinalityRepository, integrityRepository);
    }

    @Bean
    RuleRepository cardinalityRepository() {
        Properties properties = new Properties();
        URL resource = Resources.getResource("cardinality.properties");
        try {
            properties.load(resource.openStream());
        } catch (IOException e) {
            log.error("Resource '{}' not found.", resource, e.getMessage(), e);
        }
//        return new CardinalityRulesRepository(properties); //DISABLED
        return new CardinalityRulesRepository(new Properties());
    }

    @Bean
    RuleRepository integrityRepository() {
        Properties properties = new Properties();
        URL resource = Resources.getResource("rules.properties");
        try {
            properties.load(resource.openStream());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
//        return new IntegrityRulesRepository(properties); //DISABLED
        return new IntegrityRulesRepository(new Properties());
    }

    @Bean(initMethod = "configure")
    ToCenConversionRepository toCenConversionRepository(IReflections reflections, EigorConfiguration configuration) {
        return new ToCenListBakedRepository(
                new Ubl2Cen(reflections, configuration),
                new UblCn2Cen(reflections, configuration),
                new CsvCen2Cen(reflections),
                new FattPa2Cen(reflections, configuration),
                new Cii2Cen(reflections, configuration),
                new XmlCen2Cen(reflections, configuration)
        );
    }

    @Bean(initMethod = "configure")
    FromCenConversionRepository fromCenConversionRepository(IReflections reflections, EigorConfiguration configuration) {
        return new FromCenListBakedRepository(
                new Cen2FattPA(reflections, configuration),
                new Cen2Ubl(reflections, configuration),
                new Cen2UblCn(reflections, configuration),
                new Cen2Cii(reflections, configuration),
                new CenToXmlCenConverter(configuration),
                new Cen2PEPPOLBSI(reflections, configuration),
                new Cen2PeppolCn(reflections, configuration)
        );
    }

    @Bean
    EigorCli app(CommandLineInterpreter interpreter) {
        return new EigorCli(interpreter);
    }

    @Bean
    CommandLineInterpreter commandLineInterpreter(ToCenConversionRepository toCenConversionRepository, FromCenConversionRepository fromCenConversionRepository, RuleRepository compositeRepository, EigorConfiguration configuration) {
        return new JoptsimpleBasecCommandLineInterpreter(toCenConversionRepository, fromCenConversionRepository, compositeRepository, configuration);
    }

}
