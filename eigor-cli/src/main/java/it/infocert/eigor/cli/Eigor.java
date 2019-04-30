package it.infocert.eigor.cli;

import it.infocert.eigor.api.FromCenConversionRepository;
import it.infocert.eigor.api.ToCenConversionRepository;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.impl.FromCenListBakedRepository;
import it.infocert.eigor.api.impl.ToCenListBakedRepository;
import it.infocert.eigor.api.utils.EigorVersion;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.converter.cen2cii.Cen2Cii;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPA;
import it.infocert.eigor.converter.cen2peoppl.Cen2PeppolBis;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

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
        return new DefaultEigorConfigurationLoader().loadConfiguration();
    }

    @Bean
    IReflections reflections() {
        return new JavaReflections();
    }

    @Bean
    ToCenConversionRepository toCenConversionRepository(IReflections reflections, EigorConfiguration configuration) throws ConfigurationException {
        ToCenListBakedRepository toCenListBakedRepository = new ToCenListBakedRepository(
                new Ubl2Cen(reflections, configuration),
                new UblCn2Cen(reflections, configuration),
                new CsvCen2Cen(reflections),
                new FattPa2Cen(reflections, configuration),
                new Cii2Cen(reflections, configuration),
                new XmlCen2Cen(reflections, configuration)
        );
        toCenListBakedRepository.configure();
        return toCenListBakedRepository;
    }

    @Bean
    FromCenConversionRepository fromCenConversionRepository(IReflections reflections, EigorConfiguration configuration) throws ConfigurationException {
        FromCenListBakedRepository fromCenListBakedRepository = new FromCenListBakedRepository(
                new Cen2FattPA(reflections, configuration),
                new Cen2Ubl(reflections, configuration),
                new Cen2UblCn(reflections, configuration),
                new Cen2Cii(reflections, configuration),
                new CenToXmlCenConverter(configuration),
                new Cen2PeppolBis(reflections, configuration),
                new Cen2PeppolCn(reflections, configuration)
        );
        fromCenListBakedRepository.configure();
        return fromCenListBakedRepository;
    }

    @Bean
    EigorCli app(CommandLineInterpreter interpreter) {
        return new EigorCli(interpreter);
    }

    @Bean
    CommandLineInterpreter commandLineInterpreter() {
        return new JoptsimpleBasecCommandLineInterpreter();
    }
}
