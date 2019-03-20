package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.PlainXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class StaticXsdTest {

    @Test
    public void shoudLoadTheStaticXsdVersion() throws SAXException {
        XSDValidator xsdValidator = new PlainXSDValidator(FileUtils.getFile("../converter-commons/src/main/resources/converterdata/converter-commons/fattpa/xsdstatic/Schema_del_file_xml_FatturaPA_versione_1.2.xsd"), ErrorCode.Location.FATTPA_OUT);
        List<IConversionIssue> issues = xsdValidator.validate("<xml>bad xml</xml>".getBytes());
        assertThat(issues, not(empty()));
    }

}
