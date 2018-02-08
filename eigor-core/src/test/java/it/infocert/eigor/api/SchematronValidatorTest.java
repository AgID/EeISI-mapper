package it.infocert.eigor.api;

import it.infocert.eigor.api.errors.ErrorCode;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class SchematronValidatorTest {

    private SchematronValidator schematronValidator;
    private byte[] sampleXml;

    @Before
    public void setUp() throws Exception {
        schematronValidator = new SchematronValidator(new File("src/test/resources/validator/dogs/dogs.sch"), false, ErrorCode.Location.CII_OUT);
        sampleXml = Files.readAllBytes(Paths.get("src/test/resources/validator/dogs/dogs.xml"));
    }

    @Test
    public void shouldNotFailParsingSchematron() {
        List<IConversionIssue> issueList = schematronValidator.validate(sampleXml);
        for (IConversionIssue issue : issueList) {
            assertFalse(issue.getErrorMessage().getMessage().contains("Schematron parsing failed."));
        }
    }
}