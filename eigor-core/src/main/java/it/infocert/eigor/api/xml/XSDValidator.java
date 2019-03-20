package it.infocert.eigor.api.xml;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.IXMLValidator;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class XSDValidator implements IXMLValidator {

    private final ErrorCode.Location callingLocation;
    private final SchemaFactory overriddenSchemaFactory;
    private final Schema schema;
    private static final SchemaFactory DEFAULT_SCHEMA_FACTORY;
    private static final Logger log = LoggerFactory.getLogger(XSDValidator.class);

    static {
        DEFAULT_SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DEFAULT_SCHEMA_FACTORY.setResourceResolver(new LoggingResourceResolver());
    }

    protected XSDValidator(ErrorCode.Location callingLocation, SchemaFactory overriddenSchemaFactory, Source schemaSource) {
        this.callingLocation = checkNotNull( callingLocation );
        this.overriddenSchemaFactory = overriddenSchemaFactory;
        long delta = System.currentTimeMillis();
        try {
            schema = schemaFactoryToUse().newSchema(schemaSource);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } finally {
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
                    int lineNumber = exception.getLineNumber();
                    int columnNumber = exception.getColumnNumber();
                    String message = String.format("XSD validation warning at %d:%d. %s",
                            lineNumber, columnNumber,
                            exception.getMessage());
                    errors.add(ConversionIssue.newWarning(exception, "XSD validation warning",
                            callingLocation,
                            ErrorCode.Action.XSD_VALIDATION,
                            ErrorCode.Error.INVALID,
                            Pair.of(ErrorMessage.SOURCEMSG_PARAM, message)
                    ));
                    log.warn(message, exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    int lineNumber = exception.getLineNumber();
                    int columnNumber = exception.getColumnNumber();
                    String message = String.format("XSD validation error at %d:%d. %s",
                            lineNumber, columnNumber,
                            exception.getMessage());
                    errors.add(ConversionIssue.newError(exception, "XSD validation failed",
                            callingLocation,
                            ErrorCode.Action.XSD_VALIDATION,
                            ErrorCode.Error.INVALID,
                            Pair.of(ErrorMessage.SOURCEMSG_PARAM, message)
                    ));
                    log.error(message, exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });
            validator.validate(xmlFile);
        } catch (SAXException | IOException e) {
            errors.add(ConversionIssue.newError(e, "XSD validator error!",
                    callingLocation,
                    ErrorCode.Action.XSD_VALIDATION,
                    ErrorCode.Error.INVALID,
                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
        }
        return errors;
    }

    private SchemaFactory schemaFactoryToUse() {
        return this.overriddenSchemaFactory != null ? overriddenSchemaFactory : DEFAULT_SCHEMA_FACTORY;
    }
}
