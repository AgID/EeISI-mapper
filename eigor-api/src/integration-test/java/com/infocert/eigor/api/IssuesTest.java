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
    public void issue276FromUblToUbl() {
        assertConversionWithoutErrors("/issues/issue-276-ubl.xml", "ubl", "ubl");
    }

    @Test
    public void issue277ThisConversionShouldCompleteWithoutErrors() throws Exception {
        assertConversionWithoutErrors("/issues/issue-277-cii.xml", "cii", "cii");
    }

    @Test
    public void fatturapaToCiiExamples() {
        assertConversionWithoutErrors(
                "/issues/cii-examples/fatturapa/B2G-D_04B_ITBGRGDN77T10L117F_60FPA.xml",
                "fatturapa", "cii");

        assertConversionWithoutErrors(
                "/issues/cii-examples/fatturapa/B2G-D_04B_ITBGRGDN77T10L117F_PEC _91FAT.xml",
                "fatturapa", "cii");
    }

    @Test
    public void ublToCiiExamples() {
        assertConversionWithoutErrors(
                "/issues/cii-examples/ubl/B2G-C_0X_ITBGRGDN77T10L117F_42CEN.XML",
                "ubl", "cii");

        assertConversionWithoutErrors(
                "/issues/cii-examples/ubl/B2G-C_0X_ITBGRGDN77T10L117F_PEC_42UBL.XML",
                "ubl", "cii");

        assertConversionWithoutErrors(
                "/issues/cii-examples/ubl/B2G-D_01C_ITBGRGDN77T10L117F_02UBL.XML",
                "ubl", "cii");

        assertConversionWithoutErrors(
                "/issues/cii-examples/ubl/B2G-D_01C_ITBGRGDN77T10L117F_PEC _02CEN.XML",
                "ubl", "cii");

        assertConversionWithoutErrors(
                "/issues/cii-examples/ubl/urn_notier_SOGG-NOT-00196_2018_9780030222_CP_FATTURA_01_CEN.xml",
                "ubl", "cii");

    }

    @Test
    public void issue254FromFattPaToCii() {
        assertConversionWithoutErrors("/issues/254/fatturapa_newB2G-D_04A_ITBGRGDN77T10L117F_50FPA.XML", "fatturapa", "cii");
    }

    @Test
    public void issue254FromUblToCii_scenario2() {
        assertConversionWithoutErrors("/issues/254/ubl_newB2G-C_01C_CII.XML", "ubl", "cii");
    }

    @Test
    public void issue254FromUblToCii_scenario1() {
        assertConversionWithoutErrors("/issues/254/ubl_B2G-D_01A_ITBGRGDN77T10L117F_36CEN.XML", "ubl", "cii");
    }


    @Test
    public void issue252ThisConversionShouldCompleteWithoutErrors() throws Exception {
        assertConversionWithoutErrors("/issues/issue-252-fattpa.xml", "fatturapa", "ubl");

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

    @Test
    public void issue256() throws Exception {

        InputStream inputFatturaPaXml = invoiceAsStream("/issues/issue-256-fattpa.xml");

        ConversionResult<byte[]> convert = api.convert("fatturapa", "ubl", inputFatturaPaXml);

        String evaluateAttachment = evalXpathExpression(convert, "//*[local-name()='AdditionalDocumentReference']//*[local-name()='Attachment']//*[local-name()='EmbeddedDocumentBinaryObject']/text()");
        String evaluateAttachmentMimeCode = evalXpathExpression(convert, "//*[local-name()='AdditionalDocumentReference']//*[local-name()='Attachment']//*[local-name()='EmbeddedDocumentBinaryObject']/@mimeCode");
        String evaluateAttachmentFileName = evalXpathExpression(convert, "//*[local-name()='AdditionalDocumentReference']//*[local-name()='Attachment']//*[local-name()='EmbeddedDocumentBinaryObject']/@filename");

        Assert.assertTrue(evaluateAttachment!=null && !evaluateAttachment.trim().isEmpty());
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "ZUlHT1IgYXR0YWNobWVudCB0ZXN0", evaluateAttachment);

        Assert.assertTrue(evaluateAttachmentMimeCode!=null && !evaluateAttachmentMimeCode.trim().isEmpty());
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "text/csv", evaluateAttachmentMimeCode);

        Assert.assertTrue(evaluateAttachmentFileName!=null && !evaluateAttachmentFileName.trim().isEmpty());
        Assert.assertEquals(buildMsgForFailedAssertion(convert, new KeepAll()), "Allegato", evaluateAttachmentFileName);
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

    private void assertConversionWithoutErrors(String invoice, String source, String target) {
        InputStream invoiceStream = invoiceAsStream(invoice);
        ConversionResult<byte[]> convert = api.convert(source, target, invoiceStream);
        Assert.assertFalse( buildMsgForFailedAssertion(convert, new KeepAll()), convert.hasIssues() );
    }
}
