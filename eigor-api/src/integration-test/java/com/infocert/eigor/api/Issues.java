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
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static org.junit.Assert.assertTrue;

public class Issues {

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
    public void issue245() throws Exception {

        InputStream inputFatturaPaXml = getClass().getResourceAsStream("/issues/issue-245-fattpa.xml");
        Assert.assertNotNull(inputFatturaPaXml);

        String sourceFormat = "fatturapa";
        String destinationFormat = "ubl";
        ConversionResult<byte[]> convert = api.convert(sourceFormat, destinationFormat, inputFatturaPaXml);

        StringReader xmlStringReader = new StringReader(new String(convert.getResult()));
        InputSource is = new InputSource( xmlStringReader );

        XPathExpression expr = xPath.compile("//*[local-name()='AccountingSupplierParty']//*[local-name()='PartyTaxScheme']//*[local-name()='ID']/text()");
        String evaluate = expr.evaluate(is);

        Assert.assertTrue(evaluate!=null && !evaluate.trim().isEmpty());
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "VAT", evaluate);
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
