package it.infocert.eigor.api;

import it.infocert.eigor.api.xml.CacheResourceValidator;
import it.infocert.eigor.api.xml.LoggingResourceResolver;
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
    private static final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private static final Logger log = LoggerFactory.getLogger(XSDValidator.class);
    static {

        // TODO: caching schemas
        // this can probably improve the time spent downloading XSD.
        // XMLCatalogResolver cr = new XMLCatalogResolver();
        // Please, read the very interesting http://xmlresolver.org/
        //
        // - in schemaFactory you can set a resource resolver that has the responsibility to download the schemas
        // - the default resource resolver is null.
        // - we have a CacheResourceValidator under development
        // - there is http://xmlresolver.org/ but it breaks with a NPE with the schemas used in Eigor
        // - there is LoggingLSResourceResolver, useful for logging the requested schemas

        CacheResourceValidator cacheResourceValidator = new CacheResourceValidator(new File("C:\\Users\\danidemi\\tmp\\eigor\\xsdcache"));

        LoggingResourceResolver newResolver = new LoggingResourceResolver();
        newResolver.setWrappedResourceResolver(cacheResourceValidator);
        schemaFactory.setResourceResolver(newResolver);
    }

    public XSDValidator(File schemaFile) throws SAXException {
        this(new StreamSource(schemaFile));
    }

    public XSDValidator(InputStream schemaFile) throws SAXException {
        this(new StreamSource(schemaFile));
    }

    public XSDValidator(Source schemaSource) throws SAXException {
        long delta = System.currentTimeMillis();
        try {
            schema = schemaFactory.newSchema(schemaSource);
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
}
