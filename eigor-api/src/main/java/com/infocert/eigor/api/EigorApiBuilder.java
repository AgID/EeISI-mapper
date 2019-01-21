package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionRepository;
import it.infocert.eigor.api.DefaultRuleRepository;
import it.infocert.eigor.api.Named;
import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.io.Copier;
import it.infocert.eigor.api.utils.EigorVersion;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.JavaReflections;
import it.infocert.eigor.converter.cen2cii.Cen2Cii;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPA;
import it.infocert.eigor.converter.cen2peoppl.Cen2PEPPOLBSI;
import it.infocert.eigor.converter.cen2ubl.Cen2Ubl;
import it.infocert.eigor.converter.cen2ublcn.Cen2UblCn;
import it.infocert.eigor.converter.cen2xmlcen.CenToXmlCenConverter;
import it.infocert.eigor.converter.cii2cen.Cii2Cen;
import it.infocert.eigor.converter.csvcen2cen.CsvCen2Cen;
import it.infocert.eigor.converter.fattpa2cen.FattPa2Cen;
import it.infocert.eigor.converter.ubl2cen.Ubl2Cen;
import it.infocert.eigor.converter.ublcn2cen.UblCn2Cen;
import it.infocert.eigor.converter.xmlcen2cen.XmlCen2Cen;
import it.infocert.eigor.rules.repositories.CardinalityRulesRepository;
import it.infocert.eigor.rules.repositories.CompositeRuleRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EigorApiBuilder {

    private final static Logger log = LoggerFactory.getLogger(EigorApiBuilder.class);

    private final EigorConfiguration configuration;
    private final ConversionRepository conversionRepository;
    private File outputFolderFile;
    private RuleRepository ruleRepository;
    private boolean copy = false;
    private boolean forceConversion = false;

    public EigorApiBuilder() throws IOException {

        // needed support classes
        IReflections reflections = new JavaReflections();

        // load the eigor configuration
        configuration = DefaultEigorConfigurationLoader.configuration();

        // prepare the set of conversions to be supported by the api
        conversionRepository =
                new ConversionRepository.Builder()
                        .register(new Cii2Cen(reflections, configuration))
                        .register(new Cen2Cii(reflections, configuration))
                        .register(new Ubl2Cen(reflections, configuration))
                        .register(new UblCn2Cen(reflections, configuration))
                        .register(new Cen2FattPA(reflections, configuration))
                        .register(new Cen2Ubl(reflections, configuration))
                        .register(new Cen2PEPPOLBSI(reflections, configuration))
                        .register(new Cen2UblCn(reflections, configuration))
                        .register(new FattPa2Cen(reflections, configuration))
                        .register(new CsvCen2Cen(reflections))
                        .register(new XmlCen2Cen(reflections, configuration))
                        .register(new CenToXmlCenConverter())
                        .build();

        outputFolderFile = FileUtils.getTempDirectory();


//        try {
            Properties cardinalityRules = new Properties();
//            cardinalityRules.load(checkNotNull(getClass().getResourceAsStream("/cardinality.properties")));
            Properties cardinalityRules2 = new Properties();
//            cardinalityRules2.load(checkNotNull(getClass().getResourceAsStream("/rules.properties")));
            ruleRepository = new CompositeRuleRepository(
                    new CardinalityRulesRepository(cardinalityRules),
                    new IntegrityRulesRepository(cardinalityRules2)
            );
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }


    public EigorApi build() throws ConfigurationException {
        log.info(EigorVersion.getAsDetailedString());

        // set up the rule repository
        RuleRepository ruleRepository = DefaultRuleRepository.newInstance();

        // "clone" the resources needed for each converter in a local file system
        File dest = new File(configuration.getMandatoryString("eigor.workdir") + "/converterdata");
        if (!dest.exists()) {
            dest.mkdirs();
        }
        List<Named> converters = new ArrayList<>();
        converters.addAll(conversionRepository.getFromCenConverters());
        converters.addAll(conversionRepository.getToCenConverters());

        //workaround for converter-commons
        converters.add(new Named() {
            @Override
            public String getName() {
                return "converter-commons";
            }
        });

        for (Named converter : converters) {
            String pathSegment = converter.getName();
            if (copy) {
                File dest1 = new File(dest, pathSegment);
                new Copier(dest1)
                        .withCallback(new Copier.Callback() {
                            @Override
                            public void afterFileCopied(File file) throws IOException {
                                if (file.isFile() && file.getName().endsWith(".xslt")) {
                                    FileUtils.touch(file);
                                }
                            }
                        })
                        .copyFrom("/converterdata/" + pathSegment);
            }
        }

        // configure the repo
        conversionRepository.configure();

        return new EigorApi(this);

    }

    File getOutputFolderFile() {
        return outputFolderFile;
    }

    ConversionRepository getConversionRepository() {
        return conversionRepository;
    }

    RuleRepository getRuleRepository() {
        return ruleRepository;
    }

    public EigorApiBuilder withOutputFolder(File tempDirectory) {
        this.outputFolderFile = tempDirectory;
        return this;
    }

    public EigorApiBuilder enableAutoCopy() {
        this.copy = true;
        return this;
    }

    public EigorApiBuilder enableForce() {
        this.forceConversion = true;
        return this;
    }

    public boolean isForceConversion() {
        return forceConversion;
    }
}
