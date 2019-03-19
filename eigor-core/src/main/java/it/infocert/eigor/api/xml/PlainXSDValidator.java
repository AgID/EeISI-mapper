package it.infocert.eigor.api.xml;

import it.infocert.eigor.api.errors.ErrorCode;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;

/**
 * This {@link XSDValidator} is able to load XSD from files or URLs, not from JARs.
 *
 * If you need to load XSD from Jars, please use {@link ClasspathXSDValidator} instead.
 *
 */
@Deprecated
public class PlainXSDValidator extends XSDValidator {

    public PlainXSDValidator(File schemaFile, SchemaFactory schemaFactory, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), schemaFactory, callingLocation);
    }

    public PlainXSDValidator(InputStream schemaFile, SchemaFactory schemaFactory, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), schemaFactory, callingLocation);
    }

    public PlainXSDValidator(Source schemaSource, SchemaFactory schemaFactory, ErrorCode.Location callingLocation) throws SAXException {

        super(callingLocation, null, schemaSource);

    }

    public PlainXSDValidator(File schemaFile, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), callingLocation);
    }

    /**
     * Loads an XSD from an {@link InputStream}.
     * Please, keep in mind that you cannot load XSDs that import other XSD as an inputstream because the parser would not be
     * able to resolve the imports, since the inputstream does not carry any info about the location it has been loaded from.
     */
    public PlainXSDValidator(InputStream schemaFile, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), callingLocation);
    }

    public PlainXSDValidator(Source schemaSource, ErrorCode.Location callingLocation) throws SAXException {

        super(callingLocation, null, schemaSource);

    }

}
