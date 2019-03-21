package it.infocert.eigor.api.configuration;

import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static java.lang.String.format;

public class DefaultEigorConfigurationLoader {

    /**
     * Name of the system property that should refer to the external configuration file to be loaded.
     * Please note that the value of this property follows the semantic supported by
     * <a href="https://docs.spring.io/spring/docs/3.0.0.M4/reference/html/ch04s04.html">Spring's Resource Loader</a>.
     */
    public static final String EIGOR_CONFIGURATION_FILE_SYSTEM_PROPERTY = "eigor.configurationFile";
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Properties DEFAULTS;
    private static EigorConfiguration CONFIG = null;

    static {
        DEFAULTS = new Properties();
        try {
            DEFAULTS.load(  DefaultEigorConfigurationLoader.class.getResourceAsStream( DefaultEigorConfigurationLoader.class.getSimpleName() + ".properties" ) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shorthand method to get the Eigor configuration.
     */
    public static EigorConfiguration configuration() {
        if(CONFIG == null) {
            CONFIG = new DefaultEigorConfigurationLoader().loadConfiguration();
        }
        return CONFIG;
    }

    /**
     * Load the Eigor configuration from a well-well knows list of location:
     * <ul>
     * <li>If the {@code eigor.configurationFile} system property is set, it tries to load the configuration from a file which path is the value of that system variable.</li>
     * <li>If a confiugration is not available, tries to load it from the classpath resource {@code /eigor-test.properties}.</li>
     * <li>If a confiugration is not available, tries to load it from the classpath resource {@code /eigor.properties}.</li>
     * </ul>
     * This is highly inspired by how logback loads its cofiguration.
     */
    public EigorConfiguration loadConfiguration() {

        ArrayList<String> tentatives = new ArrayList<>();

        EigorConfiguration eigorConfiguration = null;

        // if the system property is defined, try to load from it.
        String location = System.getProperty(EIGOR_CONFIGURATION_FILE_SYSTEM_PROPERTY);
        if (location != null) {
            tentatives.add(location);
            Resource resource = new DefaultResourceLoader().getResource(location);
            if (resource.exists()) {
                try {
                    eigorConfiguration = fromInputstream(resource.getInputStream());
                    log.debug("Successfully loaded Eigor configuration from '{}'", location);
                } catch (IOException ioe) {
                    log.debug("Skipping loading Eigor configuration from '{}' because of: {}", location, ioe.getMessage());
                }
            } else {
                log.debug("Skipping loading Eigor configuration from '{}' because it does not exist.", location);
            }
        }

        // then try to load 'eigor-test.properties' from classpath
        if (eigorConfiguration == null) {
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
        if (eigorConfiguration == null) {
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

        if (eigorConfiguration == null) {
            throw new RuntimeException(format("Unable to find an eigor configuration file in any of those locations: %s.", tentatives));
        }

        return eigorConfiguration;

    }

    private EigorConfiguration fromClasspath(String resourcePath) throws IOException {
        InputStream conf = getClass().getResourceAsStream(resourcePath);
        if (conf == null) return null;
        return fromInputstream(conf);
    }

    private EigorConfiguration fromInputstream(InputStream conf) throws IOException {
        Properties properties = new Properties(DEFAULTS);
        properties.load(conf);
        return new PropertiesBackedConfiguration(properties);
    }

}
