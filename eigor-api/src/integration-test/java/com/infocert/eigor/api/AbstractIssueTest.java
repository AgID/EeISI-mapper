package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.Iterator;

public class AbstractIssueTest {

    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    static EigorApi api;
    static ConversionUtil conversion;

    static DocumentBuilder documentBuilder;
    static XPath xPath;
    static File apiFolder;

    @BeforeClass
    public static void setUpXmlInfrastructure() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        documentBuilder = factory
                .newDocumentBuilder();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();
    }

    @BeforeClass
    public static void initApi() throws IOException, ConfigurationException {

        apiFolder = tmp.newFolder();
        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(apiFolder)
                .enableForce()
                .build();

        conversion = new ConversionUtil(api);
    }

    protected XPath ciiXpath() {

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath ublXPath = xPathFactory.newXPath();
        ublXPath.setNamespaceContext(new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                switch (prefix) {
                    case "rsm":
                        return "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100";
                    case "ram":
                        return "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100";
                    case "qdt":
                        return "urn:un:unece:uncefact:data:standard:QualifiedDataType:100";
                    case "udt":
                        return "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100";
                }
                throw new UnsupportedOperationException("Unknown prefix " + prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });

        return ublXPath;
    }

    protected XPath ublXpath() {

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath ublXPath = xPathFactory.newXPath();
        ublXPath.setNamespaceContext(new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                switch (prefix) {
                    case "cbc":
                        return "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
                    case "cac":
                        return "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
                    case "ccts":
                        return "urn:un:unece:uncefact:documentation:2";
                    case "qdt":
                        return "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDataTypes-2";
                    case "udt":
                        return "urn:oasis:names:specification:ubl:schema:xsd:UnqualifiedDataTypes-2";
                }
                throw new UnsupportedOperationException("Unknown prefix " + prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });

        return ublXPath;
    }

    protected String evalXpathExpressionAsString(ConversionResult<byte[]> convert, String expression) throws XPathExpressionException {
        StringReader xmlStringReader = new StringReader(new String(convert.getResult()));
        InputSource is = new InputSource(xmlStringReader);
        XPathExpression expr = xPath.compile(expression);
        return expr.evaluate(is);
    }

    protected NodeList evalXpathExpressionAsNodeList(ConversionResult<byte[]> convert, String expression) throws XPathExpressionException {
        StringReader xmlStringReader = new StringReader(new String(convert.getResult()));
        InputSource is = new InputSource(xmlStringReader);
        XPathExpression expr = xPath.compile(expression);
        return (NodeList) expr.evaluate(is, XPathConstants.NODESET);
    }

    protected static String printDocument(Document doc) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
        transformer.transform(new DOMSource(doc),
                new StreamResult(writer));
        return new String(out.toByteArray());
    }

    /** Try to parse the XML contained in the provided conversionResult and returns it. */
    protected Document parseAsDom(ConversionResult<byte[]> conversionResult) throws SAXException, IOException {
        return documentBuilder.parse(new ByteArrayInputStream(conversionResult.getResult()));
    }

    /** Try to parse the XML retrieved at the given classpath and returns it. */
    protected Document parseAsDom(String pathInClasspath) throws SAXException, IOException {
        return documentBuilder.parse( getClass().getResourceAsStream(pathInClasspath) );
    }

}
