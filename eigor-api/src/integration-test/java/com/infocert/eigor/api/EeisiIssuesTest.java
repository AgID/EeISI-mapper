
package com.infocert.eigor.api;

import com.infocert.eigor.api.ConversionUtil.*;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0017TenderOrLotReference;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static com.infocert.eigor.api.ConversionUtil.*;
import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests of issues discovered and fixed during the 2nd phase of development,
 * called 'eeisi'.
 */
public class EeisiIssuesTest extends AbstractIssueTest {

    @Test
    public void issueEisi274_shouldMapBT133() throws Exception {

        String sourceInvoice = "/issues/issue-eisi-274-ubl.xml";
        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                sourceInvoice,
                "ubl", "ubl", keepErrorsNotWarnings());
        String msg = errorMessage(conversionResult);

        Document sourceDom = parseAsDom(sourceInvoice);
        Document targetDom = parseAsDom(conversionResult);

        XPathExpression bt133Xpath = ublXpath().compile("(//cac:InvoiceLine)[1]/cbc:AccountingCost/text()");

        // verify BT133 in source invoice
        assertThat( bt133Xpath.evaluate( sourceDom ), equalTo( "5555" ) );

        // verify BT133 in intermediate invoice
        String bt133 = evalExpression(() -> conversionResult.getCenInvoice().getBG0025InvoiceLine(0).getBT0133InvoiceLineBuyerAccountingReference(0).getValue());
        assertThat(msg, bt133, equalTo("5555") );

