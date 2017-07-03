package it.infocert.eigor.api;

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
import java.util.ArrayList;
import java.util.List;

public class XSDValidator implements IXMLValidator {

    private Schema schema;

    public XSDValidator(File schemaFile) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory.newSchema(schemaFile);
        } catch (SAXException e) {
            throw new RuntimeException("Invalid XSD!", e);
        }
    }

    @Override
    public List<ConversionIssue> validate(byte[] xml) {
        final List<ConversionIssue> errors = new ArrayList<>();
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
                    errors.add(ConversionIssue.newError(exception, "XSD validation error."));
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
