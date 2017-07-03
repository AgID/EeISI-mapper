package it.infocert.eigor.api.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DefaultEigorConfigurationLoader {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public EigorConfiguration loadConfiguration() {

        EigorConfiguration eigorConfiguration = null;

        // if the system property is defined, try to load from it.
        String location = System.getProperty("eigor.configurationFile");
        if(location!=null){
            Resource resource = new DefaultResourceLoader().getResource(location);
            if(resource.exists()){
                try {
                    eigorConfiguration = fromInputstream(resource.getInputStream());
                } catch (IOException ioe) {
                    log.debug("Skipping loading Eigor configuration from '{}' because of: {}", location, ioe.getMessage());
                }
            }else{
                log.debug("Skipping loading Eigor configuration from '{}' because it does not exist.", location);
            }
        }

        // then try to load 'eigor-test.properties' from classpath
        String resourcePath = "/eigor-test.properties";
        try {
            eigorConfiguration = fromClasspath(resourcePath);
            if(eigorConfiguration==null) {
                log.debug("Skipping loading Eigor configuration from classpath resource '{}' that does not exist.",
                        resourcePath);
            }else{
                return eigorConfiguration;
            }
        } catch (IOException ioe) {
            log.debug("Skipping loading Eigor configuration from classpath resource '{}' because of: {}", resourcePath, ioe.getMessage());
        }

        // try to load 'eigor.properties' from classpath
        resourcePath = "/eigor.properties";
        try {
            eigorConfiguration = fromClasspath(resourcePath);
            if(eigorConfiguration==null) {
                log.debug("Skipping loading Eigor configuration from classpath resource '{}' that does not exist.",
                        resourcePath);
            }
        } catch (IOException ioe) {
            log.debug("Skipping loading Eigor configuration from classpath resource '{}' because of: {}", resourcePath, ioe.getMessage());
        }

        return eigorConfiguration;


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