        // verify BT133 in target invoice
        assertThat( bt133Xpath.evaluate( targetDom ), equalTo( "5555" ) );


    }

    @Test
    public void issueEisi294() throws Exception {

        // given
        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-294-cii.xml",
                "cii", "cii", ignoreAll());

        String errMsg = errorMessage(conversionResult);

        LocalDate bt26 = evalExpression( () -> conversionResult.getCenInvoice().getBG0003PrecedingInvoiceReference(0).getBT0026PrecedingInvoiceIssueDate(0).getValue() );
        assertThat( errMsg, bt26, equalTo(new LocalDate(2015, 3, 1) ) );

        Document sourceDom = parseAsDom("/issues/issue-eisi-294-cii.xml");
        Document targetDom = parseAsDom(conversionResult);

        XPathExpression dateTimeStringXpath = ciiXpath().compile(" string( //ram:InvoiceReferencedDocument/ram:FormattedIssueDateTime/qdt:DateTimeString/text() ) ");
        assertEquals(errMsg, dateTimeStringXpath.evaluate(sourceDom), dateTimeStringXpath.evaluate(targetDom));

        XPathExpression dateTimeStringFormatXpath = ciiXpath().compile(" string( //ram:InvoiceReferencedDocument/ram:FormattedIssueDateTime/qdt:DateTimeString/@format ) ");
        assertEquals(errMsg, dateTimeStringFormatXpath.evaluate(sourceDom), dateTimeStringFormatXpath.evaluate(targetDom));

    }

    @Test
    public void issueEisi285() throws Exception {

        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi285-cii.xml",
                "cii", "cii", ignoreAll());

        String errMsg = describeIntermediateInvoice(conversionResult) + "\n=======\n" + describeConvertedInvoice(conversionResult);

        // <ram:AdditionalReferencedDocument>
        //    <ram:IssuerAssignedID>BT-18 Invoice object id</ram:IssuerAssignedID><!--BT-18-->
        //    <ram:TypeCode>130</ram:TypeCode><!--BT-18 fixed value 130-->
        //    <ram:ReferenceTypeCode>ZZZ</ram:ReferenceTypeCode><!--BT-18-1-->
        // </ram:AdditionalReferencedDocument>
        //
        // TypeCode == 130 => BT-18
        // IssuerAssignedID -> BT-18.identifier
        // ReferenceTypeCode -> BT-18.schemaIdentifier
        Identifier bt18 = conversionResult.getCenInvoice().getBT0018InvoicedObjectIdentifierAndSchemeIdentifier(0).getValue();
        assertThat( bt18.getIdentifier(), equalTo("BT-18 Invoice object id") );
        assertThat( bt18.getIdentificationSchema(), equalTo("ZZZ") );
        assertThat( bt18.getSchemaVersion(), nullValue() );

        long countBt18InBt122 = conversionResult.getCenInvoice()
                .getBG0024AdditionalSupportingDocuments()
                .stream()
                .map(bg24 -> bg24.getBT0122SupportingDocumentReference())
                .flatMap(Collection::stream)
                .peek(bt122 -> System.out.println(bt122.getValue()))
                .filter(bt122 -> bt122.getValue().equals("BT-18 Invoice object id"))
                .count();

        assertThat("BT-18 is not supposed to be duplicated in bt122s.", countBt18InBt122, equalTo(0L));

        Document targetCii = parseAsDom(conversionResult);

        assertThat(
                errMsg,
                ciiXpath().compile("count(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument/ram:IssuerAssignedID[text()='BT-18 Invoice object id'])").evaluate(targetCii),
                equalTo("1") );
        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[3]/ram:IssuerAssignedID/text()").evaluate(targetCii),
                equalTo("BT-18 Invoice object id") );
        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[3]/ram:ReferenceTypeCode/text()").evaluate(targetCii),
                equalTo("ZZZ") );
        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[3]/ram:TypeCode/text()").evaluate(targetCii),
                equalTo("130") );

    }



    @Test
    public void issueEisi286() throws Exception {

        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-286-cii.xml",
                "cii", "cii", ignoreAll());

        Document targetCii = parseAsDom(conversionResult);
        String errMsg = describeConvertedInvoice(conversionResult);

        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject/ram:ID/text()").evaluate(targetCii),
                equalTo("ContractCUPID") );
        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SpecifiedProcuringProject/ram:Name/text()").evaluate(targetCii),
                equalTo("Project reference") );

    }

    @Test
    public void issueEisi283() throws Exception {

        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-283-cii.xml",
                "cii", "cii", ignoreAll());

        Document targetCii = parseAsDom(conversionResult);
        String errMsg = describeConvertedInvoice(conversionResult);

        assertThat(
                errMsg,
                ciiXpath().compile("(//ram:AdditionalReferencedDocument)[2]/ram:IssuerAssignedID/text()").evaluate(targetCii),
                equalTo("EeISI.csv") );
        assertThat(
                errMsg,
                ciiXpath().compile("count( (//ram:AdditionalReferencedDocument)[2]/ram:IssuerAssignedID/ram:ReferenceTypeCode )").evaluate(targetCii),
                equalTo("0") );

    }

    @Test
    public void issueEisi284() throws Exception {

        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-284-cii.xml",
                "cii", "cii", ignoreAll());

        BG0000Invoice cenInvoice = conversionResult.getCenInvoice();

        BT0017TenderOrLotReference bt17 = cenInvoice.getBT0017TenderOrLotReference(0);
        assertThat( describeIntermediateInvoice(cenInvoice),  bt17.getValue(), equalTo("ContractCIGID") );

        assertThat( cenInvoice.getBG0024AdditionalSupportingDocuments(1).getBT0122SupportingDocumentReference(0).getValue(), equalTo("ContractCIGID") );

        Document targetCii = parseAsDom(conversionResult);
        String errMsg = describeConvertedInvoice(conversionResult);

        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[2]/ram:IssuerAssignedID/text()").evaluate(targetCii),
                equalTo("ContractCIGID") );
        assertThat(
                errMsg,
                ciiXpath().compile("/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[2]/ram:TypeCode/text()").evaluate(targetCii),
                equalTo("50") );
        assertThat(
                errMsg,
                ciiXpath().compile("count(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:AdditionalReferencedDocument[2]/ram:ReferenceTypeCode)").evaluate(targetCii),
                equalTo("0") );

    }

    @Test
    public void issueEisi287() throws Exception {

        // given
        Document sourceDom = parseAsDom("/issues/issue-eisi-287-cii.xml");

        // when
        ImprovedConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-287-cii.xml",
                "cii", "cii", ignoreAll());

        Document targetDom = parseAsDom(conversionResult);

        String msg = describeIntermediateInvoice(conversionResult) + "\n\n" + describeConvertedInvoice(conversionResult);

        // then
        Identifier bt71 = evalExpression(() -> conversionResult.getCenInvoice().getBG0013DeliveryInformation(0).getBT0071DeliverToLocationIdentifierAndSchemeIdentifier(0).getValue());
        assertThat( bt71.getIdentifier(), equalTo("6754238987648") );
        assertThat( bt71.getIdentificationSchema(), equalTo("0095") );
        assertThat( bt71.getSchemaVersion(), nullValue() );


        Node idNode = (Node) ciiXpath().compile("(//ram:ShipToTradeParty)[1]/ram:GlobalID").evaluate(targetDom, XPathConstants.NODE);
        assertNotNull( describeConvertedInvoice(conversionResult), idNode);
        assertTrue( describeConvertedInvoice(conversionResult), idNode.hasAttributes());

        XPathExpression globalIdXpath = ciiXpath().compile("(//ram:ShipToTradeParty)[1]/ram:GlobalID/text()");
        assertEquals(msg, globalIdXpath.evaluate(sourceDom), globalIdXpath.evaluate(targetDom) );

        XPathExpression schemeIDPath = ciiXpath().compile("string( (//ram:ShipToTradeParty)[1]/ram:GlobalID/@schemeID )");
        assertEquals(msg, schemeIDPath.evaluate(sourceDom), schemeIDPath.evaluate(targetDom) );

    }

    @Test
    public void issueEisi292() throws Exception {

        // when
        ConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-292-cii.xml",
                "cii", "cii", ignoreAll());

        Document dom = parseAsDom(conversionResult);

        String reasonCode = ciiXpath().compile("(//*[local-name() = 'SpecifiedTradeAllowanceCharge'])[3]/*[local-name() = 'ReasonCode']/text()").evaluate(dom);

        assertThat( describeConvertedInvoice(conversionResult), reasonCode, notNullValue());
        assertThat( describeConvertedInvoice(conversionResult), reasonCode, equalTo("66") );

    }



    @Test
    public void issueEeisi205() throws Exception {

        // given
        XPathExpression xPathBt46 = ublXpath().compile("//BT-46");

        // when
        ConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi205-fattpa.xml",
                "fatturapa", "xmlcen", keepErrorsNotWarnings());

        ConversionResult<byte[]> conversionResult1 = conversionResult;
        Document convertedInvoice = parseAsDom(conversionResult1);

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
        ConversionResult<byte[]> conversionResult = conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi216-ubl.xml",
                "ubl", "ubl", keepErrorsNotWarnings());
        convertedInvoice = parseAsDom(conversionResult);

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
        conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi195-fattpa-noDK.xml",
                "fatturapa", "peppolcn", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi191() {
        conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi191-cii.xml",
                "cii", "xmlcen", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi188() {
        conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-188-xmlcen.xml",
                "xmlcen", "ubl", keepErrorsNotWarnings());
    }

    @Test
    public void issueEeisi248() {
        conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi-248-xmlcen_.xml",
                "xmlcen", "ubl", keepErrorsNotWarnings());
    }

    @Test
    public void issueEeisi192() {
        conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi192-fattpa.xml",
                "fatturapa", "ubl", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi193a() {

        conversion.assertConversionWithoutErrors(
                "/issues/issue-eeisi193-fattpa.xml",
                "fatturapa", "fatturapa", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi193b() {

        conversion.assertConversionWithoutErrors(
                "/issues/issue-eisi193b-fattpa.xml",
                "fatturapa", "fatturapa", keepErrorsNotWarnings());

    }

    @Test
    public void issueEeisi7() throws Exception {

        // when
        ConversionResult<byte[]> conversion = AbstractIssueTest.conversion.assertConversionWithoutErrors(
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
        ConversionResult<byte[]> conversion = AbstractIssueTest.conversion.assertConversionWithoutErrors(
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
        ConversionResult<byte[]> conversion = AbstractIssueTest.conversion.assertConversionWithoutErrors("/issues/issue-eeisi22-ubl.xml", "ubl", "fatturapa");

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

        conversion.assertConversionWithoutErrors("/issues/issue-eisi-20-cii.xml", "cii", "fatturapa");

    }

    private String errorMessage(ImprovedConversionResult<byte[]> conversionResult) {
        return describeIntermediateInvoice(conversionResult) + "\n=======\n" + describeConvertedInvoice(conversionResult);
    }

}
