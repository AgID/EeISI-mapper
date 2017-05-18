package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.converter.csvcen2cen.CsvCen2Cen;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Cen2FattPATest {

    private CsvCen2Cen csvCen2Cen;
    private Cen2FattPAConverter cen2FattPA;
    private XPathFactory xPathfactory;


    @Before
    public void setUp() {
        csvCen2Cen = new CsvCen2Cen(new Reflections("it.infocert"));
        cen2FattPA = new Cen2FattPAConverter();
        xPathfactory = XPathFactory.newInstance();
    }

    @Test
    public void shouldSupportCsvCen() {
        assertThat(cen2FattPA.support("cenfattpa"), is(true));
        assertThat(cen2FattPA.support("CenFattPA"), is(true));
        assertThat(cen2FattPA.support("xml"), is(false));
    }

    @Test
    public void testVatIdSplitting() {
        String[] testStrings = {
                "RO151590954",
                "IT 41440312",
                "IE123456Z89"};

        String[] expectedCountry = {
                "RO",
                "IT",
                "IE"};

        String[] expectedCodes = {
                "151590954",
                "41440312",
                "123456Z89"};

        for (int i = 0; i < testStrings.length; i++) {
            String testString = testStrings[i];
            assertTrue(expectedCountry[i].equals(Cen2FattPAConverterUtils.getCountryFromVATString(testString)));
            assertTrue(expectedCodes[i].equals(Cen2FattPAConverterUtils.getCodeFromVATString(testString)));
        }
    }

    @Test
    public void testNullOrEmptyVatIdSplitting() {

        assertTrue("".equals(Cen2FattPAConverterUtils.getCountryFromVATString("")));
        assertTrue("".equals(Cen2FattPAConverterUtils.getCodeFromVATString("")));

        assertTrue("".equals(Cen2FattPAConverterUtils.getCountryFromVATString(null)));
        assertTrue("".equals(Cen2FattPAConverterUtils.getCodeFromVATString(null)));
    }

    private String getStringByXPath(Document doc, String xpath) throws XPathExpressionException {
        XPathExpression xPathExpression = xPathfactory.newXPath().compile(xpath);
        return (String) xPathExpression.evaluate(doc, XPathConstants.STRING);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutableConversionResult() {
        ConversionResult cr = new BinaryConversionResult("dummy".getBytes(), new ArrayList<Exception>());
        cr.getErrors().add(new Exception());
    }

    @Test(expected = NullPointerException.class)
    public void testNullErrorsConversionResult() {
        ConversionResult cr = new BinaryConversionResult("dummy".getBytes(), null);
    }
}