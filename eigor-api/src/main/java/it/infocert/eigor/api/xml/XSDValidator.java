package it.infocert.eigor.api.xml;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.IXMLValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class XSDValidator implements IXMLValidator {

    private Schema schema;
    private static final SchemaFactory DEFAULT_SCHEMA_FACTORY;
    private static final Logger log = LoggerFactory.getLogger(XSDValidator.class);
    private final SchemaFactory overriddenSchemaFactory;

    static {
        DEFAULT_SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DEFAULT_SCHEMA_FACTORY.setResourceResolver(new LoggingResourceResolver());
    }

    public XSDValidator(File schemaFile, SchemaFactory schemaFactory) throws SAXException {
        this(new StreamSource(schemaFile), schemaFactory);
    }

    public XSDValidator(InputStream schemaFile, SchemaFactory schemaFactory) throws SAXException {
        this(new StreamSource(schemaFile), schemaFactory);
    }

    public XSDValidator(Source schemaSource, SchemaFactory schemaFactory) throws SAXException {
        overriddenSchemaFactory = null;
        long delta = System.currentTimeMillis();
        try {
            schema = schemaFactoryToUse().newSchema(schemaSource);
        }finally {
            delta = System.currentTimeMillis() - delta;
            log.info(MarkerFactory.getMarker("PERFORMANCE"), "Loaded '{}' in {}ms.", schemaSource.getSystemId() != null ? schemaSource.getSystemId() : schemaSource, delta);
        }
    }

    public XSDValidator(File schemaFile) throws SAXException {
        this(new StreamSource(schemaFile));
    }

    /**
     * Loads an XSD from an {@link InputStream}.
     * Please, keep in mind that you cannot load XSDs that import other XSD as an inputstream because the parser would not be
     * able to resolve the imports, since the inputstream does not carry any info about the location it has been loaded from.
     */
    public XSDValidator(InputStream schemaFile) throws SAXException {
        this(new StreamSource(schemaFile));
    }

    public XSDValidator(Source schemaSource) throws SAXException {
        overriddenSchemaFactory = null;
        long delta = System.currentTimeMillis();
        try {
            schema = schemaFactoryToUse().newSchema(schemaSource);
        }finally {
            delta = System.currentTimeMillis() - delta;
            log.info(MarkerFactory.getMarker("PERFORMANCE"), "Loaded '{}' in {}ms.", schemaSource.getSystemId() != null ? schemaSource.getSystemId() : schemaSource, delta);
        }
    }

    @Override
    public List<IConversionIssue> validate(byte[] xml) {
        final List<IConversionIssue> errors = new ArrayList<>();
        Source xmlFile = new StreamSource(new ByteArrayInputStream(xml));
        Validator validator = schema.newValidator();
        try {
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    errors.add(ConversionIssue.newWarning(exception));
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {

                    int lineNumber = exception.getLineNumber();
                    int columnNumber = exception.getColumnNumber();
                    String message = String.format( "XSD validation error at %d:%d. %s",
                            lineNumber, columnNumber,
                            exception.getMessage() );
                    errors.add(ConversionIssue.newError(exception, message));
                    log.error(message, exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });
            validator.validate(xmlFile);
        } catch (SAXException | IOException e) {
            errors.add(ConversionIssue.newError(e, "XSD validation failed!"));
        }
        return errors;
    }

    private SchemaFactory schemaFactoryToUse() {
        return this.overriddenSchemaFactory!=null ? overriddenSchemaFactory : DEFAULT_SCHEMA_FACTORY;
    }
}
