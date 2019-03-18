package it.infocert.eigor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.helger.xml.ls.SimpleLSResourceResolver;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.util.StringJoiner;

public class LoadFromXlasspathTest {


    //SimpleLSResourceResolver

    @Test
    public void shouldLoadFromClasspath() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source source = new StreamSource(getClass().getResourceAsStream("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));
        schemaFactory.newSchema( source );
    }

    @Test
    public void shouldLoadFromClasspathWithResolver() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver( new MyLSResolver() );
        Source source = new StreamSource(getClass().getResourceAsStream("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));
        schemaFactory.newSchema( source );
    }

    @Test
    public void shouldLoadFromClasspathWithSimpleLSResourceResolver() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver( new SimpleLSResourceResolver() );
        Source source = new StreamSource(getClass().getResourceAsStream("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));
        schemaFactory.newSchema( source );
    }

    @Test
    public void shouldLoadFromDom() throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource("systemid");

        Document doc = dBuilder.parse(is);

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source source = new DOMSource(doc);
        schemaFactory.newSchema( source );
    }

    @Test
    public void shouldLoadFromFile() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.newSchema( new File("C:\\Users\\esche\\workspace\\repo\\infocert\\eeisi\\eigor-test-schemas\\src\\main\\resources\\test-converterdata\\test-converter-cii-cen\\cii\\xsd\\uncoupled\\data\\standard\\CrossIndustryInvoice_100pD16B.xsd") );
    }

    public class MyResolver implements EntityResolver {
        public InputSource resolveEntity (String publicId, String systemId)
        {
            if (systemId.equals("http://www.myhost.com/today")) {
                // return a special input source
                Reader reader = null; //new MyReader();
                return new InputSource(reader);
            } else {
                // use the default behaviour
                return null;
            }
        }
    }

    class MyLSResolver implements LSResourceResolver {

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            // LSResourceResolver provides a way for applications to redirect references to external resources.
            // Applications needing to implement custom handling for external resources can implement this interface and register
            // their implementation by setting the "resource-resolver" parameter of DOMConfiguration objects attached to LSParser and LSSerializer.
            //
            // It can also be register on DOMConfiguration objects attached to Document if the "LS" feature is supported.
            // The LSParser will then allow the application to intercept any external entities,
            // including the external DTD subset and external parameter entities, before including them.
            // The top-level document entity is never passed to the resolveResource method.
            // Many DOM applications will not need to implement this interface, but it will be especially useful for applications that build XML documents from databases or other specialized input sources, or for applications that use URNs.

            String s = new StringJoiner("|")
                    .add(type != null ? type : "")
                    .add(namespaceURI != null ? namespaceURI : "")
                    .add(publicId != null ? publicId : "")
                    .add(systemId != null ? systemId : "")
                    .add(baseURI != null ? baseURI : "")
                    .toString();

            System.out.println(s + "\n");

            ImmutableMap<String, LSInput> map = ImmutableMap.<String, LSInput>builder()
                    .put("http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:QualifiedDataType:100||CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd|",
                            new MyLsInput("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd"))
                    .put("http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100||CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd|",
                            new MyLsInput("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd"))
                    .put("http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100||CrossIndustryInvoice_UnqualifiedDataType_100pD16B.xsd|",
                            new MyLsInput("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_UnqualifiedDataType_100pD16B.xsd"))
                    .build();

            LSInput ls = map.get(s);

            return ls;
        }
    }

    class MyLsInput implements LSInput {

        private String resource;
        private String systemId;
        private String encoding;
        private String baseURI;
        private String publicId;

        public MyLsInput(String resource) {
            this.resource = resource;
        }

