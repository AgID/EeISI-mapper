package it.infocert.eigor.api;

import com.google.common.collect.Multimap;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.mapping.GenericOneToOneTransformer;
import it.infocert.eigor.api.mapping.InputInvoiceXpathMap;
import it.infocert.eigor.api.mapping.fromCen.InvoiceXpathCenMappingValidator;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Base class with utility methods for CEN-XML conversion
 */
public abstract class AbstractFromCenConverter implements FromCenConversion {

    private static final Logger log = LoggerFactory.getLogger(AbstractFromCenConverter.class);
    private Reflections reflections;
    private ConversionRegistry conversionRegistry;
    private String regex;


    protected AbstractFromCenConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        this.reflections = reflections;
        this.conversionRegistry = conversionRegistry;
    }

    /**
     * Move values from a {@link BG0000Invoice} to an XML {@link Document} and serialize it in a byte array
     *
     * @param invoice  the {@link BG0000Invoice} from which to take the values
     * @param document the {@link Document} to populate with BT values
     * @param errors   a list of {@link ConversionIssue}, to be filled if an error occurs during the conversion
     * @return a {@link ConversionResult} of {@link BinaryConversionResult} containing both the XML byte array and the error list
     * @throws SyntaxErrorInInvoiceFormatException
     */
    protected BinaryConversionResult applyOne2OneTransformationsBasedOnMapping(BG0000Invoice invoice, Document document, List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        String pathOfMappingConfFile = getMappingPath();
        Multimap<String, String> mappings = new InputInvoiceXpathMap(new InvoiceXpathCenMappingValidator(getMappingRegex(), reflections)).getMapping(pathOfMappingConfFile);
        byte[] targetXml = null;
        try {
            for (Map.Entry<String, String> entry : mappings.entries()) {
                String key = entry.getKey();
                GenericOneToOneTransformer transformer = new GenericOneToOneTransformer(key, entry.getValue(), reflections, conversionRegistry);
                transformer.transformCenToXml(invoice, document, errors);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new XMLOutputter().output(document, bos);
            targetXml = bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            errors.add(ConversionIssue.newError(e, e.getMessage()));
        }
        return new BinaryConversionResult(targetXml, errors);
    }

    /**
     * Get the mapping configuration file path
     *
     * @return the path to the file
     */
    public abstract String getMappingPath();

    public void setMappingRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getMappingRegex() {
        return regex;
    }
}
