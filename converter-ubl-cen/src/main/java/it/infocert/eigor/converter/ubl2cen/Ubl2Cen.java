package it.infocert.eigor.converter.ubl2cen;

import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The UBL to CEN format converter
 */
public class Ubl2Cen extends Abstract2CenConverter {

    private static final Logger log = LoggerFactory.getLogger(Ubl2Cen.class);
    private static final String FORMAT = "ubl";

    public static final String ONE2ONE_MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/one_to_one.properties";
    public static final String MANY2ONE_MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/many_to_one.properties";
    public static final String ONE2MANY_MAPPING_PATH = "converterdata/converter-ubl-cen/mappings/one_to_many.properties";


    public Ubl2Cen(Reflections reflections) {
        super(reflections);
    }

    /**
     * 1. read document (from xml to Document obj)
     * 2. maps each path into BG/BT obj
     *
     * @param sourceInvoiceStream The stream containing the representation of the invoice to be converted.
     * @return ConversionResult<BG0000Invoice>
     * @throws SyntaxErrorInInvoiceFormatException
     */
    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {
        List<ConversionIssue> errors = new ArrayList<>();

        InputStream clonedInputStream = null;
        File fullSchemaFile = new File("converterdata/converter-ubl-cen/schematron-xslt/EN16931-UBL-validation.xslt");

        IXMLValidator validator;
        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
            clonedInputStream = new ByteArrayInputStream(bytes);
            validator = new SchematronValidator(fullSchemaFile, true);
            errors.addAll(validator.validate(bytes));
        } catch (IOException | IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, "Unable to schematron-validate input!"));
        }

        Document document = getDocument(clonedInputStream);
        ConversionResult<BG0000Invoice> result = applyOne2OneTransformationsBasedOnMapping(document, errors);

        result = applyMany2OneTransformationsBasedOnMapping(result.getResult(), document, result.getIssues());

        return result;
    }

    @Override
    public boolean support(String format) {
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList(FORMAT));
    }

    @Override
    public String getOne2OneMappingPath() {
        return ONE2ONE_MAPPING_PATH;
    }

    @Override
    public String getMany2OneMappingPath() {
        return MANY2ONE_MAPPING_PATH;
    }
}
