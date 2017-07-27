package it.infocert.eigor.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that loads a list of {@link CustomMapping} classes defined in a configuration file
 */
public class CustomMappingLoader {
    private final static Logger log = LoggerFactory.getLogger(CustomMappingLoader.class);

    private final InputStream sourceStream;
    private ArrayList<CustomMapping<?>> customMappers;

    /**
     * Create the loader with the given {@link InputStream}, representing the configuration file
     *
     * @param inputStream
     */
    public CustomMappingLoader(InputStream inputStream) {
        sourceStream = inputStream;
    }

    /**
     * Load the classes from the configuration file. They will be loaded in the order they appear in the file.
     * Comments starts with '#'.
     * Valid classes that aren't implementations of {@link CustomMapping} will be skipped and will appear in the error logs
     *
     * @return a list of {@link CustomMapping} instances.
     * @throws ClassNotFoundException if the line is not a valid full qualified class name of any kind
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public List<CustomMapping<?>> loadCustomMapping() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        if (customMappers == null) {
            this.customMappers = new ArrayList<>(0);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(sourceStream, StandardCharsets.UTF_8));
            String current;
            while ((current = bfr.readLine()) != null) {
                current = current.trim();
                if (current.startsWith("#") || "".equals(current)) {
                    continue;
                }
                Object m = Class.forName(current).newInstance();
                if (m instanceof CustomMapping) {
                    customMappers.add((CustomMapping<?>) m);
                } else {
                    log.error("{} is not a valid class for Custom Mapping", current);
                }
            }
        }

        return customMappers;
    }

    public static <Type> List<CustomMapping<Type>> getSpecificTypeMappings(List<CustomMapping<?>> mappings) {
        ArrayList<CustomMapping<Type>> customMappings = new ArrayList<>(mappings.size());
        for (CustomMapping<?> mapping : mappings) {
            try {
                CustomMapping<Type> cast = (CustomMapping<Type>) mapping;
                customMappings.add(cast);
            } catch (ClassCastException e) {
                log.error("{} cannot be cast to CustomMapping<{}>, will be ignored", mapping.getClass().getSimpleName(), mapping.getClass().getSimpleName());
            }
        }
        return customMappings;
    }
}
