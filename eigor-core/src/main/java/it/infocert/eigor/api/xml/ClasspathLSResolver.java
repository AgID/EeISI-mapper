package it.infocert.eigor.api.xml;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


class ClasspathLSResolver implements LSResourceResolver {

    private static final Map<String, LSInputFactory> map;

    static {
        map = ImmutableMap.<String, LSInputFactory>builder()
            .put(
                "http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:QualifiedDataType:100||CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd|",
                new ClasspathLSInput(
                        "/converterdata/converter-commons/cii/xsd/coupled/data/standard/CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd",
                        "UTF-16",
                        null,
                        "urn:un:unece:uncefact:data:standard:QualifiedDataType:100",
                        null
                        )
            )
            .put(
                "http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:codelist:standard:EDIFICAS-EU:AccountingAccountType:D11A||../../codelist/standard/EDIFICAS-EU_AccountingAccountType_D11A.xsd|urn:un:unece:uncefact:data:standard:QualifiedDataType:100",
                    new ClasspathLSInput(
                            "/converterdata/converter-commons/cii/xsd/coupled/codelist/standard/EDIFICAS-EU_AccountingAccountType_D11A.xsd",
                            "UTF-16",
                            null,
                            "urn:un:unece:uncefact:codelist:standard:EDIFICAS-EU:AccountingAccountType:D11A",
                            null
                    )
            )
//                .put("http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:QualifiedDataType:100||CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd|",
//                        new ClasspathLSInput("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_QualifiedDataType_100pD16B.xsd"))
//                .put("http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100||CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd|",
//                        new ClasspathLSInput("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_ReusableAggregateBusinessInformationEntity_100pD16B.xsd"))
//                .put("http://www.w3.org/2001/XMLSchema|urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100||CrossIndustryInvoice_UnqualifiedDataType_100pD16B.xsd|",
//                        new ClasspathLSInput("/test-converterdata/test-converter-cii-cen/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_UnqualifiedDataType_100pD16B.xsd"))
                .build();
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

        String s = new StringJoiner("|")
                .add(type != null ? type : "")
                .add(namespaceURI != null ? namespaceURI : "")
                .add(publicId != null ? publicId : "")
                .add(systemId != null ? systemId : "")
                .add(baseURI != null ? baseURI : "")
                .toString();

        System.out.println("resolving: " + s);

        LSInputFactory ls = map.get(s);

        if(ls != null) {
            return ls.build(type, namespaceURI, publicId, systemId, baseURI);
            //throw new RuntimeException("Unable to get an LSInput for '" + s + "'");
        }

        return new SmartLSInputFactory("/converterdata/converter-commons/cii/xsd/coupled/data/standard/").build(type, namespaceURI, publicId, systemId, baseURI);

    }

    private interface LSInputFactory {
        LSInput build( String type, String namespaceURI, String publicId, String systemId, String baseURI );
    }

    private static class SmartLSInputFactory implements LSInputFactory {

        private final String basePath;

        private SmartLSInputFactory(String basePath) {
            this.basePath = checkNotNull( basePath );
        }

        @Override
        public LSInput build(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

            LinkedList<String> resourceArr = new LinkedList<>( Arrays.asList( basePath.split("/") ) );

            LinkedList<String> systemIdArr = new LinkedList<>( Arrays.asList( systemId.split("/") ) );

            while(systemIdArr.get(0).equals("..")) {
                systemIdArr.remove(0);
                resourceArr.removeLast();
            }

            String resource =
                    resourceArr.stream().collect(Collectors.joining("/")) + "/" +
                            systemIdArr.stream().collect(Collectors.joining("/"));

            return new ClasspathLSInput(
                    resource,
                    "UTF-16",
                    publicId,
                    systemId,
                    baseURI
            );
        }
    }

    private static class ClasspathLSInput implements LSInput, LSInputFactory {

        private final String resource;
        private final String systemId;
        private final String encoding;
        private final String baseURI;
        private final String publicId;

        public ClasspathLSInput(String resource) {
            this.resource = resource;
            systemId = null;
            encoding = null;
            baseURI = null;
            publicId = null;
        }

        public ClasspathLSInput(String resource, String encoding, String publicId, String systemId, String baseURI) {
            this.resource = resource;
            this.systemId = systemId;
            this.encoding = encoding;
            this.baseURI = baseURI;
            this.publicId = publicId;
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

            logResource();

            InputStream resourceAsStream = checkNotNull( getClass().getResourceAsStream(resource), "unable to find %s", resource );
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
            logResource();
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

        private void logResource() {
            System.out.println("Loading resource: " + resource);
        }

        @Override
        public LSInput build(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            return this;
        }
    }
}


