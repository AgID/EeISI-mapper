package it.infocert.eigor.api;

import com.helger.commons.xml.ls.LoggingLSResourceResolver;
import org.w3c.dom.ls.LSResourceResolver;
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

    public XSDValidator(File schemaFile) throws SAXException {
        this(new StreamSource(schemaFile));
    }

    public XSDValidator(InputStream schemaFile) throws SAXException {
        this(new StreamSource(schemaFile));
    }

    public XSDValidator(Source schemaSource) throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // this can probably improve the time spens downloading XSD.
        // XMLCatalogResolver cr = new XMLCatalogResolver();
        // Please, read the very interesting http://xmlresolver.org/
        LSResourceResolver originalResourceResolver = schemaFactory.getResourceResolver();
        LSResourceResolver newResolver = null;
        if(originalResourceResolver!=null) {
            LoggingLSResourceResolver anotherResolver = new LoggingLSResourceResolver();
            anotherResolver.setWrappedResourceResolver(originalResourceResolver);
            newResolver = anotherResolver;
        }else{
            newResolver = new LoggingLSResourceResolver();
        }
        schemaFactory.setResourceResolver(newResolver);

        schema = schemaFactory.newSchema( schemaSource );
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
