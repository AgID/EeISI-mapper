package it.infocert.eigor.api.mapping;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import it.infocert.eigor.api.mapping.toCen.InputInvoiceMapValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * This class stores the invoice path mappings for {@link it.infocert.eigor.api.mapping.GenericOneToOneTransformer}
 */
public class InputInvoiceXpathMap {
    private static final Logger log = LoggerFactory.getLogger(InputInvoiceXpathMap.class);

    private Multimap<String, String> mapping = HashMultimap.create();
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

            try {
                mapping = loadMapFromFile(path);
            } catch (RuntimeException e) {
                InputStream resourceAsStream = this.getClass().getResourceAsStream(path);
                if (resourceAsStream == null) {
                    throw new RuntimeException("Error on loading mappings file from resource: " + path);
                }

                mapping = loadMapFromInputStream(resourceAsStream);
            }

        }
        return mapping;
    }

    private Multimap<String, String> loadMapFromFile(String path) {
        File file = new File(path);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error on loading mappings file from path", e);
        }
        log.debug("Reading mapping file from {}", file.getAbsolutePath());


        return loadMapFromInputStream(fileInputStream);
    }

    private Multimap<String, String> loadMapFromInputStream(InputStream inputStream) {
        Multimap<String, String> mappings = HashMultimap.create();
        Properties properties = new Properties();
        try {
            try {
                properties.load(inputStream);
                for (String key : properties.stringPropertyNames()) {
                    mappings.put(key, properties.getProperty(key));
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error on loading mappings file", e);
        }
        validator.validate(mappings);
        return mappings;
    }

}