        /**
         * An attribute of a language and binding dependent type that represents
         * a stream of 16-bit units. The application must encode the stream
         * using UTF-16 (defined in [Unicode] and in [ISO/IEC 10646]). It is not a requirement to have an XML declaration when
         * using character streams. If an XML declaration is present, the value
         * of the encoding attribute will be ignored.
         */
        @Override
        public Reader getCharacterStream() {
            InputStream resourceAsStream = Preconditions.checkNotNull( getClass().getResourceAsStream(resource), "unable to find %s", resource );
            return new InputStreamReader(resourceAsStream);
        }

        /**
         * An attribute of a language and binding dependent type that represents
         * a stream of 16-bit units. The application must encode the stream
         * using UTF-16 (defined in [Unicode] and in [ISO/IEC 10646]). It is not a requirement to have an XML declaration when
         * using character streams. If an XML declaration is present, the value
         * of the encoding attribute will be ignored.
         *
         * @param characterStream
         */
        @Override
        public void setCharacterStream(Reader characterStream) {
            throw new UnsupportedOperationException();
        }

        /**
         * An attribute of a language and binding dependent type that represents
         * a stream of bytes.
         * <br> If the application knows the character encoding of the byte
         * stream, it should set the encoding attribute. Setting the encoding in
         * this way will override any encoding specified in an XML declaration
         * in the data.
         */
        @Override
        public InputStream getByteStream() {
            return getClass().getResourceAsStream( resource );
        }

        /**
         * An attribute of a language and binding dependent type that represents
         * a stream of bytes.
         * <br> If the application knows the character encoding of the byte
         * stream, it should set the encoding attribute. Setting the encoding in
         * this way will override any encoding specified in an XML declaration
         * in the data.
         *
         * @param byteStream
         */
        @Override
        public void setByteStream(InputStream byteStream) {
            throw new UnsupportedOperationException();
        }

