package it.infocert.eigor.api.configuration;

/**
 * The Eigor configuration.
 * The main way to load such configuration is to use {@link DefaultEigorConfigurationLoader the default loader}.
 */
public interface EigorConfiguration {

    String getMandatoryString(String property);
}
