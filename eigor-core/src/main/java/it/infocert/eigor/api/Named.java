package it.infocert.eigor.api;

/**
 * Implemented by objects that should be referenced by a name.
 * This is mainly related to configuration, to enable the segregation of config files based on the
 * name of the object being configured.
 */
public interface Named {
    String getName();
}
