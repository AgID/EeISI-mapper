
package com.infocert.eigor.api;

import com.infocert.eigor.api.ConversionUtil.*;
import it.infocert.eigor.api.ConversionResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Base64;
import java.util.List;

import static com.infocert.eigor.api.ConversionUtil.*;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests of issues discovered and fixed during the 2nd phase of development,
 * called 'eeisi'.
 */
public class EeisiIssuesTest extends AbstractIssueTest {

    @Test
    public void issueEeisi205() throws Exception {

        // given
        XPathExpression xPathBt46 = ublXpath().compile("//BT-46");

        // when
        ConversionResult<byte[]> conversionResult = this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi205-fattpa.xml",
                "fatturapa", "xmlcen", keepErrorsNotWarnings());

        Document convertedInvoice = documentBuilder.parse( new ByteArrayInputStream( conversionResult.getResult() ) );

        Node bt46Element = (Node) xPathBt46.evaluate(convertedInvoice, XPathConstants.NODE);

        // Prepare a meaningful error message in case the following assertion will fail
        NamedNodeMap attributes = bt46Element.getAttributes();
        String errMessage = "";
        if(attributes.getLength()>=1) {
            for(int i=0; i<attributes.getLength(); i++) {
                errMessage += "'" + attributes.item(i).getNodeName() + "' ";
            }
        }

        assertEquals("Unexpected attributes " + errMessage + "\n" + new String(conversionResult.getResult()), 0, bt46Element.getAttributes().getLength());

    }

    @Test
    public void issueEeisi216() throws Exception {

        // given
        Document originalInvoice = documentBuilder.parse(getClass().getResourceAsStream("/issues/issue-eisi216-ubl.xml"));
        Document convertedInvoice = null;

        // when
        ConversionResult<byte[]> conversionResult = this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi216-ubl.xml",
                "ubl", "ubl", keepErrorsNotWarnings());
        convertedInvoice = documentBuilder.parse( new ByteArrayInputStream( conversionResult.getResult() ) );

        // then

        // check PartyIdentification (BT-46) is properly mapped
        XPathExpression idXpath = ublXpath().compile("//cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID");
        assertEquals("ID should be the same on both invoices. Converted invoice is: " + describeConvertedInvoice(conversionResult), idXpath.evaluate(originalInvoice), idXpath.evaluate(convertedInvoice));

        // assert CompanyID (BT-47) is properly mapped.
        XPathExpression companyIdXpath = ublXpath().compile("//cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID");
        assertEquals("CompanyID should be the same on both invoices. Converted invoice is: " + describeConvertedInvoice(conversionResult), companyIdXpath.evaluate(originalInvoice), companyIdXpath.evaluate(convertedInvoice));

    }

    @Test
    public void issueEeisi195NoDk() {
        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi195-fattpa-noDK.xml",
                "fatturapa", "peppolcn", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi191() {
        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi191-cii.xml",
                "cii", "xmlcen", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi188() {
        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-188-xmlcen.xml",
                "xmlcen", "ubl", keepErrorsNotWarnings());
    }

    @Test
    public void issueEeisi248() {
        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-248-xmlcen_.xml",
                "xmlcen", "ubl", keepErrorsNotWarnings());
    }

    @Test
    public void issueEeisi192() {
        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi192-fattpa.xml",
                "fatturapa", "ubl", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi193a() {

        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi193-fattpa.xml",
                "fatturapa", "fatturapa", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi193b() {

        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi193b-fattpa.xml",
                "fatturapa", "fatturapa", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi7() throws Exception {

        // when
        ConversionResult<byte[]> conversion = this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi7-cen.xml",
                "xmlcen", "fatturapa");

        // then
        assertThat(
                conversion.getIssues().stream().map(issue -> issue +  "\n" ).collect(joining()),
                conversion.hasIssues(),
                is(false) );

        NodeList lines = evalXpathExpressionAsNodeList(conversion, "//NumeroLinea");
        assertEquals("3", lines.item(0).getTextContent() );
        assertEquals("5", lines.item(1).getTextContent() );
        assertEquals("4", lines.item(2).getTextContent() );
        assertEquals("6", lines.item(3).getTextContent() );

    }

    @Test
    public void issueEeisi28() throws XPathExpressionException, IOException {

        String invoiceTemplate = IOUtils.toString(new InputStreamReader( getClass().getResourceAsStream("/examples/xmlcen/eisi-28-issue.xml") ));

        // a conversion UBL - fatturaPA withouth errors.
        ConversionResult<byte[]> conversion = this.conversion.assertConversionWithoutErrors(
                IOUtils.toInputStream(invoiceTemplate, "UTF-8"), "xmlcen", "fatturapa",  new KeepAll());

    }

    /**
     * Let's suppose to have an UBL invoice with very long fields like:
     * <pre>
     *     &lt;cac:PartyIdentification&gt;&lt;cbc:ID&gt;IT:ALBO:IngegneriElettroniciInformaticiIngegneriElettroniciInformatici:123456789012345678901234567890123456789012345678901234567890111&lt;/cbc:ID
     * </pre>
     * This is too long to be stored in XML PA, hence, the not truncated value should be stored in an attached file, in CSV format.
     * This CSV should have several columns, one for the untruncated value, the other for the truncated value.
     */
    @Test
    public void issueEeisi22() throws XPathExpressionException, IOException {

        // a conversion UBL - fatturaPA withouth errors.
        ConversionResult<byte[]> conversion = this.conversion.assertConversionWithoutErrors("/issues/issue-eeisi22-ubl.xml", "ubl", "fatturapa");

        // The CSV in base 64 is the 3rd attachment in this case.
        String truncatedValuesCSVInBase64 = evalXpathExpressionAsString(conversion, "//*[local-name()='Allegati'][3]/*[local-name()='Attachment']/text()");

        String csvSource = new String(Base64.getDecoder().decode(truncatedValuesCSVInBase64));


        List<CSVRecord> records = CSVFormat
                .DEFAULT
                .withFirstRecordAsHeader()
                .parse( new StringReader(csvSource) ).getRecords();
        assertThat( records.size(), is(1) );
        assertThat( records.get(0).get("Original value"), is("Street seller Additional street seller xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Line seller") );
        assertThat( records.get(0).get("Trimmed value"), is("Street seller Additional street seller xxxxxxxxxxxxxxxxxxxxx") );

    }

    @Test
    public void issueEeisi20() throws XPathExpressionException, IOException {

        this.conversion.assertConversionWithoutErrors("/issues/issue-eisi-20-cii.xml", "cii", "fatturapa");

    }



}
