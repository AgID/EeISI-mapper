package it.infocert.eigor.api.xml;

import it.infocert.eigor.api.errors.ErrorCode;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

/**
 * An {@link it.infocert.eigor.api.IXMLValidator} that is able to load XSDs from JARs that uses &lt;import&gt;.
 */
public class ClasspathXSDValidator extends XSDValidator {

    public ClasspathXSDValidator(String classpathResource, ErrorCode.Location callingLocation) {
        super(callingLocation, buildSchemaFactory(classpathResource), new StreamSource(ClasspathXSDValidator.class.getResourceAsStream(classpathResource)) );
    }

    private static SchemaFactory buildSchemaFactory(String classpathResource) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver( new ResourceBasedClasspathLSResolver(classpathResource) );
        return schemaFactory;
    }

}
