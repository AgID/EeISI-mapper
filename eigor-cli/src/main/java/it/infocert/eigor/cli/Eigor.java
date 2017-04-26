package it.infocert.eigor.cli;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.impl.ReflectionBasedRepository;
import it.infocert.eigor.rules.repositories.ConstraintsRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class Eigor {

    public static Logger log = LoggerFactory.getLogger(Eigor.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Eigor.class);
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
    RuleRepository constraintsRepository(Reflections reflections) {
        return new ConstraintsRepository(reflections);
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
    CommandLineInterpreter commandLineInterpreter(ToCenConversionRepository toCenConversionRepository, FromCenConversionRepository fromCenConversionRepository, RuleRepository constraintsRepository) {
        return new JoptsimpleBasecCommandLineInterpreter(toCenConversionRepository, fromCenConversionRepository, constraintsRepository);
    }

}
