package it.infocert.eigor.api.mapping.toCen;


import com.google.common.collect.Multimap;

/**
 * Interface for input invoice map validators.
 */
public interface InputInvoiceMapValidator {

    /**
     * Validates a mappings map.
     *
     * @param map the mappings map
     * @throws RuntimeException if rules are not met
     */
    void validate(Multimap<String, String> map);

}
