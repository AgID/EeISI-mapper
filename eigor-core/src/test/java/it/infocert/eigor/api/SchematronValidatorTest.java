package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.org.springframework.core.io.FileSystemResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class SchematronValidatorTest {

    private SchematronValidator schematronValidator;
    private byte[] sampleXml;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {

        String schFileName = "dogs.sch";
        FileUtils.copyInputStreamToFile( getClass().getResourceAsStream("/dogs/" + schFileName), tmp.newFile(schFileName) );
        File schematron = new File( tmp.getRoot(), schFileName);
        schematronValidator = new SchematronValidator(
                new FileSystemResource( schematron ), false, false, ErrorCode.Location.CII_OUT);
        sampleXml = IOUtils.toByteArray( getClass().getResource("/dogs/dogs.xml") );
    }

    @Test
    public void shouldNotFailParsingSchematron() {
        List<IConversionIssue> issueList = schematronValidator.validate(sampleXml);
        for (IConversionIssue issue : issueList) {
            assertFalse(issue.getErrorMessage().getMessage().contains("Schematron parsing failed."));
        }
    }
}
