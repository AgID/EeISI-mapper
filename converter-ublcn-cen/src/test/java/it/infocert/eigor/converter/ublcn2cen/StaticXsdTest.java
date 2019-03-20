package it.infocert.eigor.converter.ublcn2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.PlainXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.util.List;

import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class StaticXsdTest {

    static XSDValidator sut;

    @BeforeClass
    public static void setUp() throws SAXException {
        sut = new PlainXSDValidator(
                getFile("../converter-commons/src/main/resources/converterdata/converter-commons/ublcn/xsdstatic/UBL-CreditNote-2.1.xsd"),
                ErrorCode.Location.UBLCN_IN);
    }

    @Test
    public void shoudLoadTheOnlineXsdVersion() throws SAXException {
        List<IConversionIssue> issues = sut.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

    @Test
    public void shoudLoadTheStaticXsdVersion() throws SAXException {
        List<IConversionIssue> issues = sut.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

}