        /**
         * String data to parse. If provided, this will always be treated as a
         * sequence of 16-bit units (UTF-16 encoded characters). It is not a
         * requirement to have an XML declaration when using
         * <code>stringData</code>. If an XML declaration is present, the value
         * of the encoding attribute will be ignored.
         */
        @Override
        public String getStringData() {
            try {
                return IOUtils.toString( getByteStream(), "UTF-16" );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * String data to parse. If provided, this will always be treated as a
         * sequence of 16-bit units (UTF-16 encoded characters). It is not a
         * requirement to have an XML declaration when using
         * <code>stringData</code>. If an XML declaration is present, the value
         * of the encoding attribute will be ignored.
         *
         * @param stringData
         */
        @Override
        public void setStringData(String stringData) {
            throw new UnsupportedOperationException();
        }

        /**
         * The system identifier, a URI reference [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], for this
         * input source. The system identifier is optional if there is a byte
         * stream, a character stream, or string data. It is still useful to
         * provide one, since the application will use it to resolve any
         * relative URIs and can include it in error messages and warnings. (The
         * LSParser will only attempt to fetch the resource identified by the
         * URI reference if there is no other input available in the input
         * source.)
         * <br> If the application knows the character encoding of the object
         * pointed to by the system identifier, it can set the encoding using
         * the <code>encoding</code> attribute.
         * <br> If the specified system ID is a relative URI reference (see
         * section 5 in [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]), the DOM
         * implementation will attempt to resolve the relative URI with the
         * <code>baseURI</code> as the base, if that fails, the behavior is
         * implementation dependent.
         */
        @Override
        public String getSystemId() {
            return systemId;
        }

        /**
         * The system identifier, a URI reference [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], for this
         * input source. The system identifier is optional if there is a byte
         * stream, a character stream, or string data. It is still useful to
         * provide one, since the application will use it to resolve any
         * relative URIs and can include it in error messages and warnings. (The
         * LSParser will only attempt to fetch the resource identified by the
         * URI reference if there is no other input available in the input
         * source.)
         * <br> If the application knows the character encoding of the object
         * pointed to by the system identifier, it can set the encoding using
         * the <code>encoding</code> attribute.
         * <br> If the specified system ID is a relative URI reference (see
         * section 5 in [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]), the DOM
         * implementation will attempt to resolve the relative URI with the
         * <code>baseURI</code> as the base, if that fails, the behavior is
         * implementation dependent.
         *
         * @param systemId
         */
        @Override
        public void setSystemId(String systemId) {
            throw new UnsupportedOperationException();
        }

        /**
         * The public identifier for this input source. This may be mapped to an
         * input source using an implementation dependent mechanism (such as
         * catalogues or other mappings). The public identifier, if specified,
         * may also be reported as part of the location information when errors
         * are reported.
         */
        @Override
        public String getPublicId() {
            return publicId;
        }

        /**
         * The public identifier for this input source. This may be mapped to an
         * input source using an implementation dependent mechanism (such as
         * catalogues or other mappings). The public identifier, if specified,
         * may also be reported as part of the location information when errors
         * are reported.
         *
         * @param publicId
         */
        @Override
        public void setPublicId(String publicId) {
            throw new UnsupportedOperationException();
        }

        /**
         * The base URI to be used (see section 5.1.4 in [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]) for
         * resolving a relative <code>systemId</code> to an absolute URI.
         * <br> If, when used, the base URI is itself a relative URI, an empty
         * string, or null, the behavior is implementation dependent.
         */
        @Override
        public String getBaseURI() {
            return baseURI;
        }

        /**
         * The base URI to be used (see section 5.1.4 in [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]) for
         * resolving a relative <code>systemId</code> to an absolute URI.
         * <br> If, when used, the base URI is itself a relative URI, an empty
         * string, or null, the behavior is implementation dependent.
         *
         * @param baseURI
         */
        @Override
        public void setBaseURI(String baseURI) {
            throw new UnsupportedOperationException();
        }

        /**
         * The character encoding, if known. The encoding must be a string
         * acceptable for an XML encoding declaration ([<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>] section
         * 4.3.3 "Character Encoding in Entities").
         * <br> This attribute has no effect when the application provides a
         * character stream or string data. For other sources of input, an
         * encoding specified by means of this attribute will override any
         * encoding specified in the XML declaration or the Text declaration, or
         * an encoding obtained from a higher level protocol, such as HTTP [<a href='http://www.ietf.org/rfc/rfc2616.txt'>IETF RFC 2616</a>].
         */
        @Override
        public String getEncoding() {
            return encoding;
        }

        /**
         * The character encoding, if known. The encoding must be a string
         * acceptable for an XML encoding declaration ([<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>] section
         * 4.3.3 "Character Encoding in Entities").
         * <br> This attribute has no effect when the application provides a
         * character stream or string data. For other sources of input, an
         * encoding specified by means of this attribute will override any
         * encoding specified in the XML declaration or the Text declaration, or
         * an encoding obtained from a higher level protocol, such as HTTP [<a href='http://www.ietf.org/rfc/rfc2616.txt'>IETF RFC 2616</a>].
         *
         * @param encoding
         */
        @Override
        public void setEncoding(String encoding) {
            throw new UnsupportedOperationException();
        }

        /**
         * If set to true, assume that the input is certified (see section 2.13
         * in [<a href='http://www.w3.org/TR/2004/REC-xml11-20040204/'>XML 1.1</a>]) when
         * parsing [<a href='http://www.w3.org/TR/2004/REC-xml11-20040204/'>XML 1.1</a>].
         */
        @Override
        public boolean getCertifiedText() {
            return false;
        }

        /**
         * If set to true, assume that the input is certified (see section 2.13
         * in [<a href='http://www.w3.org/TR/2004/REC-xml11-20040204/'>XML 1.1</a>]) when
         * parsing [<a href='http://www.w3.org/TR/2004/REC-xml11-20040204/'>XML 1.1</a>].
         *
         * @param certifiedText
         */
        @Override
        public void setCertifiedText(boolean certifiedText) {
            throw new UnsupportedOperationException();
        }
    }

}
