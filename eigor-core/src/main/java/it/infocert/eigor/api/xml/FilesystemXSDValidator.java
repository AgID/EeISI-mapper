package it.infocert.eigor.api.xml;

import it.infocert.eigor.api.errors.ErrorCode;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;

public class FilesystemXSDValidator extends XSDValidator {

    @Deprecated
    public FilesystemXSDValidator(File schemaFile, SchemaFactory schemaFactory, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), schemaFactory, callingLocation);
    }

    @Deprecated
    public FilesystemXSDValidator(InputStream schemaFile, SchemaFactory schemaFactory, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), schemaFactory, callingLocation);
    }

    @Deprecated
    public FilesystemXSDValidator(Source schemaSource, SchemaFactory schemaFactory, ErrorCode.Location callingLocation) throws SAXException {

        super(callingLocation, null, schemaSource);

    }

    @Deprecated
    public FilesystemXSDValidator(File schemaFile, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), callingLocation);
    }

    /**
     * Loads an XSD from an {@link InputStream}.
     * Please, keep in mind that you cannot load XSDs that import other XSD as an inputstream because the parser would not be
     * able to resolve the imports, since the inputstream does not carry any info about the location it has been loaded from.
     */
    public FilesystemXSDValidator(InputStream schemaFile, ErrorCode.Location callingLocation) throws SAXException {
        this(new StreamSource(schemaFile), callingLocation);
    }

    public FilesystemXSDValidator(Source schemaSource, ErrorCode.Location callingLocation) throws SAXException {

        super(callingLocation, null, schemaSource);

    }

}
