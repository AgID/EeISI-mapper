package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.XSDValidator;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class StaticXsdTest {

    @Test
    public void shoudLoadTheOnlineXsdVersion() throws SAXException {
        XSDValidator xsdValidator = new XSDValidator(new File(this.getClass().getResource("/converterdata/converter-ubl-cen/ubl/xsd/UBL-Invoice-2.1.xsd").getFile()), ErrorCode.Location.UBL_IN);
        List<IConversionIssue> issues = xsdValidator.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

    @Test
    public void shoudLoadTheStaticXsdVersion() throws SAXException {
        XSDValidator xsdValidator = new XSDValidator(new File(this.getClass().getResource("/converterdata/converter-ubl-cen/ubl/xsdstatic/UBL-Invoice-2.1.xsd").getFile()), ErrorCode.Location.UBL_IN);
        List<IConversionIssue> issues = xsdValidator.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

}
