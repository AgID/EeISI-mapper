package it.infocert.eigor.api.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Add some support methods to implement the {@link Configurable} interface.
 */
public class ConfigurableSupport implements Configurable {

    private final Configurable supported;
    private boolean configured;

    public ConfigurableSupport(Configurable configurable) {
        this.supported = checkNotNull( configurable );
        this.configured = false;
    }

    /**
     * Invoke this method in your {@link Configurable#configure()}.
     */
    @Override public void configure() throws ConfigurationException {
        if(configured){
            throw new ConfigurationException("'" + supported + "' has been already configured.");
        }
        configured = true;
    }

    public void checkConfigurationOccurred() {
        if(!configured) throw new IllegalStateException("'" + supported + "' has not yet been configured.");
    }
}
