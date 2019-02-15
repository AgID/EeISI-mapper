package com.infocert.eigor.api;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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

public class AbstractIssueTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    EigorApi api;
    ConversionUtil conversion;

    static DocumentBuilder documentBuilder;
    static XPath xPath;
    File apiFolder;

    @BeforeClass
    public static void setUpXmlInfrastructure() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        documentBuilder = factory.newDocumentBuilder();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();
    }

    @Before
    public void initApi() throws IOException, ConfigurationException {

        apiFolder = tmp.newFolder();
        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(apiFolder)
                .enableForce()
                .build();

        conversion = new ConversionUtil(api);
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

}
