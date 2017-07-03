package it.infocert.eigor.api.configuration;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.Named;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;

class PropertiesBackedConfiguration implements EigorConfiguration {

    private final Properties properties;
    private final DefaultResourceLoader drl;

    public PropertiesBackedConfiguration(final Properties properties) {
        Preconditions.checkNotNull(properties);
        this.properties = properties;
        this.drl = new DefaultResourceLoader(PropertiesBackedConfiguration.class.getClassLoader());
    }

    @Override
    public Resource pathForModuleResource(Named named, String path) {
        String basePath = getMandatoryProperty("eigor.converters.basePath");
        String location = joinTokens(basePath, named.getName(), path);
        return drl.getResource(location);
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
