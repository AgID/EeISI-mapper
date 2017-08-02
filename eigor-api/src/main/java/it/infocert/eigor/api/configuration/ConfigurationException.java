package it.infocert.eigor.api.configuration;

import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.EigorException;

/**
 * Thrown by {@link Configurable configurable objects} when the configuration fails for whatever reason.
 */
public class ConfigurationException extends EigorException {

    public ConfigurationException(String message) {
        super(new ErrorMessage(message));
    }

    public ConfigurationException(String message, Throwable cause) {
        super(new ErrorMessage(message), cause);
    }

    public ConfigurationException(Throwable cause) {
        super(new ErrorMessage(cause.getMessage()), cause);
    }
}
