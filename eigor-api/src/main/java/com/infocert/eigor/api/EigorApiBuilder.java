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

/**
 * Builds instances of the {@link EigorApi} that works according to the configuration specified.
 */
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
                        .register(new Cen2UblCn(reflections, configuration))
                        .register(new FattPa2Cen(reflections, configuration))
                        .register(new CsvCen2Cen(reflections))
                        .register(new XmlCen2Cen(reflections, configuration))
                        .register(new CenToXmlCenConverter())
                        .build();

        outputFolderFile = FileUtils.getTempDirectory();

        Properties cardinalityRules = new Properties();
        Properties cardinalityRules2 = new Properties();
        ruleRepository = new CompositeRuleRepository(
                new CardinalityRulesRepository(cardinalityRules),
                new IntegrityRulesRepository(cardinalityRules2)
        );

    }

    /**
     * Creates and return a new instance of the API configured to work according to the given configuration.
     * @throws ConfigurationException In case a problem arises during the creation of the API instance.
     */
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

    /**
     * Each time Eigor converts an invoice, it also output a set of files that contains worth information
     * about the conversion.
     * This is the folder where those files are stored.
     */
    public EigorApiBuilder withOutputFolder(File tempDirectory) {
        this.outputFolderFile = tempDirectory;
        return this;
    }

    /**
     * A common set up of {@link EigorApi} requires a bunch of files and directories to be
     * present in the filesystem of the host running Eigor.
     *
     * Those files are mainly XSDs and Schematron of all supported formats plus other configuration files.
     *
     * To speed up the set up, Eigor is shipped with an always up-to-date set of these files, that are usually stored
     * in Eigor packages. Enabling this flag, the builder will take care of create a copy of those files.
     */
    public EigorApiBuilder enableAutoCopy() {
        this.copy = true;
        return this;
    }

    /**
     * If invoked, the created API will work in "force mode".
     * In this case, the API won't stop an invoice conversion if an error is found, as it would normally
     * do if the "force mode" is not enabled.
     *
     * "force mode" is disabled by default.
     */
    public EigorApiBuilder enableForce() {
        this.forceConversion = true;
        return this;
    }

    boolean isForceConversion() {
        return forceConversion;
    }
}
