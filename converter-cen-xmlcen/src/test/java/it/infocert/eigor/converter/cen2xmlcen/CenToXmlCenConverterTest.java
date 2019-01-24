package it.infocert.eigor.converter.cen2xmlcen;


import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CenToXmlCenConverterTest {

    private static XPathFactory xFactory;

    CenToXmlCenConverter sut;

    @BeforeClass
    static public void setUpFactory() {
        xFactory = XPathFactory.instance();
    }

    @Before
    public void setUpSut() throws ConfigurationException {
        sut = new CenToXmlCenConverter();
        sut.configure();
    }

    @Test
    public void shouldNotProvideAnyRegEx() {

        assertThat( sut.getMappingRegex(), nullValue() );

    }

    @Test
    public void shouldSupportXmlCen() {

        assertThat( sut.getSupportedFormats(), hasItems("xmlcen") );

    }

    @Test
    public void shouldOutputFilesAsXml() {

        assertThat( sut.extension(), equalTo("xml") );

    }

    @Test
    public void shouldSupportXmlCenAsFormat() {

        assertTrue( sut.support("xmlcen") );
        assertFalse( sut.support("xxx") );

    }

    @Test
    public void shouldConvertAnInvoice() throws SyntaxErrorInInvoiceFormatException, JDOMException, IOException {

        // given
        BG0000Invoice invoice = aMinimalCenInvoice();

        // when
        BinaryConversionResult result = sut
                .convert(invoice);

        String xmlString = new String(result.getResult(), "UTF-8") + " " + result.getIssues().toString();

        Document cenXml = xmlToDom(
                result.getResult()
        );

        assertThat( xmlString, selectOneElement(cenXml, "/SEMANTIC-INVOICE").getText().trim(), equalTo(""));
        assertThat( xmlString, selectOneElement(cenXml, "/SEMANTIC-INVOICE/BT-1").getText().trim(), equalTo("12345"));
        assertFalse( xmlString , result.hasIssues() );

    }

    private List<Element> selectElements(Document cenXml, String xpathExpression) {
        XPathExpression<Element> expr = xFactory.compile(xpathExpression, Filters.element());
        return expr.evaluate(cenXml);
    }

    private Element selectOneElement(Document cenXml, String xpathExpression) {
        XPathExpression<Element> expr = xFactory.compile(xpathExpression, Filters.element());
        List<Element> elements = expr.evaluate(cenXml);
        assertThat( "Wrong size", elements.size(), equalTo(1) );
        return elements.get(0);
    }

    private Document xmlToDom(byte[] xml) throws JDOMException, IOException {
        // the SAXBuilder is the easiest way to create the JDOM2 objects.
        SAXBuilder jdomBuilder = new SAXBuilder();

        // jdomDocument is the JDOM2 Object
        return jdomBuilder.build( new ByteArrayInputStream( xml ));
    }

    private BG0000Invoice aMinimalCenInvoice() {

        Identifier fullIdentifier = new Identifier("schema", "version", "identifier");
        Identifier incompleteIdentifier = new Identifier("identifier");

        Calendar instance = GregorianCalendar.getInstance();
        instance.set(Calendar.DATE, 22);
        instance.set(Calendar.MONTH, Calendar.MARCH);
        instance.set(Calendar.YEAR, 2007);
        LocalDate localDate = LocalDate.fromCalendarFields( instance );

        BG0002ProcessControl bg0002 = new BG0002ProcessControl();
        bg0002.getBT0023BusinessProcessType().add(new BT0023BusinessProcessType("bt23"));
        bg0002.getBT0024SpecificationIdentifier().add( new BT0024SpecificationIdentifier("spec") );

        BG0003PrecedingInvoiceReference bg3 = new BG0003PrecedingInvoiceReference();
        bg3.getBT0025PrecedingInvoiceReference().add(new BT0025PrecedingInvoiceReference("bt25"));
        bg3.getBT0026PrecedingInvoiceIssueDate().add(new BT0026PrecedingInvoiceIssueDate(localDate));

        BG0005SellerPostalAddress bg05 = new BG0005SellerPostalAddress();
        bg05.getBT0035SellerAddressLine1().add(new BT0035SellerAddressLine1("bt35"));
        bg05.getBT0036SellerAddressLine2().add(new BT0036SellerAddressLine2("bt36"));
        bg05.getBT0037SellerCity().add(new BT0037SellerCity("bt37"));
        bg05.getBT0038SellerPostCode().add(new BT0038SellerPostCode("bt38"));
        bg05.getBT0039SellerCountrySubdivision().add(new BT0039SellerCountrySubdivision("bt39"));
        bg05.getBT0040SellerCountryCode().add(new BT0040SellerCountryCode(Iso31661CountryCodes.DK));
        bg05.getBT0162SellerAddressLine3().add( new BT0162SellerAddressLine3("bt162") );

        BG0006SellerContact bg06 = new BG0006SellerContact();
        bg06.getBT0041SellerContactPoint().add(new BT0041SellerContactPoint("bt41"));
        bg06.getBT0042SellerContactTelephoneNumber().add(new BT0042SellerContactTelephoneNumber("bt42"));
        bg06.getBT0043SellerContactEmailAddress().add(new BT0043SellerContactEmailAddress("a@a.com"));

        BG0004Seller bg004Seller = new BG0004Seller();
        bg004Seller.getBT0027SellerName().add(new BT0027SellerName("Jonh"));
        bg004Seller.getBT0028SellerTradingName().add(new BT0028SellerTradingName("bt28"));
        bg004Seller.getBT0031SellerVatIdentifier().add(new BT0031SellerVatIdentifier("vatIdentifier"));
        bg004Seller.getBT0032SellerTaxRegistrationIdentifier().add( new BT0032SellerTaxRegistrationIdentifier("identifier") );
        bg004Seller.getBT0033SellerAdditionalLegalInformation().add( new BT0033SellerAdditionalLegalInformation( "legalInfo" ));

        bg004Seller.getBT0034SellerElectronicAddressAndSchemeIdentifier().add( new BT0034SellerElectronicAddressAndSchemeIdentifier(incompleteIdentifier) );
        bg004Seller.getBG0005SellerPostalAddress().add(bg05);
        bg004Seller.getBG0006SellerContact().add(bg06);

        BG0008BuyerPostalAddress bg0008 = new BG0008BuyerPostalAddress();
        bg0008.getBT0050BuyerAddressLine1().add( new BT0050BuyerAddressLine1("line1") );
        bg0008.getBT0051BuyerAddressLine2().add( new BT0051BuyerAddressLine2("line2") );
        bg0008.getBT0052BuyerCity().add( new BT0052BuyerCity("line3") );
        bg0008.getBT0053BuyerPostCode().add( new BT0053BuyerPostCode("bt53") );
        bg0008.getBT0054BuyerCountrySubdivision().add( new BT0054BuyerCountrySubdivision("bt54") );
        bg0008.getBT0055BuyerCountryCode().add( new BT0055BuyerCountryCode(Iso31661CountryCodes.DZ) );
        bg0008.getBT0163BuyerAddressLine3().add( new BT0163BuyerAddressLine3("bt163") );

        BG0007Buyer bg007Buyer = new BG0007Buyer();
        bg007Buyer.getBT0044BuyerName().add(new BT0044BuyerName("name"));
        bg007Buyer.getBT0045BuyerTradingName().add(new BT0045BuyerTradingName("bt45"));
        bg007Buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().add(new BT0046BuyerIdentifierAndSchemeIdentifier( incompleteIdentifier ));
        bg007Buyer.getBG0008BuyerPostalAddress().add(bg0008);

        BG0022DocumentTotals bg22 = new BG0022DocumentTotals();
        bg22.getBT0106SumOfInvoiceLineNetAmount().add(new BT0106SumOfInvoiceLineNetAmount(new BigDecimal(1.0)));
        bg22.getBT0109InvoiceTotalAmountWithoutVat().add(new BT0109InvoiceTotalAmountWithoutVat(new BigDecimal(1.0)));
        bg22.getBT0112InvoiceTotalAmountWithVat().add(new BT0112InvoiceTotalAmountWithVat(new BigDecimal(1.0)));
        bg22.getBT0115AmountDueForPayment().add(new BT0115AmountDueForPayment(new BigDecimal(1.0)));

        BG0023VatBreakdown bg23 = new BG0023VatBreakdown();
        bg23.getBT0116VatCategoryTaxableAmount().add(new BT0116VatCategoryTaxableAmount(new BigDecimal(1.0)) );
        bg23.getBT0117VatCategoryTaxAmount().add(new BT0117VatCategoryTaxAmount(new BigDecimal(1.0)) );
        bg23.getBT0118VatCategoryCode().add(new BT0118VatCategoryCode(Untdid5305DutyTaxFeeCategories.E) );

        BG0029PriceDetails bg29 = new BG0029PriceDetails();
        bg29.getBT0146ItemNetPrice().add(new BT0146ItemNetPrice(new BigDecimal(1.0)));

        BG0030LineVatInformation bg30 = new BG0030LineVatInformation();
        bg30.getBT0151InvoicedItemVatCategoryCode().add(new BT0151InvoicedItemVatCategoryCode(Untdid5305DutyTaxFeeCategories.E) );

        BG0031ItemInformation bg31 = new BG0031ItemInformation();
        bg31.getBT0153ItemName().add(new BT0153ItemName("bt153"));

        BG0025InvoiceLine bg25 = new BG0025InvoiceLine();
        bg25.getBT0126InvoiceLineIdentifier().add( new BT0126InvoiceLineIdentifier("id") );
        bg25.getBT0129InvoicedQuantity().add( new BT0129InvoicedQuantity(new BigDecimal(1.0)) );
        bg25.getBT0130InvoicedQuantityUnitOfMeasureCode().add( new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.TOTE_TE) );
        bg25.getBT0131InvoiceLineNetAmount().add( new BT0131InvoiceLineNetAmount(new BigDecimal(1.0)) );
        bg25.getBG0029PriceDetails().add(bg29);
        bg25.getBG0030LineVatInformation().add(bg30);
        bg25.getBG0031ItemInformation().add(bg31);

        BG0000Invoice bg0000Invoice = new BG0000Invoice();
        bg0000Invoice.getBT0001InvoiceNumber().add( new BT0001InvoiceNumber("12345") );
        bg0000Invoice.getBT0002InvoiceIssueDate().add( new BT0002InvoiceIssueDate(localDate) );
        bg0000Invoice.getBT0003InvoiceTypeCode().add(new BT0003InvoiceTypeCode(Untdid1001InvoiceTypeCode.Code381));
        bg0000Invoice.getBT0005InvoiceCurrencyCode().add( new BT0005InvoiceCurrencyCode(Iso4217CurrenciesFundsCodes.EUR) );
        bg0000Invoice.getBT0006VatAccountingCurrencyCode().add( new BT0006VatAccountingCurrencyCode(Iso4217CurrenciesFundsCodes.EUR) );
        bg0000Invoice.getBT0007ValueAddedTaxPointDate().add( new BT0007ValueAddedTaxPointDate(localDate) );
        bg0000Invoice.getBT0008ValueAddedTaxPointDateCode().add( new BT0008ValueAddedTaxPointDateCode(Untdid2005DateTimePeriodQualifiers.Code432) );
        bg0000Invoice.getBT0009PaymentDueDate().add( new BT0009PaymentDueDate(localDate) );
        bg0000Invoice.getBT0010BuyerReference().add( new BT0010BuyerReference("buyer reference") );
        bg0000Invoice.getBT0011ProjectReference().add( new BT0011ProjectReference("bt11") );
        bg0000Invoice.getBT0012ContractReference().add(new BT0012ContractReference("bt12"));
        bg0000Invoice.getBT0013PurchaseOrderReference().add(new BT0013PurchaseOrderReference("bt13"));
        bg0000Invoice.getBT0014SalesOrderReference().add(new BT0014SalesOrderReference("bt14"));
        bg0000Invoice.getBT0015ReceivingAdviceReference().add(new BT0015ReceivingAdviceReference("bt15"));
        bg0000Invoice.getBT0016DespatchAdviceReference().add(new BT0016DespatchAdviceReference("bt16"));
        bg0000Invoice.getBT0017TenderOrLotReference().add(new BT0017TenderOrLotReference("bt17"));
        bg0000Invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add(new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(new Identifier("sch", "ver", "id")));
        bg0000Invoice.getBT0019BuyerAccountingReference().add(new BT0019BuyerAccountingReference("bt19"));
        bg0000Invoice.getBT0020PaymentTerms().add(new BT0020PaymentTerms("bt20"));

        bg0000Invoice.getBG0002ProcessControl().add(bg0002);
        bg0000Invoice.getBG0003PrecedingInvoiceReference().add(bg3);
        bg0000Invoice.getBG0004Seller().add(bg004Seller);
        bg0000Invoice.getBG0007Buyer().add(bg007Buyer);
        bg0000Invoice.getBG0022DocumentTotals().add(bg22);
        bg0000Invoice.getBG0023VatBreakdown().add(bg23);
        bg0000Invoice.getBG0025InvoiceLine().add(bg25);

        return bg0000Invoice;
    }

}
