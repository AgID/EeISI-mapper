package it.infocert.eigor.api.configuration;

import it.infocert.eigor.api.Named;
import org.springframework.core.io.Resource;

public interface EigorConfiguration {

    Resource pathForModuleResource(Named named, String path);
}
