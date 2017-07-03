package it.infocert.eigor.api.configuration;

import com.google.common.base.Preconditions;

import java.util.Properties;

class PropertiesBackedConfiguration implements EigorConfiguration {

    public PropertiesBackedConfiguration(final Properties properties) {
        Preconditions.checkNotNull(properties);
    }

}
