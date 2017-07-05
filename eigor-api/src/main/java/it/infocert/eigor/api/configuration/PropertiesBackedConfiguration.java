package it.infocert.eigor.api.configuration;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.Named;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

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

    @Override
    public Resource pathForModuleResource(Named named, String path) {
        String basePath = getMandatoryProperty("eigor.converters.basePath");
        String location = joinTokens(basePath, named.getName(), path);
        return drl.getResource(location);
    }

    @Override public String getMandatoryString(String property) {
        String theProperty = properties.getProperty(property);
        if(theProperty == null) throw MissingMandatoryPropertyException.missingProperty(property);
        return theProperty;
    }

    private String joinTokens(String... tokens) {

        for (int i = 0; i < tokens.length; i++) {

            if(i<tokens.length-1){
                if(tokens[i].endsWith("/")){
                    tokens[i] = tokens[i].substring(0, tokens[i].length()-1);
                }
            }

            if(i>=1){
                if(tokens[i].startsWith("/")){
                    tokens[i]=tokens[i].substring(1);
                }
            }

        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            if(sb.length()>=1){
                sb.append("/");
            }
            sb.append(tokens[i]);
        }
        return sb.toString();


    }

    private String getMandatoryProperty(String propertyName) {
        String property = properties.getProperty(propertyName);
        if(property == null){
            throw new IllegalStateException("Missing mandatory property '" + propertyName + "'");
        }
        return property;
    }
}
