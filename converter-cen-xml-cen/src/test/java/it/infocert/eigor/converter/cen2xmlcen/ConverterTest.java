package it.infocert.eigor.converter.cen2xmlcen;


import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConverterTest {

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
    public void shouldSupportXmlCenExtension() {

        assertThat( sut.extension(), equalTo("xmlcen") );

    }

    @Test
    public void shouldSupportXmlCenAsFormat() {

        assertTrue( sut.support("xmlcen") );
        assertFalse( sut.support("xxx") );

    }

    @Test
    public void shouldConvertAnInvoice() throws SyntaxErrorInInvoiceFormatException, JDOMException, IOException {

        // given
        BG0000Invoice invoice = aCenInvoice();

        // when
        byte[] result = sut
                .convert(invoice)
                .getResult();

        System.out.println( new java.lang.String(result, "UTF-8"));

        Document cenXml = xmlToDom(
                result
        );

        assertThat( selectOneElement(cenXml, "/bg-0").getText().trim(), equalTo(""));
        assertThat( selectOneElement(cenXml, "/bg-0/bt-1").getText().trim(), equalTo("12345"));

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

    private BG0000Invoice aCenInvoice() {
        BG0000Invoice bg0000Invoice = new BG0000Invoice();
        bg0000Invoice.getBT0001InvoiceNumber().add( new BT0001InvoiceNumber("12345") );
        BG0004Seller bg004Seller = new BG0004Seller();
        bg004Seller.getBT0027SellerName().add(new BT0027SellerName("Jonh"));
        bg004Seller.getBT0031SellerVatIdentifier().add(new BT0031SellerVatIdentifier("vatIdentifier"));
        bg0000Invoice.getBG0004Seller().add(bg004Seller);

        return bg0000Invoice;
    }

}
