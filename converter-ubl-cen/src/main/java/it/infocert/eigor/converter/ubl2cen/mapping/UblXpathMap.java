package it.infocert.eigor.converter.ubl2cen.mapping;

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
public class UblXpathMap {

    private Multimap<String, String> mapping = HashMultimap.create();


    /**
     * Gets the mappings for GenericOneToOneTransformations.
     *
     * @return the mapping map
     */
    public Multimap<String, String> getMapping() {
        if (mapping.isEmpty()) {
            mapping = loadMapFromFile();
        }
        return mapping;
    }

    private Multimap<String, String> loadMapFromFile() {
        Multimap<String, String> mappings = HashMultimap.create();
        Properties properties = new Properties();
        File file = new File("converterdata/converter-ubl-cen/mappings/one_to_one.properties");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            //TODO log something if file is missing or can't be opened
        }
        for (String key : properties.stringPropertyNames()) {
            mappings.put(key, properties.getProperty(key));
        }
        return mappings;
    }
}