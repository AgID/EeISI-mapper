package it.infocert.eigor.api.mapping;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.infocert.eigor.api.SyntaxErrorInMappingFileException;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * This class stores the invoice path mappings
 * for {@link it.infocert.eigor.api.mapping.GenericOneToOneTransformer}
 */
public class InputInvoiceXpathMap {
    private static final Logger log = LoggerFactory.getLogger(InputInvoiceXpathMap.class);

    private Multimap<String, String> mapping;
    private final InvoiceMappingValidator validator;

    /**
     * Creates an {@link InputInvoiceXpathMap} that validates the loaded file.
     */
    public InputInvoiceXpathMap(InvoiceMappingValidator validator) {
        this.validator = validator;
        mapping = null;
    }

    /**
     * Creates an {@link InputInvoiceXpathMap} that does not validates the loaded file.
     */
    public InputInvoiceXpathMap() {
        this.validator = null;
        mapping = null;
    }

    /**
     * Gets the mappings for GenericOneToOneTransformations.
     *
     * @return the mapping map
     */
    public Multimap<String, String> getMapping(Resource r) {

        try {
            InputStream inputStream = r.getInputStream();
            return getMapping(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load mapping file from resource: '" + r + "'.");
        }

    }

    public Multimap<String, String> getMapping(InputStream inputStream) {
        try {
            mapping = loadMapFromInputStream(inputStream);
        } catch (SyntaxErrorInMappingFileException e) {
            log.error("Validation error '{}' for inputStream ", e.getMessage(), e);
        }
        return mapping;
    }

    /**
     * Gets the mappings for GenericOneToOneTransformations.
     *
     * @return the mapping map
     * @deprecated Use {@link InputInvoiceXpathMap#getMapping(Resource)} instead.
     */
    @Deprecated
    public Multimap<String, String> getMapping(String path) {

        if (mapping == null) {
            // try #1, from filesystem
            try {
                File file = new File(path);
                FileInputStream fileInputStream;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Error on loading mappings file from path '" + path + "' (resolved to '" + file.getAbsolutePath() + "') because of: " + e.getMessage(), e);
                }
                log.debug("Reading mapping file from {}", file.getAbsolutePath());

                mapping = loadMapFromInputStream(fileInputStream);
            } catch (RuntimeException e) {
                log.warn("Unable to load mapping from file '{}'.", path);
                log.debug(e.getClass().getSimpleName(), e);
            } catch (SyntaxErrorInMappingFileException e) {
                log.error("Validation error '{}' for resource '{}'", e.getMessage(), path, e);
            }
        }

        if (mapping == null) {
            // try #2, from classpath with path not changed
            try {
                InputStream resourceAsStream = this.getClass().getResourceAsStream(path);
                if (resourceAsStream == null) {
                    throw new RuntimeException("Error on loading mappings file from resource: " + path);
                }
                mapping = loadMapFromInputStream(resourceAsStream);
            } catch (RuntimeException e) {
                log.warn("Unable to load mapping from resource '{}'.", path);
                log.debug(e.getClass().getSimpleName(), e);
            } catch (SyntaxErrorInMappingFileException e) {
                log.error("Validation error '{}' for resource '{}'", e.getMessage(), path, e);
            }
        }

        if (mapping == null) {
            // try #3, from classpath with path with "/" prepended
            try {
                InputStream resourceAsStream = this.getClass().getResourceAsStream("/" + path);
                if (resourceAsStream == null) {
                    throw new RuntimeException("Error on loading mappings file from resource: " + path);
                }
                mapping = loadMapFromInputStream(resourceAsStream);
            } catch (RuntimeException e) {
                log.warn("Unable to load mapping from resource '{}'.", path);
                log.debug(e.getClass().getSimpleName(), e);
            } catch (SyntaxErrorInMappingFileException e) {
                log.error("Validation error '{}' for resource '{}'", e.getMessage(), path, e);
            }
        }

        if (mapping == null) {
            throw new RuntimeException("Unable to load mapping file from resource: '" + path + "'.");
        }

        return mapping;
    }

    private Multimap<String, String> loadMapFromInputStream(InputStream inputStream) throws SyntaxErrorInMappingFileException {
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
        if (validator != null) {
            validator.validate(mappings);
        }
        return mappings;
    }


}