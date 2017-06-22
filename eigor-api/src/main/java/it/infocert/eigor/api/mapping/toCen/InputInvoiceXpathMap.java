package it.infocert.eigor.api.mapping.toCen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * This class stores the invoice path mappings for GenericOneToOneTransformation
 */
public class InputInvoiceXpathMap {

    private Multimap<String, String> mapping;
    private InputInvoiceMapValidator validator;

    public InputInvoiceXpathMap(InputInvoiceMapValidator validator) {
        this.validator = validator;
        mapping = HashMultimap.create();
    }

    /**
     * Gets the mappings for GenericOneToOneTransformations.
     *
     * @return the mapping map
     */
    public Multimap<String, String> getMapping(String path) {
        if (mapping.isEmpty()) {
            mapping = loadMapFromFile(path);
        }
        return mapping;
    }

    private Multimap<String, String> loadMapFromFile(String path) {
        Multimap<String, String> mappings = HashMultimap.create();
        Properties properties = new Properties();
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
            for (String key : properties.stringPropertyNames()) {
                mappings.put(key, properties.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error on loading mappings file", e);
        }
        if (validator != null) {
            validator.validate(mappings);
        }
        return mappings;
    }
}