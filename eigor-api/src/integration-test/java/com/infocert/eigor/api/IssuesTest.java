package com.infocert.eigor.api;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static it.infocert.eigor.test.Utils.invoiceAsStream;

public class IssuesTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    EigorApi api;

    private static DocumentBuilder documentBuilder;
    private static XPath xPath;

    @BeforeClass public static void setUpXmlInfrastructure() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        documentBuilder = factory.newDocumentBuilder();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();
    }

    @Before public void initApi() throws IOException, ConfigurationException {
        api = new EigorApiBuilder()
                .enableAutoCopy()
                .withOutputFolder(tmp.newFolder())
                .enableForce()
                .build();
    }


    @Test
    public void issue252() throws Exception {
        InputStream ciiInStream = invoiceAsStream("/issues/issue-252-fattpa.xml");
        ConversionResult<byte[]> convert = api.convert("fatturapa", "ubl", ciiInStream);
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "aa", "bb");
    }

    @Test
    public void issue269() throws Exception {
        InputStream ciiInStream = invoiceAsStream("/issues/issue-269-cii.xml");
        ConversionResult<byte[]> convert = api.convert("cii", "fatturapa", ciiInStream);
        String evaluate = evalXpathExpression(convert, "//*[local-name()='CessionarioCommittente']//*[local-name()='IdCodice']/text()");
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "97735020584", evaluate);
    }

    @Test
    public void issue245() throws Exception {

        InputStream inputFatturaPaXml = invoiceAsStream("/issues/issue-245-fattpa.xml");

        ConversionResult<byte[]> convert = api.convert("fatturapa", "ubl", inputFatturaPaXml);

        String evaluate = evalXpathExpression(convert, "//*[local-name()='AccountingSupplierParty']//*[local-name()='PartyTaxScheme']//*[local-name()='ID']/text()");

        Assert.assertTrue(evaluate!=null && !evaluate.trim().isEmpty());
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "VAT", evaluate);
    }

    private String evalXpathExpression(ConversionResult<byte[]> convert, String expression) throws XPathExpressionException {
        StringReader xmlStringReader = new StringReader(new String(convert.getResult()));
        InputSource is = new InputSource( xmlStringReader );
        XPathExpression expr = xPath.compile(expression);
        return expr.evaluate(is);
    }

    private static class KeepByErrorCode implements Predicate<IConversionIssue> {
        private final String errorCode;

        public KeepByErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public boolean apply(@Nullable IConversionIssue input) {
            return input.getErrorMessage() != null
                && input.getErrorMessage().getErrorCode()!=null
                && input.getErrorMessage().getErrorCode().toString().equals(errorCode);
        }
    }

    private static class KeepAll implements Predicate<IConversionIssue> {

        @Override
        public boolean apply(@Nullable IConversionIssue input) {
            return true;
        }
    }

    private String buildMsgForFailedAssertion(ConversionResult<byte[]> convert, Predicate<IConversionIssue> predicate){
        Iterable<IConversionIssue> conversionIssues = Iterables.filter(convert.getIssues(), predicate);
        StringBuilder issuesDescription = new StringBuilder();
        boolean areThereIssues = conversionIssues.iterator().hasNext();
        if(areThereIssues){
            issuesDescription.append("\n\nIssues:\n\n");
            for (IConversionIssue issue : conversionIssues) {
                issuesDescription
                        .append( issue.getMessage() )
                        .append("\n")
                        .append("   ►►► ")
                        .append(issue.getCause()!=null ? issue.getCause().getMessage() : "no details")
                        .append("\n\n");
            }
            issuesDescription.append( new String(convert.getResult()) )
                    .append("\n\n");
        }
        return issuesDescription.toString();
    }
}
