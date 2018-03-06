package it.infocert.eigor.converter.ublcn2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.XSDValidator;
import org.apache.commons.io.FileUtils;
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
        XSDValidator xsdValidator = new XSDValidator(FileUtils.getFile("../converter-commons/src/main/resources/converterdata/converter-commons/ublcn/xsd/UBL-CreditNote-2.1.xsd"), ErrorCode.Location.UBLCN_IN);
        List<IConversionIssue> issues = xsdValidator.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

    @Test
    public void shoudLoadTheStaticXsdVersion() throws SAXException {
        XSDValidator xsdValidator = new XSDValidator(FileUtils.getFile("../converter-commons/src/main/resources/converterdata/converter-commons/ublcn/xsd/UBL-CreditNote-2.1.xsd"), ErrorCode.Location.UBLCN_IN);
        List<IConversionIssue> issues = xsdValidator.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

}
