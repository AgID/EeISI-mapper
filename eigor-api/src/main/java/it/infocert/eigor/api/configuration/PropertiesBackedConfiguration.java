package it.infocert.eigor.api.configuration;

import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class PropertiesBackedConfiguration implements EigorConfiguration {

    private final PropertiesWithReplacement properties;
    private final DefaultResourceLoader drl;

    public PropertiesBackedConfiguration() {
        this.properties = new PropertiesWithReplacement();
        this.drl = new DefaultResourceLoader(PropertiesBackedConfiguration.class.getClassLoader());
    }

    public PropertiesBackedConfiguration(final Properties properties) {
        this.properties = new PropertiesWithReplacement( checkNotNull(properties) );
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
