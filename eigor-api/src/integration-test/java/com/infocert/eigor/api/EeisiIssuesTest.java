package com.infocert.eigor.api;

import com.infocert.eigor.api.ConversionUtil.KeepAll;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.matchers.CompareMatcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Base64;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests of issues discovered and fixed during the 2nd phase of development,
 * called 'eeisi'.
 */
public class EeisiIssuesTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    EigorApi api;
    ConversionUtil conversion;

    private static DocumentBuilder documentBuilder;
    private static XPath xPath;
    private File apiFolder;

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

    /**
     * Let's suppose to have an UBL invoice with very long fields like:
     * <pre>
     *     &lt;cac:PartyIdentification&gt;&lt;cbc:ID&gt;IT:ALBO:IngegneriElettroniciInformaticiIngegneriElettroniciInformatici:123456789012345678901234567890123456789012345678901234567890111&lt;/cbc:ID
     * </pre>
     * This is too long to be stored in XML PA, hence, the untruncated value should be stored in "attachment".
     */
    @Test
    public void issueEeisi22() throws XPathExpressionException {


        ConversionResult<byte[]> convert = conversion.assertConversionWithoutErrors("/issues/issue-eeisi22-ubl.xml", "ubl", "fatturapa");

        System.out.println( new String(convert.getResult()) );

        String s = evalXpathExpression(convert, "//*[local-name()='Allegati'][3]/*[local-name()='Attachment']/text()");

        System.out.println( s );

        String s1 = new String(Base64.getDecoder().decode(s));

        System.out.println( s1 );

//        String evaluate = evalXpathExpression(convert, "//*[local-name()='FatturaElettronicaBody']//*[local-name()='DatiGenerali']//*[local-name()='DatiTrasporto']//*[local-name()='DataOraConsegna']/text()");
//
//        assertTrue(convert.getIssues().isEmpty()); // no warnings for text exceeding length limit
//
//        assertTrue(evaluate != null && !evaluate.trim().isEmpty());
//        Assert.assertEquals(conversion.buildMsgForFailedAssertion(convert, new KeepAll(), null), "2017-10-15T00:00:00", evaluate);
    }

    private String evalXpathExpression(ConversionResult<byte[]> convert, String expression) throws XPathExpressionException {
        StringReader xmlStringReader = new StringReader(new String(convert.getResult()));
        InputSource is = new InputSource(xmlStringReader);
        XPathExpression expr = xPath.compile(expression);
        return expr.evaluate(is);
    }

}
