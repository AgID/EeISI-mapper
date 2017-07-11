package it.infocert.eigor.api.configuration;

/**
 * Thrown by {@link Configurable configurable objects} when the configuration fails for whatever reason.
 */
public class ConfigurationException extends Exception {

    public ConfigurationException() {
    }

    public ConfigurationException(String s) {
        super(s);
    }

    public ConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }

}
