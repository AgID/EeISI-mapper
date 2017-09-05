package it.infocert.eigor.api.mapping;


import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;

/**
 * Interface for input invoice map validators.
 */
public interface InvoiceMappingValidator {

    /**
     * Validates a mappings map.
     *
     * @param map the mappings map
     * @throws SyntaxErrorInMappingFileException if rules are not met
     */
    void validate(Multimap<String, String> map) throws SyntaxErrorInMappingFileException;

}
