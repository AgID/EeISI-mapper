package it.infocert.eigor.converter.cen2ubl;

import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ConverterUnitTest {

    protected static DocumentBuilderFactory factory;
    protected static XPathFactory xPathFactory;

    @BeforeClass
    public static void setUpStatic() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        xPathFactory = XPathFactory.newInstance();
    }

    protected Document bytesToDom(byte[] ublXML) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(ublXML));
    }

    protected String getStringByXPath(Document doc, String xpath) throws XPathExpressionException {
        XPath xPath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xPath.compile(xpath);
        return (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
    }
}
