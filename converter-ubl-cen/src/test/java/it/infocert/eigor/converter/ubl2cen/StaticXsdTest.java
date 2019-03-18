package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.PlainXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class StaticXsdTest {

    XSDValidator sut;

    @Before
    public void setUp() throws SAXException {
        sut = new PlainXSDValidator(FileUtils.getFile("../converter-commons/src/main/resources/converterdata/converter-commons/ubl/xsdstatic/UBL-Invoice-2.1.xsd"),
                ErrorCode.Location.UBL_IN);
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
