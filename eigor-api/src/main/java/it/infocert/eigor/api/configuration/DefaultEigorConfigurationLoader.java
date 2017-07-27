package it.infocert.eigor.api.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static java.lang.String.format;

public class DefaultEigorConfigurationLoader {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Load the Eigor configuration from a well-well knows list of location:
     * <ul>
     *     <li>If the {@code eigor.configurationFile} system property is set, it tries to load the configuration from a file which path is the value of that system variable.</li>
     *     <li>If a confiugration is not available, tries to load it from the classpath resource {@code /eigor-test.properties}.</li>
     *     <li>If a confiugration is not available, tries to load it from the classpath resource {@code /eigor.properties}.</li>
     * </ul>
     * This is highly inspired by how logback loads its cofiguration.
     */
    public EigorConfiguration loadConfiguration() {

        ArrayList<String> tentatives = new ArrayList();

        EigorConfiguration eigorConfiguration = null;

        // if the system property is defined, try to load from it.
        String location = System.getProperty("eigor.configurationFile");
        if(location!=null){
            tentatives.add(location);
            Resource resource = new DefaultResourceLoader().getResource(location);
            if(resource.exists()){
                try {
                    eigorConfiguration = fromInputstream(resource.getInputStream());
                    log.debug("Successfully loaded Eigor configuration from '{}'", location);
                } catch (IOException ioe) {
                    log.debug("Skipping loading Eigor configuration from '{}' because of: {}", location, ioe.getMessage());
                }
            }else{
                log.debug("Skipping loading Eigor configuration from '{}' because it does not exist.", location);
            }
        }

        // then try to load 'eigor-test.properties' from classpath
        if(eigorConfiguration==null) {
            String resourcePath = "/eigor-test.properties";
            try {
                tentatives.add(resourcePath);
                eigorConfiguration = fromClasspath(resourcePath);
                if (eigorConfiguration == null) {
                    log.debug("Skipping loading Eigor configuration from classpath resource '{}' that does not exist.", resourcePath);
                } else {
                    log.debug("Successfully loaded Eigor configuration from classpath resource '{}'", resourcePath);
                    return eigorConfiguration;
                }
            } catch (IOException ioe) {
                log.debug("Skipping loading Eigor configuration from classpath resource '{}' because of: {}", resourcePath, ioe.getMessage());
            }
        }

        // try to load 'eigor.properties' from classpath
        if(eigorConfiguration==null) {
            String resourcePath = "/eigor.properties";
            try {
                tentatives.add(resourcePath);
                eigorConfiguration = fromClasspath(resourcePath);
                if (eigorConfiguration == null) {
                    log.debug("Skipping loading Eigor configuration from classpath resource '{}' that does not exist.", resourcePath);
                } else {
                    log.debug("Successfully loaded Eigor configuration from classpath resource '{}'", resourcePath);
                }
            } catch (IOException ioe) {
                log.debug("Skipping loading Eigor configuration from classpath resource '{}' because of: {}", resourcePath, ioe.getMessage());
            }
        }

        if(eigorConfiguration == null){
            throw new RuntimeException( format("Unable to find an eigor configuration file in any of those locations: %s.", tentatives) );
        }

        return eigorConfiguration;

    }

    /** Shorthand method to get the Eigor configuration. */
    public static EigorConfiguration configuration() {
        return new DefaultEigorConfigurationLoader().loadConfiguration();
    }

    private EigorConfiguration fromClasspath(String resourcePath) throws IOException {
        InputStream conf = getClass().getResourceAsStream(resourcePath);
        if(conf == null) return null;
        return fromInputstream(conf);
    }

    private EigorConfiguration fromInputstream(InputStream conf) throws IOException {
        Properties properties = new Properties();
        properties.load(conf);
        return new PropertiesBackedConfiguration(properties);
    }

}
