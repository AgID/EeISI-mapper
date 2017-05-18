package it.infocert.eigor.cli;

import com.google.common.io.Resources;
import it.infocert.eigor.api.FromCenConversionRepository;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.ToCenConversionRepository;
import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
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
    Object reflections() {
        return new Reflections("it.infocert");
    }

    @Bean
    RuleRepository ruleRepository(Reflections reflections) {
        return new ReflectionBasedRepository(reflections);
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

    @Bean
    ToCenConversionRepository toCenConversionRepository(Reflections reflections) {
        return new ReflectionBasedRepository(reflections);
    }

    @Bean
    FromCenConversionRepository fromCenConversionRepository(Reflections reflections) {
        return new ReflectionBasedRepository(reflections);
    }

    @Bean
    EigorCli app(CommandLineInterpreter interpreter) {
        return new EigorCli(interpreter);
    }

    @Bean
    CommandLineInterpreter commandLineInterpreter(ToCenConversionRepository toCenConversionRepository, FromCenConversionRepository fromCenConversionRepository, RuleRepository integrityRepository) {
        return new JoptsimpleBasecCommandLineInterpreter(toCenConversionRepository, fromCenConversionRepository, integrityRepository);
    }

}
