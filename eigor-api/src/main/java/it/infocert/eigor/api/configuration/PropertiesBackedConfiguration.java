package it.infocert.eigor.api.configuration;

import com.google.common.base.Preconditions;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Properties;

public class PropertiesBackedConfiguration implements EigorConfiguration {

    private final Properties properties;
    private final DefaultResourceLoader drl;

    public PropertiesBackedConfiguration() {
        this.properties = new Properties();
        this.drl = new DefaultResourceLoader(PropertiesBackedConfiguration.class.getClassLoader());
    }

    public PropertiesBackedConfiguration(final Properties properties) {
        Preconditions.checkNotNull(properties);
        this.properties = new Properties( properties );
        this.drl = new DefaultResourceLoader(PropertiesBackedConfiguration.class.getClassLoader());
    }

    public Object setProperty(String property, String value) {
        return properties.setProperty(property, value);
    }

    public PropertiesBackedConfiguration addProperty(String property, String value) {
        properties.setProperty(property, value);
        return this;
    }

    @Override public String getMandatoryString(String property) {
        String theProperty = properties.getProperty(property);
        if(theProperty == null) throw MissingMandatoryPropertyException.missingProperty(property);
        return theProperty;
    }

    private String getMandatoryProperty(String propertyName) {
        String property = properties.getProperty(propertyName);
        if(property == null){
            throw new IllegalStateException("Missing mandatory property '" + propertyName + "'");
        }
        return property;
    }
}
