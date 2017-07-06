package it.infocert.eigor.cli;

import com.google.common.io.Resources;
import it.infocert.eigor.api.ApplicationContextProvider;
import it.infocert.eigor.api.FromCenConversionRepository;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.ToCenConversionRepository;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.impl.FromCenListBakedRepository;
import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.api.impl.ToCenListBakedRepository;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPA;
import it.infocert.eigor.converter.cii2cen.Cii2Cen;
import it.infocert.eigor.converter.csvcen2cen.CsvCen2Cen;
import it.infocert.eigor.converter.fattpa2cen.FattPA2CenConverter;
import it.infocert.eigor.converter.ubl2cen.Ubl2Cen;
import it.infocert.eigor.rules.repositories.CardinalityRulesRepository;
import it.infocert.eigor.rules.repositories.CompositeRuleRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Eigor {

    public static Logger log = LoggerFactory.getLogger(Eigor.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Eigor.class);
        ApplicationContextProvider.setApplicationContext(ctx);
        ctx.getBean(EigorCli.class).run(args);
    }

    @Bean
    EigorConfiguration configuration() {
        EigorConfiguration eigorConfiguration = new DefaultEigorConfigurationLoader().loadConfiguration();
        return eigorConfiguration;
    }

    @Bean
    Object reflections() {
        return new Reflections("it.infocert");
    }

    @Bean
    RuleRepository ruleRepository(Reflections reflections) {
        return new ReflectionBasedRepository(reflections);
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
        return new CardinalityRulesRepository(properties);
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
        return new IntegrityRulesRepository(properties);
    }

    @Bean(initMethod = "configure")
    ToCenConversionRepository toCenConversionRepository(Reflections reflections, EigorConfiguration configuration) {
        return new ToCenListBakedRepository(
                new Ubl2Cen(reflections, configuration),
                new FattPA2CenConverter(reflections, configuration),
                new CsvCen2Cen(reflections),
                new Cii2Cen(reflections, configuration)
        );
    }

    @Bean(initMethod = "configure")
    FromCenConversionRepository fromCenConversionRepository(Reflections reflections, EigorConfiguration configuration) {
        return new FromCenListBakedRepository(
                new Cen2FattPA(reflections, configuration)
        );
    }

    @Bean
    EigorCli app(CommandLineInterpreter interpreter) {
        return new EigorCli(interpreter);
    }

    @Bean
    CommandLineInterpreter commandLineInterpreter(ToCenConversionRepository toCenConversionRepository, FromCenConversionRepository fromCenConversionRepository, RuleRepository compositeRepository) {
        return new JoptsimpleBasecCommandLineInterpreter(toCenConversionRepository, fromCenConversionRepository, compositeRepository);
    }

}
