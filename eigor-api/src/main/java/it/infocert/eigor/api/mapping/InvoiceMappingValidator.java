package it.infocert.eigor.api.mapping;


import com.google.common.collect.Multimap;

/**
 * Interface for input invoice map validators.
 */
public interface InvoiceMappingValidator {

    /**
     * Validates a mappings map.
     *
     * @param map the mappings map
     * @throws RuntimeException if rules are not met
     */
    void validate(Multimap<String, String> map);

}
