package com.infocert.eigor.api;

import com.infocert.eigor.api.ConversionUtil.KeepAll;
import com.infocert.eigor.api.ConversionUtil.KeepErrosrNotWarnings;
import it.infocert.eigor.api.ConversionResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Base64;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests of issues discovered and fixed during the 2nd phase of development,
 * called 'eeisi'.
 */
public class EeisiIssuesTest extends AbstractIssueTest {

    @Test
    public void issueEeisi193() throws Exception {

        // when
        this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi193-fattpa.xml",
                "fatturapa", "fatturapa", new KeepErrosrNotWarnings());

    }

    @Test
    public void issueEeisi7() throws Exception {

        // when
        ConversionResult<byte[]> conversion = this.conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi7-cen.xml",
                "xmlcen", "fatturapa");

        String truncatedValuesCSVInBase64 = evalXpathExpressionAsString(conversion, "//*[local-name()='Allegati'][3]/*[local-name()='Attachment']/text()");

        System.out.println( new String( conversion.getResult() ) );

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

    @Ignore("Invalid schema")
    @Test
    public void issueEeisi28() throws XPathExpressionException, IOException {

        String invoiceTemplate = IOUtils.toString(new InputStreamReader( getClass().getResourceAsStream("/examples/xmlcen/eisi-28-issue.xml") ));
        String s = "E";
        invoiceTemplate = invoiceTemplate.replace("@@BT-95@@", s);
        invoiceTemplate = invoiceTemplate.replace("@@BT-102@@", s);
        invoiceTemplate = invoiceTemplate.replace("@@BT-118@@", s);
        invoiceTemplate = invoiceTemplate.replace("@@BT-151@@", s);



        // a conversion UBL - fatturaPA withouth errors.
        ConversionResult<byte[]> conversion = this.conversion.assertConversionWithoutErrors(
                IOUtils.toInputStream(invoiceTemplate, "UTF-8"), "xmlcen", "fatturapa",  new KeepAll());

        // The CSV in base 64 is the 3rd attachment in this case.
        String truncatedValuesCSVInBase64 = evalXpathExpressionAsString(conversion, "//*[local-name()='Allegati'][3]/*[local-name()='Attachment']/text()");

        System.out.println( new String( conversion.getResult() ) );

    }

    /**
     * Let's suppose to have an UBL invoice with very long fields like:
     * <pre>
     *     &lt;cac:PartyIdentification&gt;&lt;cbc:ID&gt;IT:ALBO:IngegneriElettroniciInformaticiIngegneriElettroniciInformatici:123456789012345678901234567890123456789012345678901234567890111&lt;/cbc:ID
     * </pre>
     * This is too long to be stored in XML PA, hence, the not truncated value should be stored in an attached file, in CSV format.
     * This CSV should have several columns, one for the untruncated value, the other for the truncated value.
     */
    @Ignore("UBL input schematron fails")
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
        assertThat( records.size(), is(2) );
        assertThat( records.get(0).get("Original value"), is("IngegneriElettroniciInformaticiIngegneriElettroniciInformatici") );
        assertThat( records.get(0).get("Trimmed value"), is("IngegneriElettroniciInformaticiIngegneriElettroniciInformati") );
        assertThat( records.get(1).get("Original value"), is("123456789012345678901234567890123456789012345678901234567890111") );
        assertThat( records.get(1).get("Trimmed value"), is("123456789012345678901234567890123456789012345678901234567890") );

    }



}
